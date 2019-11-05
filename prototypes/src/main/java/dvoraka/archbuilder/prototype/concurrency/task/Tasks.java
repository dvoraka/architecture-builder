package dvoraka.archbuilder.prototype.concurrency.task;

public class Tasks {

    public String task1() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "task1";
    }

    public Integer task2(String data) {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 2;
    }

    public String task5() {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "task5";
    }

    public String task10() {
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "task10";
    }

    public String taskE() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }
}
