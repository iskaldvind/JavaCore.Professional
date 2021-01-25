import java.util.concurrent.Semaphore;

public class Tunnel extends Stage {

    private Semaphore sem;

    public Tunnel(Semaphore sem) {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
        this.sem = sem;
    }

    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                sem.acquire();
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(c.getName() + " закончил этап: " + description);
                sem.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}