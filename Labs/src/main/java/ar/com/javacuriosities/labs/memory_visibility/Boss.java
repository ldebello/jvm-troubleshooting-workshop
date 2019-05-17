package ar.com.javacuriosities.labs.memory_visibility;

public class Boss implements Runnable {

    private final int id;
    private final Meeting meeting;

    public Boss(int id, Meeting meeting) {
        this.id = id;
        this.meeting = meeting;
    }

    @Override
    public void run() {
        String name = "The Boss";
        System.out.printf("%s arrives at the office.%n", name);
        meeting.startMeeting(name);
    }
}
