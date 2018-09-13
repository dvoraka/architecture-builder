package dvoraka.architecturebuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public interface JavaHelper {

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
        return directory.getPackageName() + "." + directory.getFilename();
    }
}
