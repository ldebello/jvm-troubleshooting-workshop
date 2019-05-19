package ar.com.javacuriosities.labs.memory_visibility;

public class Worker implements Runnable {

    private final int id;
    private final Meeting meeting;

    public Worker(int id, Meeting meeting) {
        this.id = id;
        this.meeting = meeting;
    }

    @Override
    public void run() {
        String name = "Worker " + id;
        System.out.printf("%s arrives at the office.%n", name);
        meeting.waitBoss(name);
    }
}
