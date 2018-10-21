package dvoraka.archbuilder.generate;

import java.lang.reflect.Modifier;

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
}
