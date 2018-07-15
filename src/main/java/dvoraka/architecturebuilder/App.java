package dvoraka.architecturebuilder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {

            Directory root = new Directory.DirectoryBuilder("rootDir")
                    .withType(DirType.ROOT)
                    .withParent(null)
                    .build();

            Directory srcRoot = new Directory.DirectoryBuilder("src/main/java")
                    .withType(DirType.SRC_ROOT)
                    .withParent(root)
                    .build();

            Directory srcTestRoot = new Directory.DirectoryBuilder("src/test/groovy")
                    .withType(DirType.SRC_TEST_ROOT)
                    .withParent(root)
                    .build();

            Directory srcBase = new Directory.DirectoryBuilder("dvoraka/testapp")
                    .withType(DirType.SRC_BASE)
                    .withParent(srcRoot)
                    .build();

            Directory srcTestBase = new Directory.DirectoryBuilder("dvoraka/testapp")
                    .withType(DirType.SRC_TEST_BASE)
                    .withParent(srcTestRoot)
                    .build();

            Directory service = new Directory.DirectoryBuilder("service")
                    .withType(DirType.SERVICE)
                    .withParent(srcBase)
                    .build();

            System.out.println(service.getPackageName());
            System.out.println(service.getPath());

            processDirs(root);
        };
    }

    public void processDirs(Directory root) {
        if (root.getChildren().isEmpty()) {
            processLeaf(root);
        } else {
            for (Directory dir : root.getChildren()) {
                processDirs(dir);
            }
            process(root);
        }
    }

    public void processLeaf(Directory directory) {
        System.out.println("Processing leaf: " + directory.getName());
    }

    public void process(Directory directory) {
        System.out.println("Processing: " + directory.getName());
    }
}
