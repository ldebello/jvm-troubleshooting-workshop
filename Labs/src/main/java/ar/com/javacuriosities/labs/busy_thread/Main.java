package ar.com.javacuriosities.labs.busy_thread;

import ar.com.javacuriosities.utils.Utils;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Utils.start(args);

        new Thread(new Idle(), "Idle").start();
        new Thread(new Busy(), "Busy").start();
    }

    private static class Idle implements Runnable {

        @Override
        public void run() {
            try {
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    private static class Busy implements Runnable {
        @Override
        public void run() {
            while (true) {
                "Foo".matches("F.*");
            }
        }
    }
}
