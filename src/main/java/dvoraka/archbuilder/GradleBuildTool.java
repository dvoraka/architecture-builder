package dvoraka.archbuilder;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class GradleBuildTool implements BuildTool {

    public static final String PREPARE_ENV_CMD = "wrapper";

    private final File path;


    public GradleBuildTool(File path) {
        this.path = requireNonNull(path);
    }

    @Override
    public void prepareEnv() {
        ProjectConnection connection = createProjectConnection();

        BuildLauncher buildLauncher = connection.newBuild();
        buildLauncher.setStandardOutput(System.out);
        buildLauncher.forTasks(PREPARE_ENV_CMD);
        buildLauncher.run();

        connection.close();
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

    private ProjectConnection createProjectConnection() {
        return GradleConnector.newConnector()
                .forProjectDirectory(getPath())
                .connect();
    }
}
