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

//        try (ProjectConnection connection = GradleConnector.newConnector()
//                // gradle root of the new project (with settings.gradle file)
//                .forProjectDirectory(new File("testRoot"))
//                // you can set Gradle version, default is current project's version
////                .useDistribution(URI.create("https://services.gradle.org/distributions/gradle-5.0-all.zip"))
//                .connect()) {
//
//            GradleProject project = connection.getModel(GradleProject.class);
//
//            DomainObjectSet<? extends GradleTask> tasks = project.getTasks();
//            tasks.forEach(System.out::println);
//
//            BuildLauncher buildLauncher = connection.newBuild();
////                buildLauncher.addProgressListener((ProgressListener) System.out::println);
//            buildLauncher.setStandardOutput(System.out);
//            buildLauncher.forTasks("wrapper");
//
//            buildLauncher.run();

        BuildTool buildTool = new GradleBuildTool(new File("."));
        buildTool.prepareEnv();
    }
}
