package dvoraka.archbuilder.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.service.DirService;
import dvoraka.archbuilder.util.ByteClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Objects.requireNonNull;

@Service
public class JavaGenerator implements LangGenerator, JavaHelper {

    private final DirService dirService;

    private final Logger log = LoggerFactory.getLogger(JavaGenerator.class);

    private final EnumMap<DirType, Consumer<Directory>> conf;
    private final HashSet<Directory> processedDirs;


    @Autowired
    public JavaGenerator(DirService dirService) {
        this.dirService = requireNonNull(dirService);

        conf = new EnumMap<>(DirType.class);
        conf.put(DirType.IMPL, this::genImplSafe);
        conf.put(DirType.SERVICE, this::genServiceSafe);
        conf.put(DirType.SERVICE_IMPL, this::genServiceImplSafe);
        conf.put(DirType.SRC_PROPERTIES, this::genSrcProps);
        conf.put(DirType.SRC_ROOT, this::processSrcRoot);

        checkImplementation();
        processedDirs = new HashSet<>();
    }

    private void checkImplementation() {
        for (DirType type : DirType.values()) {
            if (!conf.containsKey(type)) {
                log.debug("Type not implemented: {}", type.toString());
            }
        }
    }

    @Override
    public void generate(Directory directory) {
        if (processedDirs.contains(directory)) {
            log.debug("Already processed: {}", directory.getType());
            return;
        }

        log.debug("Generating code for: {}", directory.getType());
        processedDirs.add(directory);

        if (conf.containsKey(directory.getType())) {
            conf.get(directory.getType()).accept(directory);
        }
    }

    private void processSrcRoot(Directory directory) {
        addClassPath(Paths.get(directory.getPath()));
    }

    private void genImplSafe(Directory directory) {
        try {
            genImpl(directory);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void genImpl(Directory directory) throws ClassNotFoundException {
        log.debug("Generating implementation: {}", directory);

        Class<?> superType = loadClass(directory.getSuperType()
                .orElseThrow(this::noSuperTypeException)
                .getTypeName());

        // check type parameters
        Map<TypeVariable, Type> typeMapping;
        if (superType.getTypeParameters().length == 0) {
            typeMapping = new HashMap<>();
        } else {
            typeMapping = getTypeVarMapping(directory, superType.getTypeParameters());
        }

        List<Method> allMethods;
        if (directory.isAbstractType()) {
            allMethods = Collections.emptyList();
        } else {
            allMethods = findMethods(superType);
        }

        List<MethodSpec> methodSpecs = genMethodSpecs(allMethods, typeMapping);

        String name = directory.getFilename()
                .orElse(superType.getSimpleName() + "Impl");

        TypeSpec implementation;
        TypeSpec.Builder implementationBuilder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodSpecs);

        if (superType.isInterface()) {

            if (superType.getTypeParameters().length == 0) {
                implementationBuilder = implementationBuilder.addSuperinterface(superType);
            } else { // parametrized type
                TypeVariable<? extends Class<?>>[] typeParameters = superType.getTypeParameters();

                // load parameter types
                List<Type> types = new ArrayList<>();
                for (TypeVariable<? extends Class<?>> typeVariable : typeParameters) {
                    Type realType = typeMapping.get(typeVariable);
                    types.add(realType);
                }

                ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName
                        .get(superType, types.toArray(new Type[0]));

                implementationBuilder = implementationBuilder.addSuperinterface(parameterizedTypeName);
            }
        } else { // supertype is class
            implementationBuilder = implementationBuilder
                    .superclass(superType);
        }

        // abstract implementation
        if (directory.isAbstractType()) {
            implementationBuilder = implementationBuilder
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

            // we need to add type variables
            if (directory.getParameters().isEmpty()) {
                implementationBuilder
                        .addTypeVariables(Stream.of(superType.getTypeParameters())
                                .map(TypeVariableName::get)
                                .collect(Collectors.toList()));
            }
        } else {
            implementationBuilder = implementationBuilder
                    .addModifiers(Modifier.PUBLIC);
        }

        implementation = implementationBuilder.build();

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), implementation)
                .build();

