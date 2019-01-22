package dvoraka.archbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import dvoraka.archbuilder.generate.Generator;
import dvoraka.archbuilder.generate.LangGenerator;
import dvoraka.archbuilder.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    @Autowired
    private DirService dirService;
    @Autowired
    private LangGenerator langGenerator;
    @Autowired
    private Generator generator;


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("App");
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
