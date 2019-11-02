package dvoraka.archbuilder.prototype.concurrency;

import dvoraka.archbuilder.prototype.concurrency.task.Tasks;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class ConcurrencyApp {

    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyApp.class, args);
    }

    /**
     * Scenario 1:
     * <li>6 times task1 (unlimited thread count)</li>
     */
    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("Concurrency app");

            final int loops = 6;
            final StopWatch stopWatch = new StopWatch("Concurrency app");
            Tasks tasks = new Tasks();

            warmup();

            // main thread
            System.out.println("main thread...");
            stopWatch.start("main thread");
            for (int i = 0; i < loops; i++) {
                tasks.task1();
            }
            stopWatch.stop();

            // new thread
            System.out.println("new thread...");
            Runnable runnable = () -> {
                for (int i = 0; i < loops; i++) {
                    tasks.task1();
                }
            };
            Thread thread1 = new Thread(runnable);
            stopWatch.start("new thread");
            thread1.start();
            thread1.join();
            stopWatch.stop();

            // completable future simple
            System.out.println("completable future simple...");
            stopWatch.start("completable future simple");
            CompletableFuture.runAsync(() -> {
                for (int i = 0; i < loops; i++) {
                    tasks.task1();
                }
            }).get();
            stopWatch.stop();

            System.out.println(stopWatch.prettyPrint());
        };
    }

    private void warmup() {

    }
}
