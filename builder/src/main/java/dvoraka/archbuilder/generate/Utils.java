package dvoraka.archbuilder.generate;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public final class Utils {

    private final static Logger log = LoggerFactory.getLogger(Utils.class);


    private Utils() {
    }

    public static void removeFiles(String rootDirName) throws IOException {
        log.debug("Cleaning up...");

        Path path = Paths.get(rootDirName);
        if (Files.notExists(path)) {
            return;
        }

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .peek(p -> log.debug("Deleting: {}", p))
                .forEach(File::delete);
    }

    public static void removeClassFiles(String rootDirName) throws IOException {
        log.debug("Cleaning up class files...");

        Path path = Paths.get(rootDirName);
        if (Files.notExists(path)) {
            return;
        }

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".class"))
                .peek(p -> log.debug("Deleting: {}", p))
                .forEach(File::delete);
    }

    public static Directory createAbstractDirFor(Class<?> cls, Directory srcBase) {
        return new Directory.Builder("util", DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(cls)
                .build();
    }

    public static GeneratorException noFilenameException(Directory directory) {
        return new GeneratorException("No filename for: " + directory);
    }

    public static String uncapitalize(String string) {
        return StringUtils.uncapitalize(string);
    }
}
