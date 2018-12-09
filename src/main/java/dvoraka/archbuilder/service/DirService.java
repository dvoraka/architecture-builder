package dvoraka.archbuilder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public interface DirService {

    void processDirs(Directory root, Consumer<Directory> nodeProcessor, Consumer<Directory> leafProcessor);

    void processDirs(Directory root, Consumer<Directory> processor);

    void processDirLeafs(Directory root, Consumer<Directory> processor);

    void processDirNodes(Directory root, Consumer<Directory> processor);

    /**
     * Finds a given type in the whole directory.
     *
     * @param type      the directory type
     * @param directory any part of the directory
     * @return the found directory
     */
    Optional<Directory> findByType(DirType type, Directory directory);

    /**
     * Finds a given type from a given directory.
     *
     * @param type          the directory type
     * @param fromDirectory the root directory for the finding
     * @return the found directory
     */
    Optional<Directory> findByTypeFrom(DirType type, Directory fromDirectory);

    /**
     * Returns a directory root.
     *
     * @param directory the directory
     * @return the root
     */
    Directory getRoot(Directory directory);

    /**
     * Converts a directory into JSON.
     *
     * @param directory the directory
     * @return the JSON
     */
    String toJson(Directory directory) throws JsonProcessingException;

    /**
     * Loads a directory from JSON.
     *
     * @param json the JSON
     * @return the directory
     */
    Directory fromJson(String json) throws JsonProcessingException;

    Path getFilePath(Directory directory);
}
