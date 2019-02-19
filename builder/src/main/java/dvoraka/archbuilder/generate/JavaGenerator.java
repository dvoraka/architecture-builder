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
import dvoraka.archbuilder.service.DirService;
import dvoraka.archbuilder.util.ByteClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        conf.put(DirType.BUILD_CONFIG, this::genBuildConfig);
        conf.put(DirType.CUSTOM_TYPE, this::genCustomType);
        conf.put(DirType.IMPL, this::genImplSafe);
        conf.put(DirType.SERVICE, this::genServiceSafe);
        conf.put(DirType.SERVICE_IMPL, this::genServiceImplSafe);
        conf.put(DirType.SPRING_CONFIG, this::genSpringConfigType);
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

    private void genBuildConfig(Directory directory) {
        log.debug("Generating build config: {}", directory);

        String configuration = directory.getText();
        String filename = directory.getFilename()
                .orElseThrow(RuntimeException::new);

        try {
            save(directory, configuration, filename);
        } catch (IOException e) {
            e.printStackTrace();
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
            allMethods = superTypes.stream()
                    .map(this::findMethods)
                    .flatMap(List::stream)
                    .distinct()
                    .collect(Collectors.toList());
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

        //TODO: filename is always presented
        // prepare final filename
        String name = directory.getFilename()
                .orElseThrow(() -> new GeneratorException("No filename"));

        // spec builder
        TypeSpec.Builder implementationBuilder = directory.isInterfaceType()
                ? TypeSpec.interfaceBuilder(name)
                : TypeSpec.classBuilder(name);

        implementationBuilder.addMethods(methodSpecs);

        // set supertypes
        for (Class<?> superType2 : superTypes) {

            if (superType2.isInterface()) {

                if (superType2.getTypeParameters().length == 0) {
                    implementationBuilder = implementationBuilder
                            .addSuperinterface(superType2);
                } else { // parametrized interface
                    ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                            superType2,
                            buildTypeArray(superType2.getTypeParameters(), typeMapping)
                    );
                    implementationBuilder = implementationBuilder
                            .addSuperinterface(parameterizedTypeName);
                }
            } else { // supertype is class

                if (superType2.getTypeParameters().length == 0) {
                    implementationBuilder = implementationBuilder
                            .superclass(superType2);
                } else { // parametrized class
                    ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                            superType2,
                            buildTypeArray(superType2.getTypeParameters(), typeMapping)
                    );
                    implementationBuilder = implementationBuilder
                            .superclass(parameterizedTypeName);
                }
            }
        }

        // add type variables from supertypes if necessary
        if (typeParameters != null) {

            List<TypeVariableName> typeVariableNames = new ArrayList<>();
            for (TypeVariable<? extends Class<?>> typeParameter : typeParameters) {
                typeVariableNames.add(TypeVariableName.get(typeParameter));
            }

            implementationBuilder.addTypeVariables(typeVariableNames);
        }

        // modifiers
        if (directory.isInterfaceType()) { // interface
            implementationBuilder = implementationBuilder
                    .addModifiers(Modifier.PUBLIC);
        } else if (directory.isAbstractType()) { // abstract
            implementationBuilder = implementationBuilder
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        } else { // not abstract
            implementationBuilder = implementationBuilder
                    .addModifiers(Modifier.PUBLIC);
        }

        TypeSpec implementation = implementationBuilder.build();

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

                Map<TypeVariable<?>, Type> typeMapping = getTypeVarMapping(directory, superClass);

                ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                        superClass,
                        buildTypeArray(typeParameters, typeMapping)
                );

                serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(parameterizedTypeName)
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
        Map<TypeVariable<?>, Type> typeMapping;
        Directory paramDir;
        if (directory.getParameters().isEmpty()) {
            paramDir = superDir;
            typeMapping = getTypeVarMapping(paramDir, superSuperClass);
        } else {
            paramDir = directory;
            typeMapping = getTypeVarMapping(paramDir, superClass);
        }

        // find all methods from the super type
        List<Method> allMethods = findMethods(superClass);

        // process all methods
        List<MethodSpec> methodSpecs = genMethodSpecs(allMethods, typeMapping);

        String name = directory.getFilename()
                .orElseThrow(() -> new GeneratorException("No filename for service implementation."));

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

    private void genCustomType(Directory directory) {
        log.debug("Generating custom type: {}", directory);

        String source = directory.getText();
        String filename = javaSuffix(directory.getFilename()
                .orElseThrow(RuntimeException::new));

        try {
            save(directory, source, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genSpringConfigType(Directory directory) {
        log.debug("Generating Spring config type: {}", directory);

        String source = directory.getTextSupplier() != null
                ? directory.getTextSupplier().get()
                : directory.getText();

        if (source == null || source.isEmpty()) {
            throw new RuntimeException("No text");
        }

        String filename = javaSuffix(directory.getFilename()
                .orElseThrow(RuntimeException::new));

        try {
            save(directory, source, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(rawClass),
                typeNames.toArray(new TypeName[0])
        );

        return parameterizedTypeName;
    }

    private Map<TypeVariable<?>, Type> getTypeVarMapping(Directory directory, Class<?> clazz)
            throws ClassNotFoundException {

        TypeVariable<? extends Class<?>>[] typeVariables = clazz.getTypeParameters();

        Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();
        if (directory.getParameters().isEmpty()) {
            //TODO: throw an exception after code migration
//            throw new GeneratorException("No parameters for directory");
            // just copy type variables
            for (TypeVariable<? extends Class<?>> typeVariable : typeVariables) {
                typeMapping.put(typeVariable, typeVariable);
            }
        } else {

            if (directory.getParameters().size() != typeVariables.length) {
                throw new RuntimeException("Type parameter counts do not match for: "
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

    private void save(Directory directory, String source, String filename) throws IOException {
        log.debug("Saving source:\n{}", source);

        Files.write(
                Paths.get(directory.getPath() + File.separator + filename),
                source.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        if (filename.endsWith(".java")) {
            JavaUtils.compileSource(getPathString(directory, filename));
        }
    }
}
