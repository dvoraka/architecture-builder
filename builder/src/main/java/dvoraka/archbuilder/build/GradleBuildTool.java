package dvoraka.archbuilder.build;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class GradleBuildTool implements BuildTool {

    public static final String PREPARE_ENV_CMD = "wrapper";
    public static final String BUILD_CMD = "build";

    private final File path;


    public GradleBuildTool(File path) {
        this.path = requireNonNull(path);
    }

    @Override
    public void prepareEnv() {
        runTasks(PREPARE_ENV_CMD);
    }

    @Override
    public void build() {
        runTasks(BUILD_CMD);
    }

    @Override
    public void prepareEnvAndBuild() {
        runTasks(PREPARE_ENV_CMD, BUILD_CMD);
    }

    public File getPath() {
        return path;
    }

    private ProjectConnection createProjectConnection() {
        return GradleConnector.newConnector()
                .forProjectDirectory(getPath())
                .connect();
    }

    private void runTasks(String... tasks) {
        ProjectConnection connection = createProjectConnection();

        BuildLauncher buildLauncher = connection.newBuild();
        buildLauncher.setStandardOutput(System.out);
        buildLauncher.forTasks(tasks);
        buildLauncher.run();

        connection.close();
    }
}
