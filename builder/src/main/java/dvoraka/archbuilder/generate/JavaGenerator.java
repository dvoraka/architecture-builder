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
import dvoraka.archbuilder.TypeVarMappingException;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.util.ByteClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;

@Service
public class JavaGenerator implements LangGenerator, JavaHelper {

    private final Logger log = LoggerFactory.getLogger(JavaGenerator.class);

    private final EnumMap<DirType, Consumer<Directory>> configuration;
    private final Set<Directory> processedDirs;


    public JavaGenerator() {
        configuration = getConfiguration();
        processedDirs = new HashSet<>();

        checkImplementation();
    }

    private EnumMap<DirType, Consumer<Directory>> getConfiguration() {

        EnumMap<DirType, Consumer<Directory>> configuration = new EnumMap<>(DirType.class);
        configuration.put(DirType.BUILD_CONFIG, this::genBuildConfig);
        configuration.put(DirType.CUSTOM_TYPE, this::genCustomType);
        configuration.put(DirType.IMPL, this::genImpl);
        configuration.put(DirType.NEW_TYPE, this::genNewType);
        configuration.put(DirType.SERVICE, this::genService);
        configuration.put(DirType.SERVICE_IMPL, this::genServiceImpl);
        configuration.put(DirType.SPRING_CONFIG, this::genSpringConfigType);
        configuration.put(DirType.SRC_ROOT, this::processSrcRoot);
        configuration.put(DirType.TEXT, this::genText);

        return configuration;
    }

    private void checkImplementation() {
        for (DirType type : DirType.values()) {
            if (!configuration.containsKey(type)) {
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

        if (configuration.containsKey(directory.getType())) {
            configuration.get(directory.getType()).accept(directory);
        }
    }

    private void processSrcRoot(Directory directory) {
        addClassPath(Paths.get(directory.getPath()));
    }

    private void genBuildConfig(Directory directory) {
        log.debug("Generating build config: {}", directory);

        String configuration = directory.getText();
        String filename = directory.getFilename()
                .orElseThrow(() -> Utils.noFilenameException(directory));

        save(directory, configuration, filename);
    }

    private void genNewType(Directory directory) {
        log.debug("Generating new type: {}", directory);

        TypeSpec.Builder builder = getTypeSpecBuilder(directory);

        addTypeVariables(directory, builder);

        completeAndSaveClass(directory, builder);
    }

    private void genImpl(Directory directory) {
        log.debug("Generating implementation: {}", directory);

        // prepare super types
        List<Directory> superTypeDirs = directory.getSuperTypes();
        if (superTypeDirs.isEmpty()) {
            throw noSuperTypeException();
        }
        List<Class<?>> superTypes = superTypeDirs.stream()
                .map(dir -> loadClass(dir.getTypeName()))
                .collect(Collectors.toList());
        Optional<Class<?>> superClass = findClass(superTypes);
        int parameterCount = getParameterCount(superTypes);

        Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();
        TypeVariable<? extends Class<?>>[] typeParameters = null;
        if (directory.getParameters().isEmpty() && parameterCount > 0) {

            Class<?> templateClass = findTemplateClass(superTypes, parameterCount);
            typeParameters = templateClass.getTypeParameters();

            typeMapping.putAll(getTypeVarMapping2(superTypes, typeParameters));

        } else { // type parameters entered
            for (Class<?> superType : superTypes) {
                if (superType.getTypeParameters().length > 0) {
                    typeMapping.putAll(getTypeVarMapping(directory, superType));
                }
            }
        }

        // find methods and gen specs
        List<Method> allMethods = new ArrayList<>();
        if (!directory.isAbstractType()) {
            allMethods = findAllMethods(superTypes);
        }
        //TODO: current merging is very simple - name && parameter count check
        allMethods = mergeMethods(allMethods);
        List<MethodSpec> methodSpecs = genMethodSpecs(allMethods, typeMapping);
        // add constructor specs if necessary
        if (superClass.isPresent()) {
            Class<?> cls = superClass.get();
            if (isConstructorNeeded(cls)) {
                List<MethodSpec> constructorSpecs = genConstructorSpecs(cls, typeMapping);
                methodSpecs.addAll(constructorSpecs);
            }
        }

        // type spec builder
        TypeSpec.Builder implementationBuilder = getTypeSpecBuilder(directory);

        implementationBuilder.addMethods(methodSpecs);

        // set supertypes
        for (Class<?> superType : superTypes) {
            setSuperType(superType, typeMapping, implementationBuilder);
        }

        // add type variables from supertypes if necessary
        if (typeParameters != null) {
            addTypeVariables(typeParameters, implementationBuilder);
        }

        completeAndSaveClass(directory, implementationBuilder);
    }

    private void genService(Directory directory) {
        log.debug("Generating service: {}", directory);

        // find supertype
        List<Directory> superTypes = directory.getSuperTypes();
        if (superTypes.size() != 1) {
            throw new GeneratorException("Service must have exactly 1 super interface.");
        }
        Directory superDir = directory.getSuperTypes().get(0);
        Class<?> superClass = loadClass(superDir.getTypeName());

        String filename = getFilename(directory);

        TypeSpec serviceInterface;
        if (superClass.getTypeParameters().length == 0) {
            serviceInterface = TypeSpec.interfaceBuilder(filename)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(superClass)
                    .build();
        } else {
            TypeVariable<? extends Class<?>>[] typeParameters = superClass.getTypeParameters();
            Map<TypeVariable<?>, Type> typeMapping = getTypeVarMapping(directory, superClass);

            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                    superClass,
                    buildTypeArray(typeParameters, typeMapping)
            );

            serviceInterface = TypeSpec.interfaceBuilder(filename)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(parameterizedTypeName)
                    .build();
        }

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), serviceInterface)
                .build();

