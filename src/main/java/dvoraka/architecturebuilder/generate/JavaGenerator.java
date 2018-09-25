package dvoraka.architecturebuilder.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import dvoraka.architecturebuilder.DirType;
import dvoraka.architecturebuilder.Directory;
import dvoraka.architecturebuilder.service.DirService;
import dvoraka.architecturebuilder.util.ByteClassLoader;
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

        checkImplementation();
        processedDirs = new HashSet<>();

        addClassPath(Paths.get("rootDir/src/main/java"));
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

    private void genImplSafe(Directory directory) {
        try {
            genImpl(directory);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void genImpl(Directory directory) throws ClassNotFoundException {
        log.debug("Generating implementation {}...", directory.getFilename());

        Class<?> superType = loadClass(directory.getSuperType().getTypeName());

        TypeSpec implementation;
        if (superType.isInterface()) {
            implementation = TypeSpec.classBuilder("TEST")
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(loadClass(""))
//                    .addMethods(methodSpecs)
                    .build();

        } else {
            implementation = TypeSpec.classBuilder("TEST")
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(loadClass(""))
//                    .addMethods(methodSpecs)
                    .build();
        }

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), implementation)
                .build();

        System.out.println();
    }

    private void genServiceSafe(Directory directory) {
        try {
            genService(directory);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void genService(Directory directory) throws ClassNotFoundException {
        log.debug("Generating service...");

        String interfaceName = directory.getFilename();

        // find superinterface
        Optional<Directory> superDir = dirService.findByType(DirType.SERVICE_ABSTRACT, directory);

        TypeSpec serviceInterface;
        if (superDir.isPresent()) {

            Class<?> superClass = loadClass(superDir.get().getFilename());
            if (superClass.getTypeParameters().length == 0) {

                serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(superClass)
                        .build();

            } else {
                // check parameter count and save type parameters
                TypeVariable<? extends Class<?>>[] typeParameters = superClass.getTypeParameters();
                Map<String, Integer> typeMapping = getTypeVarMapping(directory, typeParameters);

                // load parameter types
                List<Type> types = new ArrayList<>();
                for (TypeVariable<? extends Class<?>> typeVariable : typeParameters) {
                    Class<?> clazz = typeVarToClass(typeVariable, typeMapping, directory);
                    types.add(clazz);
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
            javaFile.writeTo(System.out);
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
        Directory superSuperDir = dirService.findByType(DirType.SERVICE_ABSTRACT, directory)
                .orElseThrow(RuntimeException::new);
        Directory superDir = dirService.findByType(DirType.SERVICE, directory)
                .orElseThrow(RuntimeException::new);

        Class<?> superSuperClass = loadClass(superSuperDir.getFilename());
        Class<?> superClass = loadClass(getClassName(superDir));

        // check parameter count and save type parameters
        Map<TypeVariable, Type> typeMapping;
        Directory paramDir;
        if (directory.getParameters().isEmpty()) {
            paramDir = superDir;
            typeMapping = getTypeVarMapping2(paramDir, superSuperClass.getTypeParameters());
        } else {
            paramDir = directory;
            typeMapping = getTypeVarMapping2(paramDir, superClass.getTypeParameters());
        }

        // find all methods from the super super type
        List<Method> allMethods = findMethods(superSuperClass);

        // process all methods
        List<MethodSpec> methodSpecs = genMethodSpecs(allMethods, typeMapping);

        String name = "Default" + superDir.getFilename();

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
            javaFile.writeTo(System.out);
            save(directory, javaFile.toString(), javaSuffix(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<MethodSpec> genMethodSpecs(List<Method> methods, Map<TypeVariable, Type> typeMapping) {

        List<MethodSpec> methodSpecs = new ArrayList<>();

        for (Method method : methods) {
            log.debug("Processing method: {}", method.toGenericString());

            // skip default methods
            if (method.isDefault()) {
                log.debug("Skipping default method: {}", method.getName());
                continue;
            }

            // return type
            Type retType = method.getGenericReturnType();
            TypeName retTypeName;
            if (retType instanceof TypeVariable) {

                retTypeName = TypeName.get(typeMapping.get(retType));

            } else if (retType instanceof ParameterizedType) {

                ParameterizedType type = ((ParameterizedType) retType);
                retTypeName = resolveParamType(type, typeMapping);

            } else {
                retTypeName = TypeName.get(retType);
            }

            // return value
            String retValue;
            if (retType == Void.TYPE) {
                retValue = null;
            } else {
                retValue = getReturnValue(retType);
            }

            // parameters
            List<ParameterSpec> parSpecs = new ArrayList<>();
            Parameter[] params = method.getParameters();
            for (Parameter param : params) {

                ParameterSpec parSpec;
                if (param.getParameterizedType() instanceof TypeVariable) {

                    TypeVariable typeVar = ((TypeVariable) param.getParameterizedType());
                    Type realType = typeMapping.get(typeVar);
                    parSpec = ParameterSpec.builder(realType, param.getName())
                            .build();

                } else if (param.getParameterizedType() instanceof ParameterizedType) {

                    ParameterizedTypeName parameterizedTypeName = resolveParamType(
                            ((ParameterizedType) param.getParameterizedType()),
                            typeMapping
                    );

                    parSpec = ParameterSpec.builder(parameterizedTypeName, param.getName())
                            .build();

                } else {
                    parSpec = ParameterSpec.builder(param.getParameterizedType(), param.getName())
                            .build();
                }

                parSpecs.add(parSpec);
            }

            // exceptions
            Type[] exceptions = method.getGenericExceptionTypes();
            List<TypeName> exceptionTypes = new ArrayList<>();
            for (Type type : exceptions) {
                exceptionTypes.add(TypeName.get(type));
            }

            // modifiers
            List<Modifier> modifiers = new ArrayList<>();
            if ((method.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 1) {
                modifiers.add(Modifier.PUBLIC);
            }

            MethodSpec spec;
            if (retValue == null) {
                spec = MethodSpec.methodBuilder(method.getName())
                        .addAnnotation(Override.class)
                        .returns(retTypeName)
                        .addModifiers(modifiers)
                        .addParameters(parSpecs)
                        .addExceptions(exceptionTypes)
                        .build();
            } else {
                spec = MethodSpec.methodBuilder(method.getName())
                        .addAnnotation(Override.class)
                        .returns(retTypeName)
                        .addModifiers(modifiers)
                        .addParameters(parSpecs)
                        .addExceptions(exceptionTypes)
                        .addStatement("return " + retValue)
                        .build();
            }

            methodSpecs.add(spec);
        }

        return methodSpecs;
    }

    private ParameterizedTypeName resolveParamType(
            ParameterizedType type,
            Map<TypeVariable, Type> mapping2
    ) {
        ParameterizedTypeName parameterizedTypeName;
        Class<?> rawClass = (Class) type.getRawType();

        Type[] actualTypeArguments = type.getActualTypeArguments();
        List<TypeName> typeNames = new ArrayList<>();
        for (Type actualTypeArgument : actualTypeArguments) {

            if (actualTypeArgument instanceof TypeVariable) {

                TypeName typeVariableName = TypeName.get(mapping2.get(actualTypeArgument));
                typeNames.add(typeVariableName);

            } else if (actualTypeArgument instanceof ParameterizedType) {

                TypeName paramTypeName = resolveParamType(((ParameterizedType) actualTypeArgument), mapping2);
                typeNames.add(paramTypeName);

            } else if (actualTypeArgument instanceof WildcardType) {

                WildcardType wildcardType = ((WildcardType) actualTypeArgument);
                if (wildcardType.getUpperBounds().length > 0) {

                    Type upperBoundsType = wildcardType.getUpperBounds()[0];
                    if (upperBoundsType instanceof TypeVariable) {

                        Type superType = mapping2.get(upperBoundsType);
                        WildcardTypeName wildcardTypeName = WildcardTypeName.subtypeOf(superType);
                        typeNames.add(wildcardTypeName);

                    }
                } else {
                    //TODO
                }
            }
        }

        parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(rawClass),
                typeNames.toArray(new TypeName[0])
        );

        return parameterizedTypeName;
    }

    private Class<?> typeVarToClass(TypeVariable typeVariable, Map<String, Integer> mapping, Directory dir)
            throws ClassNotFoundException {

        String varName = typeVariable.getName();
        int index = mapping.get(varName);
        String className = dir.getParameters().get(index);

        return loadClass(className);
    }

    private TypeName typeVarToTypeName(TypeVariable typeVariable, Map<String, Integer> mapping, Directory dir)
            throws ClassNotFoundException {

        Class<?> clazz = typeVarToClass(typeVariable, mapping, dir);

        return TypeName.get(clazz);
    }

    private Map<String, Integer> getTypeVarMapping(
            Directory directory,
            TypeVariable<? extends Class<?>>[] typeVariables
    ) {
        if (typeVariables.length != directory.getParameters().size()) {
            throw new RuntimeException("Bad type parameter count.");
        }

        Map<String, Integer> typeMapping = new HashMap<>();
        for (int i = 0; i < typeVariables.length; i++) {
            typeMapping.put(typeVariables[i].getName(), i);
        }

        return typeMapping;
    }

    private Map<TypeVariable, Type> getTypeVarMapping2(
            Directory directory,
            TypeVariable<? extends Class<?>>[] typeVariables
    ) throws ClassNotFoundException {

        if (typeVariables.length != directory.getParameters().size()) {
            throw new RuntimeException("Bad type parameter count.");
        }

        Map<TypeVariable, Type> typeMapping = new HashMap<>();
        for (int index = 0; index < typeVariables.length; index++) {

            String className = directory.getParameters().get(index);
            Type type = loadClass(className);

            typeMapping.put(typeVariables[index], type);
        }

        return typeMapping;
    }

    private void genSrcProps(Directory directory) {
        save(directory, "", directory.getFilename());
    }

    private Class<?> loadNonCpClass(Path path) {
        return new ByteClassLoader(this.getClass().getClassLoader()).loadClass(path);
    }

    private void save(Directory directory, String source, String filename) {
        try {
            Files.write(
                    Paths.get(directory.getPath() + File.separator + filename),
                    source.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (filename.endsWith(".java")) {
            compileSource(getPathString(directory, filename));
        }
    }

    private int compileSource(String pathString) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        log.debug("Compiling source: {}...", pathString);

        // build a classpath string for the compiler
        URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
        StringBuilder cp = new StringBuilder();
        for (URL url : urls) {
            cp.append(url.getPath());
            cp.append(File.pathSeparator);
        }

        return compiler.run(
                null,
                null,
                null,
                "-cp", cp.toString(),
                pathString
        );
    }
}
