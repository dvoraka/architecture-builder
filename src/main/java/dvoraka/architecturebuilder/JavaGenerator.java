package dvoraka.architecturebuilder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

@Service
public class JavaGenerator implements LangGenerator {

    private final DirService dirService;

    private final Logger log = LoggerFactory.getLogger(JavaGenerator.class);

    private final EnumMap<DirType, Consumer<Directory>> conf;


    @Autowired
    public JavaGenerator(DirService dirService) {
        this.dirService = requireNonNull(dirService);

        conf = new EnumMap<>(DirType.class);
        conf.put(DirType.SERVICE, this::genService);
        conf.put(DirType.SERVICE_IMPL, this::genServiceImpl);

        checkImplementation();
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
        log.debug("Generating code for: {}", directory.getType());

        if (conf.containsKey(directory.getType())) {
            conf.get(directory.getType()).accept(directory);
        }
    }

    private void genService(Directory directory) {
        log.debug("Generating service...");
        log.debug("D: {}", directory);

        String interfaceName = directory.getFilename();

        // find superinterface
        Optional<Directory> superInterfaceDir = dirService.findByType(DirType.SERVICE_ABSTRACT, directory);

        TypeSpec serviceInterface;
        if (superInterfaceDir.isPresent()) {
            Class<?> clazz = loadClass(superInterfaceDir.get().getFilename())
                    .orElseThrow(RuntimeException::new);

            serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(clazz)
                    .build();
        } else {
            serviceInterface = TypeSpec.interfaceBuilder(interfaceName)
                    .addModifiers(Modifier.PUBLIC)
                    .build();
        }

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), serviceInterface)
                .build();

        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genServiceImpl(Directory directory) {
        log.debug("Generating service implementation...");

        Directory superSuperInterfaceDir = dirService.findByType(DirType.SERVICE_ABSTRACT, directory)
                .orElseThrow(RuntimeException::new);

        Directory superInterfaceDir = dirService.findByType(DirType.SERVICE, directory)
                .orElseThrow(RuntimeException::new);

        Class<?> clazz = loadClass(superSuperInterfaceDir.getFilename())
                .orElseThrow(RuntimeException::new);

        // methods from the type
        Method methods[] = clazz.getDeclaredMethods();
        System.out.println(Arrays.toString(methods));

        // find all methods
        List<Method> allMethods = findMethods(clazz);
        allMethods.forEach(System.out::println);

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
            Type retType = m.getGenericReturnType();
            String retValue = null;
            if (retType != Void.TYPE) {
                retValue = findReturnValue(retType);
            }

            // parameters
            List<ParameterSpec> parSpecs = new ArrayList<>();
            Parameter[] params = m.getParameters();
            for (Parameter param : params) {
                ParameterSpec parSpec = ParameterSpec.builder(param.getParameterizedType(), param.getName())
                        .build();

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

        String name = "Default" + superInterfaceDir.getFilename();
        TypeSpec serviceImpl = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(
                        superInterfaceDir.getPackageName(),
                        superInterfaceDir.getFilename()))
                .addAnnotation(Service.class)
                .addMethods(methodSpecs)
                .build();

        JavaFile javaFile = JavaFile.builder(directory.getPackageName(), serviceImpl)
                .build();

        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Method> findMethods(Class<?> clazz) {
        if (clazz.getInterfaces().length == 0) {
            return Arrays.asList(clazz.getDeclaredMethods());
        }

        List<Method> methods = new ArrayList<>();
        for (Class<?> cls : clazz.getInterfaces()) {
            methods.addAll(findMethods(cls));
        }

        return methods;
    }

    private Optional<Class<?>> loadClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(clazz);
    }

    private String findReturnValue(Type returnType) {
        String returnValue = "XXX";

        if (returnType instanceof Class
                && !((Class) returnType).isPrimitive()) {
            returnValue = "null";
        }

        if (returnType == Boolean.TYPE) {
            returnValue = "false";
        }

        return returnValue;
    }
}
