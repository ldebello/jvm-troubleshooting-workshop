package ar.com.javacuriosities.labs.memory_visibility;

import ar.com.javacuriosities.utils.Utils;

public class Main {

    public static void main(String[] args) {
        try {
            Meeting meeting = new Meeting();

            Utils.dispatchWorkers(3, id -> new Worker(id, meeting), false);

            Thread.sleep(2000);

            Utils.dispatchWorkers(1, id -> new Boss(id, meeting), false);
        } catch (InterruptedException e) {
            // Log and Handle exception
            e.printStackTrace();
        }
    }
}