package dvoraka.archbuilder.generate;

import com.squareup.javapoet.TypeVariableName;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.exception.GeneratorException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface JavaHelper {

    default List<Method> findMethods(Class<?> clazz) {

        // find protected methods
        List<Method> protectedMethods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> Modifier.isProtected(method.getModifiers()))
                .collect(Collectors.toList());

        // find public methods
        List<Method> publicMethods = Arrays.asList(clazz.getMethods());

        List<Method> allMethods = new ArrayList<>();
        allMethods.addAll(protectedMethods);
        allMethods.addAll(publicMethods);

        return allMethods;
    }

    default List<Method> findAllMethods(List<Class<?>> classes) {
        return classes.stream()
                .map(this::findMethods)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    default String getReturnValue(Type returnType) {
        String returnValue = "XXX";

        if (returnType instanceof Class
                && !((Class) returnType).isPrimitive()) {
            returnValue = "null";
        } else if (returnType instanceof ParameterizedType) {
            returnValue = "null";
        } else if (returnType instanceof TypeVariable) {
            returnValue = "null";
        } else if (returnType instanceof GenericArrayType) {
            returnValue = "null";
        } else if (returnType == Boolean.TYPE) {
            returnValue = "false";
        } else if (returnType == Character.TYPE) {
            returnValue = "'\n'";
        } else if (returnType == Double.TYPE) {
            returnValue = "0.0";
        } else if (returnType == Float.TYPE) {
            returnValue = "0.0f";
        } else if (returnType == Integer.TYPE || returnType == Short.TYPE || returnType == Byte.TYPE) {
            returnValue = "0";
        } else if (returnType == Long.TYPE) {
            returnValue = "0L";
        }

        return returnValue;
    }

    default Class<?> loadClass(String className) throws GeneratorException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new GeneratorException(e);
        }
    }

    default String javaSuffix(String filename) {
        return JavaUtils.javaSuffix(filename);
    }

    default String getClassName(Directory directory) {
        return directory.getTypeName();
    }

    default String getPathString(Directory directory, String filename) {
        return directory.getPath() + File.separator + filename;
    }

    default String defaultServiceImplName(Directory serviceImpl) {
        return new StringBuilder()
                .append(serviceImpl.getPackageName())
                .append(".")
                .append("Default")
                .append(serviceImpl.getFilename()
                        .orElseThrow(() -> new GeneratorException("No filename specified.")))
                .toString();
    }

    default void addClassPath(Path path) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), path.toUri().toURL());
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException
                | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    default String getGenericSignature(Method method) {
        try {
            Method m = Method.class.getDeclaredMethod("getGenericSignature");
            m.setAccessible(true);
            String signature = (String) m.invoke(method);

            return signature;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new GeneratorException("Getting method signature failed.", e);
        }
    }

    default boolean isConstructorNeeded(Class<?> superClass) {

        Constructor<?>[] declaredConstructors = superClass.getDeclaredConstructors();
        if (declaredConstructors.length == 0) {
            return false;
        }

        return Arrays.stream(declaredConstructors)
                .noneMatch(constructor -> constructor.getParameterCount() == 0);
    }

    default List<TypeVariableName> getTypeVariableNames(Class<?> clazz) {

        return Stream.of(clazz.getTypeParameters())
                .map(TypeVariableName::get)
                .collect(Collectors.toList());
    }

    default String buildSuperString(String[] argNames) {
        if (argNames == null || argNames.length == 0) {
            throw new IllegalArgumentException();
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("super(");
        for (int i = 0; i < argNames.length; i++) {
            stringBuilder.append("%s");
            if (i != (argNames.length - 1)) { // not last
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    default Type[] buildTypeArray(
            TypeVariable<? extends Class<?>>[] typeParameters,
            Map<TypeVariable<?>, Type> typeMapping
    ) {
        List<Type> types = new ArrayList<>();
        for (TypeVariable<? extends Class<?>> typeVariable : typeParameters) {
            Type realType = typeMapping.get(typeVariable);
            types.add(realType);
        }

        return types.toArray(new Type[0]);
    }

    default Optional<Class<?>> findClass(List<Class<?>> classes) {
        return classes.stream()
                .filter(cls -> !cls.isInterface() || !cls.isArray())
                .findAny();
    }
}
