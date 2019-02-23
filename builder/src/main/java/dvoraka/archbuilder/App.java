package dvoraka.archbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import dvoraka.archbuilder.generate.Generator;
import dvoraka.archbuilder.generate.LangGenerator;
import dvoraka.archbuilder.generate.MainGenerator;
import dvoraka.archbuilder.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class App {

    @Autowired
    private Generator mainGenerator;


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("App");
            System.out.println(mainGenerator);

            // some example code will be here
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
    Generator mainGenerator(DirService dirService, LangGenerator langGenerator) {
        MainGenerator mainGenerator = new MainGenerator(dirService, langGenerator);
        mainGenerator.setRemoveClasses(false);

        return mainGenerator;
    }
}
