package dvoraka.archbuilder;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class GradleBuildTool implements BuildTool {

    private final File path;


    public GradleBuildTool(File path) {
        this.path = requireNonNull(path);
    }

    @Override
    public void prepareEnv() {

    }

    @Override
    public void build() {

    }

    @Override
    public void prepareEnvAndBuild() {

    }

    public File getPath() {
        return path;
    }
}
