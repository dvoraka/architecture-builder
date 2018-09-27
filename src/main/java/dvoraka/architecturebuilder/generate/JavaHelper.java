package dvoraka.architecturebuilder.generate;

import dvoraka.architecturebuilder.Directory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

public interface JavaHelper {

    default List<Method> findMethods(Class<?> clazz) {

        return Arrays.asList(clazz.getDeclaredMethods());

//        if (clazz.getInterfaces().length == 0) {
//            return Arrays.asList(clazz.getDeclaredMethods());
//        }
//
//        List<Method> methods = new ArrayList<>();
//        for (Class<?> cls : clazz.getInterfaces()) {
//            methods.addAll(findMethods(cls));
//        }
//
//        return methods;
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

    default Class<?> loadClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    default String javaSuffix(String filename) {
        return filename + ".java";
    }

    default String getClassName(Directory directory) {
        return directory.getPackageName() + "." + directory.getFilename()
                .orElseThrow(() -> new RuntimeException("No filename!"));
    }

    default String getPathString(Directory directory, String filename) {
        return directory.getPath() + File.separator + filename;
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
}
