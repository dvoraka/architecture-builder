package dvoraka.archbuilder.prototype.net;

import dvoraka.archbuilder.prototype.data.net.PBalanceNetComponent;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableRabbit
@SpringBootApplication(scanBasePackages = "dvoraka.archbuilder.prototype")
public class NetComponentApp {

    @Autowired
    private PBalanceNetComponent netComponent;


    public static void main(String[] args) {
        SpringApplication.run(NetComponentApp.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("Net app");
            System.out.println(netComponent);

            netComponent.addMessageListener((message, acknowledgment) -> {
                System.out.println("Listener: " + message);
            });
        };
    }
}
