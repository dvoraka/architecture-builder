package dvoraka.architecturebuilder.generate;

import dvoraka.architecturebuilder.Directory;

public interface LangGenerator {

    /**
     * Generates a content for a given directory.
     *
     * @param directory the directory
     */
    void generate(Directory directory);
}
