package dvoraka.archbuilder.generate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

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

    default void removeFiles(String rootDirName) throws IOException {
//        log.debug("Cleaning up...");

        Path path = Paths.get(rootDirName);
        if (Files.notExists(path)) {
            return;
        }

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
//                .peek(p -> log.debug("Deleting: {}", p))
                .forEach(File::delete);
    }
}
