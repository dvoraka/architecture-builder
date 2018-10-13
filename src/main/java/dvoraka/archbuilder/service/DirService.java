package dvoraka.archbuilder.service;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;

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
}
