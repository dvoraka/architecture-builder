package dvoraka.archbuilder.prototype.net;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NetComponentApp {

    public static void main(String[] args) {
        SpringApplication.run(NetComponentApp.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("Net app");
        };
    }
}
