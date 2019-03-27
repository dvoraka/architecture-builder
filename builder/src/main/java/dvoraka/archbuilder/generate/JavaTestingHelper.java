package dvoraka.archbuilder.generate;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.service.DirService;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    default int annotationCount(Class<?> clazz) {
        return clazz.getDeclaredAnnotations().length;
    }

    default int declaredConstructorCount(Class<?> clazz) {
        return clazz.getDeclaredConstructors().length;
    }

    default int interfaceCount(Class<?> clazz) {
        return clazz.getInterfaces().length;
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

    default void runMainMethod(Class<?> clazz, String[] args)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Method mainMethod = clazz.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, new Object[]{args});
    }

    default void fileExists(Directory directory) {
        if (directory.getType() == DirType.ABSTRACT) {
            return;
        }

        String fullPath = directory.getPath() + File.separator + directory.getFilename()
                .orElseThrow(() -> new GeneratorException("Filename is missing."));

        if (!Files.exists(Paths.get(fullPath))) {
            throw new GeneratorException("File: " + fullPath + " is missing.");
        }
    }

    default void filesExist(DirService dirService, Directory rootDir) {
        dirService.processDirLeafs(rootDir, this::fileExists);
    }
}
