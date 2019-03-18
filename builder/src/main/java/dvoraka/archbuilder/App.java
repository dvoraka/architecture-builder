package dvoraka.archbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import dvoraka.archbuilder.build.BuildTool;
import dvoraka.archbuilder.build.GradleBuildTool;
import dvoraka.archbuilder.generate.Generator;
import dvoraka.archbuilder.generate.LangGenerator;
import dvoraka.archbuilder.generate.MainGenerator;
import dvoraka.archbuilder.module.MicroserviceType1Template;
import dvoraka.archbuilder.module.Module;
import dvoraka.archbuilder.service.DirService;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.File;

/**
 * Template testing.
 */
@SpringBootApplication
public class App {

    @Autowired
    private Generator mainGenerator;
    @Autowired
    private SpringConfigGenerator configGenerator;


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Profile("!test")
    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("App");

            // micro-service template testing

            String rootDirName = "budget-service";
            String packageName = "test.budget";
            String serviceName = "Budget";

            Module template = new MicroserviceType1Template(
                    rootDirName,
                    packageName,
                    serviceName,
                    configGenerator
            );

            mainGenerator.generate(template.getRootDirectory());

            BuildTool buildTool = new GradleBuildTool(new File(rootDirName));
            buildTool.prepareEnv();
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        return objectMapper;
    }

    @Profile("test")
    @Bean
    public Generator mainGenerator(DirService dirService, LangGenerator langGenerator) {
        MainGenerator mainGenerator = new MainGenerator(dirService, langGenerator);
        mainGenerator.setRemoveClasses(false);

        return mainGenerator;
    }
}