        saveJava(directory, javaFile.toString(), javaSuffix(filename));
    }

    private void genServiceImpl(Directory directory) {
        log.debug("Generating service implementation...");

        Directory superSuperDir = directory.getSuperTypes().stream()
                .findAny()
                .map(Directory::getSuperTypes)
                .map(Collection::stream)
                .flatMap(Stream::findAny)
                .orElseThrow(this::noSuperTypeException);

        Directory superDir = directory.getSuperTypes().stream()
                .findAny()
                .orElseThrow(this::noSuperTypeException);

        Class<?> superSuperClass = loadClass(superSuperDir.getTypeName());
        Class<?> superClass = loadClass(superDir.getTypeName());
        if (superClass.getTypeParameters().length != 0) {
            throw new GeneratorException("Super class has type parameters!");
        }

        // type parameters
        Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();
        if (directory.getParameters().isEmpty()) {
            if (!superDir.getParameters().isEmpty()) {
                typeMapping = getTypeVarMapping(superDir, superSuperClass);
            }
        } else {
            typeMapping = getTypeVarMapping(directory, superClass);
        }

        List<Method> allMethods = findMethods(superClass);
        List<MethodSpec> methodSpecs = genMethodSpecs(allMethods, typeMapping);

        String filename = getFilename(directory);

        TypeSpec serviceImpl = TypeSpec.classBuilder(filename)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(superClass)
                .addAnnotation(Service.class)
                .addMethods(methodSpecs)
                .build();

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), serviceImpl)
                .build();

        saveJava(directory, javaFile.toString(), javaSuffix(filename));
    }

    private void genCustomType(Directory directory) {
        log.debug("Generating custom type: {}", directory);

        String source = directory.getText();
        String filename = javaSuffix(getFilename(directory));

        saveJava(directory, source, filename);
    }

    private void genSpringConfigType(Directory directory) {
        log.debug("Generating Spring config type: {}", directory);

        String source = directory.getTextSupplier() != null
                ? directory.getTextSupplier().get()
                : directory.getText();

        if (source == null || source.isEmpty()) {
            throw new GeneratorException("No text.");
        }

        String filename = javaSuffix(getFilename(directory));

        saveJava(directory, source, filename);
    }

    private List<MethodSpec> genMethodSpecs(List<Method> methods, Map<TypeVariable<?>, Type> typeMapping) {

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
            TypeName returnTypeName = resolveTypeName(returnType, typeMapping);

            // return value
            String returnValue;
            if (returnType == Void.TYPE) {
                returnValue = null;
            } else {
                returnValue = getReturnValue(returnType);
            }

            // parameters (generic parameters are not used because of parameter names)
            Parameter[] parameters = method.getParameters();
            List<ParameterSpec> parameterSpecs = genParameterSpecs(parameters, typeMapping);

            // exceptions
            Type[] exceptions = method.getGenericExceptionTypes();
            List<TypeName> exceptionTypes = new ArrayList<>();
            for (Type type : exceptions) {
                exceptionTypes.add(resolveTypeName(type, typeMapping));
            }

            // modifiers
            List<Modifier> modifiers = new ArrayList<>();
            if (isPublic(method.getModifiers())) {
                modifiers.add(Modifier.PUBLIC);
            } else if (isProtected(method.getModifiers())) {
                modifiers.add(Modifier.PROTECTED);
            }

            MethodSpec methodSpec;
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(method.getName())
                    .addAnnotation(Override.class)
                    .returns(returnTypeName)
                    .addModifiers(modifiers)
                    .addParameters(parameterSpecs)
                    .addExceptions(exceptionTypes);

            if (returnValue != null) {
                methodSpecBuilder.addStatement("return " + returnValue);
            }
            if (typeVariableName != null) {
                methodSpecBuilder.addTypeVariable(typeVariableName);
            }

            methodSpec = methodSpecBuilder.build();
            methodSpecs.add(methodSpec);
        }

        return methodSpecs;
    }

    private TypeName resolveTypeName(Type type, Map<TypeVariable<?>, Type> typeMapping) {

        TypeName typeName;
        if (type instanceof TypeVariable) {

            typeName = TypeName.get(typeMapping.get(type));

        } else if (type instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) type;
            typeName = resolveParametrizedType(parameterizedType, typeMapping);

            //TODO: all cases
//            } else if (returnType instanceof GenericArrayType) {
//
//                typeName = ArrayTypeName.get(returnType);

        } else {
            typeName = TypeName.get(type);
        }

        return typeName;
    }

    private List<MethodSpec> genConstructorSpecs(Class<?> superClass, Map<TypeVariable<?>, Type> typeMapping) {

        Constructor<?>[] declaredConstructors = superClass.getDeclaredConstructors();
        List<MethodSpec> constructorSpecs = new ArrayList<>();
        for (Constructor<?> constructor : declaredConstructors) {

            int modifiers = constructor.getModifiers();

            // skip private constructors
            if (isPrivate(modifiers)) {
                continue;
            }

            Modifier constructorModifier;
            if (isPublic(modifiers)) {
                constructorModifier = Modifier.PUBLIC;
            } else if (isProtected(modifiers)) {
                constructorModifier = Modifier.PROTECTED;
            } else {
                constructorModifier = null;
            }

            Parameter[] parameters = constructor.getParameters();
            List<ParameterSpec> parameterSpecs = genParameterSpecs(parameters, typeMapping);

            String[] argNames = parameterSpecs.stream()
                    .map(parameterSpec -> parameterSpec.name)
                    .toArray(String[]::new);

            MethodSpec constructorSpec;
            if (constructorModifier != null) {
                constructorSpec = MethodSpec.constructorBuilder()
                        .addModifiers(constructorModifier)
                        .addParameters(parameterSpecs)
                        .addStatement(String.format(buildSuperString(argNames), (Object[]) argNames))
                        .build();
            } else {
                constructorSpec = MethodSpec.constructorBuilder()
                        .addParameters(parameterSpecs)
                        .addStatement(String.format(buildSuperString(argNames), (Object[]) argNames))
                        .build();
            }

            constructorSpecs.add(constructorSpec);
        }

        return constructorSpecs;
    }

    private List<ParameterSpec> genParameterSpecs(Parameter[] parameters, Map<TypeVariable<?>, Type> typeMapping) {

        List<ParameterSpec> parameterSpecs = new ArrayList<>();
        for (Parameter parameter : parameters) {

            ParameterSpec parameterSpec;
            if (parameter.getParameterizedType() instanceof TypeVariable) {

                TypeVariable<?> typeVar = ((TypeVariable) parameter.getParameterizedType());
                Type realType = typeMapping.getOrDefault(typeVar, typeVar);
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

        return parameterSpecs;
    }

    private ParameterizedTypeName resolveParametrizedType(
            ParameterizedType type,
            Map<TypeVariable<?>, Type> varTypeMapping
    ) {
        Class<?> rawClass = (Class) type.getRawType();

        Type[] actualTypeArguments = type.getActualTypeArguments();
        List<TypeName> typeNames = new ArrayList<>();
        for (Type actualTypeArgument : actualTypeArguments) {

            if (actualTypeArgument instanceof TypeVariable) {

                Type typeVar = varTypeMapping.getOrDefault(actualTypeArgument, actualTypeArgument);
                TypeName typeVariableName = TypeName.get(typeVar);
                typeNames.add(typeVariableName);

            } else if (actualTypeArgument instanceof ParameterizedType) {

                TypeName paramTypeName = resolveParametrizedType(((ParameterizedType) actualTypeArgument), varTypeMapping);
                typeNames.add(paramTypeName);

            } else if (actualTypeArgument instanceof Class) {

                TypeName typeName = TypeName.get(actualTypeArgument);
                typeNames.add(typeName);

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

        return ParameterizedTypeName.get(
                ClassName.get(rawClass),
                typeNames.toArray(new TypeName[0])
        );
    }

    private Map<TypeVariable<?>, Type> getTypeVarMapping(Directory directory, Class<?> clazz) {

        TypeVariable<? extends Class<?>>[] typeVariables = clazz.getTypeParameters();

        Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();
        if (directory.getParameters().isEmpty()) {
            throw new GeneratorException("No parameters for directory.");

            // just copy type variables
//            for (TypeVariable<? extends Class<?>> typeVariable : typeVariables) {
//                typeMapping.put(typeVariable, typeVariable);
//            }
        } else {
            if (directory.getParameters().size() != typeVariables.length) {
                throw new GeneratorException("Type parameter counts do not match for: "
                        + directory.getFilename().orElse("unknown"));
            }

            for (int index = 0; index < typeVariables.length; index++) {

                String className = directory.getParameters().get(index);
                Class<?> varClass = loadClass(className);

                typeMapping.put(typeVariables[index], varClass);
            }
        }

        addAllTypeVarMappings(clazz, typeMapping);

        return typeMapping;
    }

    private Map<TypeVariable<?>, Type> getTypeVarMapping2(
            List<Class<?>> superTypes,
            TypeVariable<? extends Class<?>>[] typeParameters
    ) {
        Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();
        for (Class<?> superType : superTypes) {
            TypeVariable<? extends Class<?>>[] typeVariables = superType.getTypeParameters();

            for (int i = 0; i < typeVariables.length; i++) {
                typeMapping.put(typeVariables[i], typeParameters[i]);
            }

            addAllTypeVarMappings(superType, typeMapping);
        }

        return typeMapping;
    }

    private void addAllTypeVarMappings(Class<?> clazz, Map<TypeVariable<?>, Type> typeMapping) {

        // from implemented interfaces
        for (Type iface : clazz.getGenericInterfaces()) {
            if (iface instanceof ParameterizedType) {
                ParameterizedType paramIface = (ParameterizedType) iface;
                // find vars for interface
                addTypeVarsForType(paramIface, typeMapping);
                // find vars for all super-interfaces
                addAllTypeVarMappings((Class<?>) paramIface.getRawType(), typeMapping);
            }
        }

        // from superclass
        Type superClass = clazz.getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType paramSuperClass = (ParameterizedType) superClass;

            //TODO: check with complex test
            // add new mapping to bridge the mapping gap
            Class<?> superRawClass = (Class<?>) paramSuperClass.getRawType();
            for (int i = 0; i < superRawClass.getTypeParameters().length; i++) {
                typeMapping.put(
                        superRawClass.getTypeParameters()[i],
                        typeMapping.get(paramSuperClass.getActualTypeArguments()[i])
                );
            }

            addAllTypeVarMappings(superRawClass, typeMapping);
        }
    }

    private void addTypeVarsForType(ParameterizedType paramType, Map<TypeVariable<?>, Type> typeMapping) {

        Type[] actualTypeArgs = paramType.getActualTypeArguments();
        for (int i = 0; i < actualTypeArgs.length; i++) {
            Type actualTypeArg = actualTypeArgs[i];

            if (actualTypeArg instanceof TypeVariable) {
                TypeVariable<?> actualVar = (TypeVariable) actualTypeArg;

                Type rawType = paramType.getRawType();
                if (rawType instanceof Class) {
                    Class<?> rawClass = ((Class) rawType);

                    TypeVariable<? extends Class<?>>[] srcTypeParams = rawClass.getTypeParameters();

                    if (typeMapping.containsKey(actualVar)) {
                        typeMapping.put(srcTypeParams[i], typeMapping.get(actualVar));
                    } else {
                        throw new TypeVarMappingException();
                    }
                }
            }
        }
    }

    private void genText(Directory directory) {
        String filename = directory.getFilename()
                .orElseThrow(() -> new RuntimeException("No filename for the text file!"));

        String text = directory.getText() != null ? directory.getText() : "";

        save(directory, text, filename);
    }

    private Class<?> loadNonCpClass(Path path) {
        return new ByteClassLoader(this.getClass().getClassLoader()).loadClass(path);
    }

    private List<Method> mergeMethods(List<Method> methods) {

        List<Method> mergedMethods = new ArrayList<>(methods);
        List<Method> toRemove = new ArrayList<>();
        for (Method method : methods) {

            if (isAbstract(method.getModifiers())) {
                for (Method m : methods) {

                    if (!(m.equals(method) || m.isSynthetic() || isAbstract(m.getModifiers()))) {

                        if (m.getName().equals(method.getName())
                                && m.getParameterCount() == method.getParameterCount()) {

                            // remove abstract method
                            toRemove.add(method);
                        }
                    }
                }
            }
        }

        mergedMethods.removeAll(toRemove);

        return mergedMethods;
    }

    private int getParameterCount(List<Class<?>> classes) {
        return classes.stream()
                .map(Class::getTypeParameters)
                .mapToInt(params -> params.length)
                .filter(length -> length > 0)
                .max()
                .orElse(0);
    }

    private Class<?> findTemplateClass(List<Class<?>> classes, int paramCount) {
        return classes.stream()
                .filter(cls -> cls.getTypeParameters().length == paramCount)
                .findFirst()
                .orElseThrow(() -> new GeneratorException("No template class found."));
    }

    private TypeSpec.Builder setSuperType(
            Class<?> superType, Map<TypeVariable<?>,
            Type> typeMapping,
            TypeSpec.Builder builder
    ) {
        TypeSpec.Builder updatedBuilder;
        if (superType.isInterface()) {

            if (superType.getTypeParameters().length == 0) {

                updatedBuilder = builder.addSuperinterface(superType);
            } else { // parametrized interface
                ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                        superType,
                        buildTypeArray(superType.getTypeParameters(), typeMapping)
                );

                updatedBuilder = builder.addSuperinterface(parameterizedTypeName);
            }
        } else { // supertype is class

            if (superType.getTypeParameters().length == 0) {

                updatedBuilder = builder.superclass(superType);

            } else { // parametrized class
                ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                        superType,
                        buildTypeArray(superType.getTypeParameters(), typeMapping)
                );

                updatedBuilder = builder.superclass(parameterizedTypeName);
            }
        }

        return updatedBuilder;
    }

    private TypeSpec.Builder addModifiers(Directory directory, TypeSpec.Builder builder) {

        if (directory.isInterfaceType()) { // interface
            builder.addModifiers(Modifier.PUBLIC);
        } else if (directory.isAbstractType()) { // abstract
            builder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        } else { // not abstract
            builder.addModifiers(Modifier.PUBLIC);
        }

        return builder;
    }

    private TypeSpec.Builder addAnnotations(Directory directory, TypeSpec.Builder builder) {

        for (String annotation : directory.getMetadata()) {
            Class<?> cls = loadClass(annotation);
            builder.addAnnotation(cls);
        }

        return builder;
    }

    private TypeSpec.Builder addTypeVariables(Directory directory, TypeSpec.Builder builder) {

        for (String variable : directory.getParameters()) {
            builder.addTypeVariable(TypeVariableName.get(variable));
        }

        return builder;
    }

    private TypeSpec.Builder addTypeVariables(
            TypeVariable<? extends Class<?>>[] typeParameters,
            TypeSpec.Builder builder
    ) {
        List<TypeVariableName> typeVariableNames = new ArrayList<>();
        for (TypeVariable<? extends Class<?>> typeParameter : typeParameters) {
            typeVariableNames.add(TypeVariableName.get(typeParameter));
        }

        builder.addTypeVariables(typeVariableNames);

        return builder;
    }

    private TypeSpec.Builder getTypeSpecBuilder(Directory directory) {

        String filename = getFilename(directory);

        TypeSpec.Builder builder;
        if (directory.isInterfaceType()) {
            builder = TypeSpec.interfaceBuilder(filename);
        } else if (directory.isEnumType()) {
            builder = TypeSpec.enumBuilder(filename);
        } else if (directory.isAnnotationType()) {
            builder = TypeSpec.annotationBuilder(filename);
        } else {
            builder = TypeSpec.classBuilder(filename);
        }

        return builder;
    }

    private String getFilename(Directory directory) {
        return directory.getFilename()
                .orElseThrow(() -> Utils.noFilenameException(directory));
    }

    private void saveJava(Directory directory, String source, String filename) {
        if (!filename.endsWith(".java")) {
            throw new GeneratorException("Java file must have .java suffix");
        }

        save(directory, source, filename);

        JavaUtils.compileSource(getPathString(directory, filename));
    }

    private void save(Directory directory, String source, String filename) {
        log.debug("Saving source:\n{}", source);

        try {
            Files.write(
                    Paths.get(directory.getPath() + File.separator + filename),
                    source.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("Save failed!", e);
            throw new GeneratorException(e);
        }
    }

    private void completeAndSaveClass(Directory directory, TypeSpec.Builder builder) {

        addAnnotations(directory, builder);
        addModifiers(directory, builder);

        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), typeSpec)
                .build();

        saveJava(directory, javaFile.toString(), javaSuffix(getFilename(directory)));
    }

    private GeneratorException noSuperTypeException() {
        return new GeneratorException("No supertype.");
    }
}
