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
            System.out.println("App");

            Directory root = new Directory.DirectoryBuilder("")
                    .withType(DirType.ROOT)
                    .withParent(null)
                    .build();

            Directory srcRoot = new Directory.DirectoryBuilder("src/main/java")
                    .withType(DirType.SRC_ROOT)
                    .withParent(root)
                    .build();

            Directory srcBase = new Directory.DirectoryBuilder("dvoraka/testapp")
                    .withType(DirType.SRC_BASE)
                    .withParent(srcRoot)
                    .build();

            Directory service = new Directory.DirectoryBuilder("service")
                    .withType(DirType.SERVICE)
                    .withParent(srcBase)
                    .build();

            System.out.println(service.getPackageName());
        };
    }
}
