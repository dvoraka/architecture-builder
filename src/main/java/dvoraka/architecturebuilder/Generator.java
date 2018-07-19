package dvoraka.architecturebuilder;

public interface Generator {

    /**
     * Generates a content from a given directory.
     *
     * @param directory the directory
     */
    void generate(Directory directory);
}
