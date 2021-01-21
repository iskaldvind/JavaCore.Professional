import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private final static String MONITOR = "syncLock";

    private static volatile String current = "A";
    private static final int LOOPS = 5;

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(() -> { printAndSwitch("A", "B"); });
        executorService.submit(() -> { printAndSwitch("B", "C"); });
        executorService.submit(() -> { printAndSwitch("C", "A"); });
        executorService.shutdown();
    }

    private static void printAndSwitch(String waited, String next) {
        try {
            for (int i = 0; i < LOOPS; i++) {
                synchronized (MONITOR) {
                    while (!current.equals(waited)) {
                        MONITOR.wait();
                    }
                    System.out.print(waited);
                    current = next;
                    MONITOR.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
