package dvoraka.archbuilder.prototype.concurrency;

import dvoraka.archbuilder.prototype.concurrency.task.Tasks;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;

@SpringBootApplication
public class ConcurrencyApp {

    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyApp.class, args);
    }

    /**
     * Scenario 1:
     * <li>10 times task1 (unlimited thread count)</li>
     */
    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("Concurrency app");

            Tasks tasks = new Tasks();
            StopWatch stopWatch = new StopWatch("Concurrency app");
            warmup();

            // main thread
            System.out.println("Main thread:");
            stopWatch.start("main thread");
            for (int i = 0; i < 10; i++) {
                tasks.task1();
            }
            stopWatch.stop();

            System.out.println(stopWatch.prettyPrint());
        };
    }

    private void warmup() {

    }
}