        try {
            save(directory, javaFile.toString(), javaSuffix(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genServiceSafe(Directory directory) {
        try {
            genService(directory);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void genService(Directory directory) throws ClassNotFoundException {
        log.debug("Generating service: {}", directory);

        String interfaceName = directory.getFilename()
                .orElseThrow(() -> new RuntimeException("No service name!"));

        // find superinterface
        Optional<Directory> superDir = directory.getSuperType();

        TypeSpec serviceInterface;
        if (superDir.isPresent()) {

            Class<?> superClass = loadClass(superDir.get().getTypeName());
            if (superClass.getTypeParameters().length == 0) {
                serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(superClass)
                        .build();
            } else {
                TypeVariable<? extends Class<?>>[] typeParameters = superClass.getTypeParameters();
                Map<TypeVariable, Type> typeMapping = getTypeVarMapping(directory, typeParameters);

                // load parameter types
                List<Type> types = new ArrayList<>();
                for (TypeVariable<? extends Class<?>> typeVariable : typeParameters) {
                    Type realType = typeMapping.get(typeVariable);
                    types.add(realType);
                }

                ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName
                        .get(superClass, types.toArray(new Class[0]));

//                List<TypeVariableName> typeVariableNames = new ArrayList<>();
//                for (TypeName typeArgument : parameterizedTypeName.typeArguments) {
//                    typeVariableNames.add(TypeVariableName.get(typeArgument.toString()));
//                }

                serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(parameterizedTypeName)
//                        .addTypeVariables(typeVariableNames)
                        .build();
            }

        } else {
            serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                    .addModifiers(Modifier.PUBLIC)
                    .build();
        }

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), serviceInterface)
                .build();

        try {
            save(directory, javaFile.toString(), javaSuffix(interfaceName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genServiceImplSafe(Directory directory) {
        try {
            genServiceImpl(directory);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void genServiceImpl(Directory directory) throws ClassNotFoundException {
        log.debug("Generating service implementation...");

        //TODO: if we don't have a super super type it's OK to continue without it
        Directory superSuperDir = directory.getSuperType()
                .flatMap(Directory::getSuperType)
                .orElseThrow(RuntimeException::new);
        Directory superDir = directory.getSuperType()
                .orElseThrow(this::noSuperTypeException);

        Class<?> superSuperClass = loadClass(superSuperDir.getTypeName());
        Class<?> superClass = loadClass(getClassName(superDir));

        // check parameter count and save type parameters
        Map<TypeVariable, Type> typeMapping;
        Directory paramDir;
        if (directory.getParameters().isEmpty()) {
            paramDir = superDir;
            typeMapping = getTypeVarMapping(paramDir, superSuperClass.getTypeParameters());
        } else {
            paramDir = directory;
            typeMapping = getTypeVarMapping(paramDir, superClass.getTypeParameters());
        }

        // find all methods from the super type
        List<Method> allMethods = findMethods(superClass);

        // process all methods
        List<MethodSpec> methodSpecs = genMethodSpecs(allMethods, typeMapping);

        String name = "Default" + superDir.getFilename()
                .orElse("Service");

        TypeSpec serviceImpl;
        if (superClass.getTypeParameters().length == 0) {
            serviceImpl = TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(superClass)
                    .addAnnotation(Service.class)
                    .addMethods(methodSpecs)
                    .build();
        } else {
            throw new RuntimeException("Super class has type parameters!");
        }

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), serviceImpl)
                .build();

        try {
//            javaFile.writeTo(System.out);
            save(directory, javaFile.toString(), javaSuffix(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<MethodSpec> genMethodSpecs(List<Method> methods, Map<TypeVariable, Type> typeMapping) {

        List<MethodSpec> methodSpecs = new ArrayList<>();

        for (Method method : methods) {
            log.debug("Processing method: {}", method.toGenericString());

            // skip synthetic methods
            if (method.isSynthetic()) {
                log.debug("Skipping synthetic method: {}", method.getName());
                continue;
            }

            // skip private methods
            if (isPrivate(method.getModifiers())) {
                log.debug("Skipping private method: {}", method.getName());
                continue;
            }

            // skip default methods
            if (method.isDefault()) {
                log.debug("Skipping default method: {}", method.getName());
                continue;
            }

            // skip non-abstract methods
            if (!isAbstract(method.getModifiers())) {
                log.debug("Skipping non-abstract method: {}", method.getName());
                continue;
            }

            // skip final methods
            if (isFinal(method.getModifiers())) {
                log.debug("Skipping final method: {}", method.getName());
                continue;
            }

            //TODO
            // type parameters
            TypeVariableName typeVariableName = null;
            if (method.getTypeParameters().length > 0) {
                typeVariableName = TypeVariableName.get(method.getTypeParameters()[0]);
            }

            // return type
            Type returnType = method.getGenericReturnType();
            TypeName returnTypeName;
            if (returnType instanceof TypeVariable) {

                returnTypeName = TypeName.get(typeMapping.get(returnType));

            } else if (returnType instanceof ParameterizedType) {

                ParameterizedType type = ((ParameterizedType) returnType);
                returnTypeName = resolveParametrizedType(type, typeMapping);

//            } else if (returnType instanceof GenericArrayType) {
//
//                returnTypeName = ArrayTypeName.get(returnType);

            } else {
                returnTypeName = TypeName.get(returnType);
            }

            // return value
            String retValue;
            if (returnType == Void.TYPE) {
                retValue = null;
            } else {
                retValue = getReturnValue(returnType);
            }

            // parameters
            List<ParameterSpec> parameterSpecs = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {

                ParameterSpec parameterSpec;
                if (parameter.getParameterizedType() instanceof TypeVariable) {

                    TypeVariable typeVar = ((TypeVariable) parameter.getParameterizedType());
                    Type realType = typeMapping.get(typeVar);
                    parameterSpec = ParameterSpec.builder(realType, parameter.getName())
                            .build();
                } else if (parameter.getParameterizedType() instanceof ParameterizedType) {

                    ParameterizedTypeName parameterizedTypeName = resolveParametrizedType(
                            ((ParameterizedType) parameter.getParameterizedType()),
                            typeMapping
                    );
                    parameterSpec = ParameterSpec.builder(parameterizedTypeName, parameter.getName())
                            .build();
                } else {

                    parameterSpec = ParameterSpec.builder(parameter.getParameterizedType(), parameter.getName())
                            .build();
                }

                parameterSpecs.add(parameterSpec);
            }

            // exceptions
            Type[] exceptions = method.getGenericExceptionTypes();
            List<TypeName> exceptionTypes = new ArrayList<>();
            for (Type type : exceptions) {
                exceptionTypes.add(TypeName.get(type));
            }

            // modifiers
            List<Modifier> modifiers = new ArrayList<>();
            if (isPublic(method.getModifiers())) {
                modifiers.add(Modifier.PUBLIC);
            } else if (isProtected(method.getModifiers())) {
                modifiers.add(Modifier.PROTECTED);
            }

            MethodSpec spec;
            if (retValue == null) {
                spec = MethodSpec.methodBuilder(method.getName())
                        .addAnnotation(Override.class)
                        .returns(returnTypeName)
                        .addModifiers(modifiers)
                        .addParameters(parameterSpecs)
                        .addExceptions(exceptionTypes)
                        .build();
            } else {
                MethodSpec.Builder builder = MethodSpec.methodBuilder(method.getName())
                        .addAnnotation(Override.class)
                        .returns(returnTypeName)
                        .addModifiers(modifiers)
                        .addParameters(parameterSpecs)
                        .addExceptions(exceptionTypes)
                        .addStatement("return " + retValue);

                if (typeVariableName != null) {
                    builder.addTypeVariable(typeVariableName);
                }

                spec = builder.build();
            }

            methodSpecs.add(spec);
        }

        return methodSpecs;
    }

    private ParameterizedTypeName resolveParametrizedType(
            ParameterizedType type,
            Map<TypeVariable, Type> varTypeMapping
    ) {
        Class<?> rawClass = (Class) type.getRawType();

        Type[] actualTypeArguments = type.getActualTypeArguments();
        List<TypeName> typeNames = new ArrayList<>();
        for (Type actualTypeArgument : actualTypeArguments) {

            if (actualTypeArgument instanceof TypeVariable) {

                TypeName typeVariableName = TypeName.get(varTypeMapping.get(actualTypeArgument));
                typeNames.add(typeVariableName);

            } else if (actualTypeArgument instanceof ParameterizedType) {

                TypeName paramTypeName = resolveParametrizedType(((ParameterizedType) actualTypeArgument), varTypeMapping);
                typeNames.add(paramTypeName);

            } else if (actualTypeArgument instanceof WildcardType) {

                WildcardType wildcardType = ((WildcardType) actualTypeArgument);
                if (wildcardType.getUpperBounds().length > 0) {

                    Type[] upperBoundsTypes = wildcardType.getUpperBounds();
                    Type upperBoundsType = upperBoundsTypes[0]; // first type only now

                    if (upperBoundsType instanceof TypeVariable) {

                        Type superType = varTypeMapping.get(upperBoundsType);
                        WildcardTypeName wildcardTypeName = WildcardTypeName.subtypeOf(superType);
                        typeNames.add(wildcardTypeName);

                    } else if (upperBoundsType instanceof ParameterizedType) {

                        //TODO

                    } else { // class
                        WildcardTypeName wildcardTypeName = WildcardTypeName.subtypeOf(upperBoundsType);
                        typeNames.add(wildcardTypeName);
                    }
                }

                if (wildcardType.getLowerBounds().length > 0) {

                    Type[] lowerBoundsTypes = wildcardType.getLowerBounds();
                    Type lowerBoundsType = lowerBoundsTypes[0]; // first type only now

                    if (lowerBoundsType instanceof TypeVariable) {

                        Type superType = varTypeMapping.get(lowerBoundsType);
                        WildcardTypeName wildcardTypeName = WildcardTypeName.supertypeOf(superType);
                        typeNames.add(wildcardTypeName);

                    } else if (lowerBoundsType instanceof ParameterizedType) {

                        //TODO

                    } else { // class
                        WildcardTypeName wildcardTypeName = WildcardTypeName.supertypeOf(lowerBoundsType);
                        typeNames.add(wildcardTypeName);
                    }
                }
            }
        }

        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(rawClass),
                typeNames.toArray(new TypeName[0])
        );

        return parameterizedTypeName;
    }

    private Map<TypeVariable, Type> getTypeVarMapping(
            Directory directory,
            TypeVariable<? extends Class<?>>[] typeVariables
    ) throws ClassNotFoundException {

        // check type variable and parameter counts
        if (typeVariables.length != directory.getParameters().size() && !directory.isAbstractType()) {
            throw new RuntimeException("Bad type parameter count.");
        }

        Map<TypeVariable, Type> typeMapping = new HashMap<>();
        if (directory.isAbstractType() && directory.getParameters().isEmpty()) {
            // just copy type variables
            for (TypeVariable<? extends Class<?>> typeVariable : typeVariables) {
                typeMapping.put(typeVariable, typeVariable);
            }
        } else {
            for (int index = 0; index < typeVariables.length; index++) {

                String className = directory.getParameters().get(index);
                Type type = loadClass(className);

                typeMapping.put(typeVariables[index], type);
            }
        }

        return typeMapping;
    }

    private void genSrcProps(Directory directory) {
        String filename = directory.getFilename()
                .orElseThrow(() -> new RuntimeException("No filename for source properties!"));
        try {
            String text = directory.getText() != null ? directory.getText() : "";
            save(directory, text, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Class<?> loadNonCpClass(Path path) {
        return new ByteClassLoader(this.getClass().getClassLoader()).loadClass(path);
    }

    private void save(Directory directory, String source, String filename) throws IOException {
        Files.write(
                Paths.get(directory.getPath() + File.separator + filename),
                source.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        if (filename.endsWith(".java")) {
            compileSource(getPathString(directory, filename));
        }
    }

    private int compileSource(String pathString) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        log.info("Compiling source: {}...", pathString);

        // build a classpath string for the compiler
        URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
        StringBuilder cp = new StringBuilder();
        for (URL url : urls) {
            cp.append(url.getPath());
            cp.append(File.pathSeparator);
        }

        int exitCode = compiler.run(
                null,
                null,
                null,
                "-cp", cp.toString(),
                pathString
        );

        if (exitCode == 0) {
            log.info("Compilation OK");
        }

        return exitCode;
    }
}
