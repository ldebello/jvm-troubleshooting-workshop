package ar.com.javacuriosities.labs.memory_visibility;

public class Meeting {

    private boolean bossArrived = false;

    public Meeting() {
    }

    public void waitBoss(String name) {

        System.out.println(name + " is waiting");
        while (!bossArrived) {
        }

        System.out.println(name + " says: Good Morning boss");
    }

    public void startMeeting(String name) {
        System.out.println(name + " says: Good Morning slaves :)");
        bossArrived = true;
    }
}
