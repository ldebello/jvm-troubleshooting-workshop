package ar.com.javacuriosities.labs.zombie_process;

public class Worker implements Runnable {

    private final int id;

    public Worker(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
