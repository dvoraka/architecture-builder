package dvoraka.archbuilder.prototype.concurrency;

import dvoraka.archbuilder.prototype.concurrency.task.Tasks;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class ConcurrencyApp {

    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyApp.class, args);
    }

    /**
     * Scenario 1:
     * <li>6 times task1 (unlimited thread count)</li>
     * Scenario 2:
     * <li>chaining task1 to task2</li>
     * Scenario 3:
     * <li>comparing thread count effectivity</li>
     */
    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("Concurrency app");

            scenario1();
        };
    }

    private void scenario1() throws InterruptedException, ExecutionException {

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

        // executor service - 1 thread
        System.out.println("executor service 1 thread...");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        stopWatch.start("executor service 1 thread");
        Future<?> future = executorService.submit(() -> {
            for (int i = 0; i < loops; i++) {
                tasks.task1();
            }
        });
        future.get();
        stopWatch.stop();

        // executor service - 6 threads
        System.out.println("executor service 6 thread...");
        ExecutorService executorService2 = Executors.newFixedThreadPool(loops);
        stopWatch.start("executor service 6 threads");
        IntStream.range(0, loops)
                .mapToObj(loop -> executorService2.submit(tasks::task1))
                .collect(Collectors.toList())
                .forEach(stringFuture -> {
                    try {
                        stringFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });
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

            // completable future - common pool
            System.out.println("completable future...");
            stopWatch.start("completable future");
            CompletableFuture.allOf(IntStream.range(0, loops)
                    .mapToObj(loop -> CompletableFuture.runAsync(tasks::task1))
                    .toArray(CompletableFuture[]::new))
                    .get();
            stopWatch.stop();

            // Reactor simple
            System.out.println("Reactor simple...");
            stopWatch.start("Reactor simple");
            Mono.fromRunnable(() -> {
                for (int i = 0; i < loops; i++) {
                    tasks.task1();
                }
            })
                    .subscribe();
            stopWatch.stop();

        System.out.println();
        System.out.println(stopWatch.prettyPrint());

        executorService.shutdown();
        executorService2.shutdown();
    }

    private void warmup() {

    }
}
