package dvoraka.archbuilder.generate;

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
        return clazz.getDeclaredMethods().length > 0;
    }

    default boolean hasNoDeclaredMethods(Class<?> clazz) {
        return !hasDeclaredMethods(clazz);
    }

    default long declaredMethodCount(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> !method.isSynthetic())
                .count();
    }
}
