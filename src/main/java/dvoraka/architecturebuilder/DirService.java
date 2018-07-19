package dvoraka.architecturebuilder;

import java.util.Optional;

public interface DirService {

    /**
     * Process all dirs from a given root.
     *
     * @param root the directory root
     */
    void processDirs(Directory root);

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
