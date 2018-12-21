package dvoraka.archbuilder.generate;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Slf4j
public final class Utils {

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
                .filter(file -> file.getName().endsWith("*.class"))
                .peek(p -> log.debug("Deleting: {}", p))
                .forEach(File::delete);
    }
}
