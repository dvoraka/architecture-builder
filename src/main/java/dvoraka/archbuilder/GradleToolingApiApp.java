package dvoraka.archbuilder;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;

import java.io.File;

public class GradleToolingApiApp {

    public static void main(String[] args) {

        try (ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(new File("."))
                .connect()) {

            GradleProject project = connection.getModel(GradleProject.class);

            DomainObjectSet<? extends GradleTask> tasks = project.getTasks();
            tasks.forEach(System.out::println);

            BuildLauncher buildLauncher = connection.newBuild();
//                buildLauncher.addProgressListener((ProgressListener) System.out::println);
            buildLauncher.setStandardOutput(System.out);
            buildLauncher.forTasks("integrationTest");

            buildLauncher.run();
        }
    }
}
