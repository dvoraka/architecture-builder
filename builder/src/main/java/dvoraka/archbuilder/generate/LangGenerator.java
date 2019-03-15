package dvoraka.archbuilder.generate;

import dvoraka.archbuilder.data.Directory;

public interface LangGenerator {

    /**
     * Generates a content for a given directory.
     *
     * @param directory the directory
     */
    void generate(Directory directory);
}
