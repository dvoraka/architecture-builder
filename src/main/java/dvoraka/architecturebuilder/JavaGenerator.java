package dvoraka.architecturebuilder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
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

    private void genServiceSafe(Directory directory) {
        try {
            genService(directory);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void genService(Directory directory) throws ClassNotFoundException {
        log.debug("Generating service...");
        log.debug("D: {}", directory);

        String interfaceName = directory.getFilename();

        // find superinterface
        Optional<Directory> superInterfaceDir = dirService.findByType(DirType.SERVICE_ABSTRACT, directory);

        TypeSpec serviceInterface;
        if (superInterfaceDir.isPresent()) {
            Class<?> clazz = loadClass(superInterfaceDir.get().getFilename());

            if (clazz.getTypeParameters().length == 0) {
                serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(clazz)
                        .build();

            } else {
                ParameterizedTypeName parameterizedTypeName =
                        ParameterizedTypeName.get(clazz, clazz.getTypeParameters());

                List<TypeVariableName> typeVariableNames = new ArrayList<>();
                for (TypeName typeArgument : parameterizedTypeName.typeArguments) {
                    typeVariableNames.add(TypeVariableName.get(typeArgument.toString()));
                }

                serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(parameterizedTypeName)
                        .addTypeVariables(typeVariableNames)
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
        TypeVariable<? extends Class<?>>[] typeParameters = superSuperClass.getTypeParameters();
        if (typeParameters.length != directory.getParameters().size()) {
            throw new RuntimeException("Bad type parameter count.");
        }
        Map<String, Integer> typeMapping = new HashMap<>();
        for (int i = 0; i < typeParameters.length; i++) {
            typeMapping.put(typeParameters[i].getName(), i);
        }

        // find all methods from the super super type
        List<Method> allMethods = findMethods(superSuperClass);

        // process all methods
        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (Method m : allMethods) {
            log.debug("Processing method: {}", m.toGenericString());

            // skip default methods
            if (m.isDefault()) {
                log.debug("Skipping default method: {}", m.getName());
                continue;
            }

            // return type
            // * TypeVariable
            // * Class
            // * ParameterizedType
            Type retType = m.getGenericReturnType();
            String retValue = null;
            if (retType instanceof TypeVariable) {
                retType = loadClass(directory.getParameters().get(typeMapping.get(((TypeVariable) retType).getName())));
            } else if (retType != Void.TYPE) {
                retValue = getReturnValue(retType);
            }

            // parameters
            List<ParameterSpec> parSpecs = new ArrayList<>();
            Parameter[] params = m.getParameters();
            for (Parameter param : params) {

                ParameterSpec parSpec;
                if (param.getParameterizedType() instanceof TypeVariable) {

                    Class<?> realClass = loadClass(directory.getParameters().get(typeMapping.get(
                            ((TypeVariable) param.getParameterizedType()).getName())));
                    parSpec = ParameterSpec.builder(realClass, param.getName())
                            .build();

                } else if (param.getParameterizedType() instanceof ParameterizedType) {

                    ParameterizedTypeName parameterizedTypeName = null;

                    ParameterizedType type = ((ParameterizedType) param.getParameterizedType());
                    Class<?> rawClass = (Class) type.getRawType();

                    Type[] actualTypeArguments = type.getActualTypeArguments();
                    for (Type actualTypeArgument : actualTypeArguments) {

                        if (actualTypeArgument instanceof WildcardType) {

                            WildcardType wildcardType = ((WildcardType) actualTypeArgument);
                            if (wildcardType.getUpperBounds().length > 0) {

                                WildcardTypeName wildcardTypeName = WildcardTypeName.subtypeOf(
                                        loadClass(directory.getParameters().get(typeMapping.get(
                                                ((WildcardType) actualTypeArgument).getUpperBounds()[0].getTypeName()))));
                                parameterizedTypeName = ParameterizedTypeName.get(
                                        ClassName.get(rawClass), wildcardTypeName);
                            } else {

                            }
                        }
                    }

                    parSpec = ParameterSpec.builder(parameterizedTypeName, param.getName())
                            .build();

                } else {
                    parSpec = ParameterSpec.builder(param.getParameterizedType(), param.getName())
                            .build();
                }

                parSpecs.add(parSpec);
            }

            // exceptions
            Type[] exceptions = m.getGenericExceptionTypes();
            List<TypeName> exceptionTypes = new ArrayList<>();
            for (Type type : exceptions) {
                exceptionTypes.add(TypeName.get(type));
            }

            // modifiers
            List<Modifier> modifiers = new ArrayList<>();
            if ((m.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 1) {
                modifiers.add(Modifier.PUBLIC);
            }

            MethodSpec spec;
            if (retValue == null) {
                spec = MethodSpec.methodBuilder(m.getName())
                        .addAnnotation(Override.class)
                        .returns(retType)
                        .addModifiers(modifiers)
                        .addParameters(parSpecs)
                        .addExceptions(exceptionTypes)
                        .build();
            } else {
                spec = MethodSpec.methodBuilder(m.getName())
                        .addAnnotation(Override.class)
                        .returns(retType)
                        .addModifiers(modifiers)
                        .addParameters(parSpecs)
                        .addExceptions(exceptionTypes)
                        .addStatement("return " + retValue)
                        .build();
            }

            methodSpecs.add(spec);
        }

        String name = "Default" + superDir.getFilename();

        TypeSpec serviceImpl;
        if (superClass.getTypeParameters().length == 0) {
            serviceImpl = TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get(
                            superDir.getPackageName(),
                            superDir.getFilename()))
                    .addAnnotation(Service.class)
                    .addMethods(methodSpecs)
                    .build();
        } else {
            Class<?> param1 = loadClass(directory.getParameters().get(0));

            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(superClass, param1);

            List<TypeVariableName> typeVariableNames = new ArrayList<>();
            for (TypeName typeArgument : parameterizedTypeName.typeArguments) {
                typeVariableNames.add(TypeVariableName.get(typeArgument.toString()));
            }

            serviceImpl = TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(parameterizedTypeName)
//                    .addTypeVariables(typeVariableNames)
                    .addAnnotation(Service.class)
                    .addMethods(methodSpecs)
                    .build();
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

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        System.out.println("Compiling source...");

        String file = directory.getPath() + File.separator + filename;

        // build a classpath string for the compiler
        URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
        StringBuilder cp = new StringBuilder();
        for (URL url : urls) {
            cp.append(url.getPath());
            cp.append(File.pathSeparator);
        }

        int success = compiler.run(null, null, null, "-cp", cp.toString(), file);
        System.out.println(success);
    }
}
