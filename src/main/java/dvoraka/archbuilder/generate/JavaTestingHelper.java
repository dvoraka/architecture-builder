package dvoraka.archbuilder.generate;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.service.DirService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public interface JavaTestingHelper {

    default boolean isPublic(Class<?> clazz) {
        return Modifier.isPublic(clazz.getModifiers());
    }

    default boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    default boolean isNotAbstract(Class<?> clazz) {
        return !isAbstract(clazz);
    }

    default boolean isPublicAbstract(Class<?> clazz) {
        return isPublic(clazz) && isAbstract(clazz);
    }

    default boolean isPublicNotAbstract(Class<?> clazz) {
        return isPublic(clazz) && !isAbstract(clazz);
    }

    default boolean hasTypeParameters(Class<?> clazz) {
        return clazz.getTypeParameters().length > 0;
    }

    default boolean hasNoTypeParameters(Class<?> clazz) {
        return !hasTypeParameters(clazz);
    }

    default boolean hasDeclaredMethods(Class<?> clazz) {
        return declaredMethodCount(clazz) > 0;
    }

    default boolean hasNoDeclaredMethods(Class<?> clazz) {
        return !hasDeclaredMethods(clazz);
    }

    default long declaredMethodCount(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> !method.isSynthetic())
                .count();
    }

    default int declaredConstructorCount(Class<?> clazz) {
        return clazz.getDeclaredConstructors().length;
    }

    default boolean exists(DirType dirType, Directory directory, DirService dirService) {
        return dirService.findByType(dirType, directory)
                .isPresent();
    }

    /**
     * For a simple no-arg constructor and method.
     */
    default Object runMethod(Class<?> clazz, String methodName)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        Object instance = clazz.getConstructor().newInstance();

        return clazz.getDeclaredMethod(methodName).invoke(instance);
    }
}
