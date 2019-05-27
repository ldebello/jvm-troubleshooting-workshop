package ar.com.javacuriosities.labs.exceptions;

public class Worker implements Runnable {

    private int id;
    private long scaryCounter;

    public Worker(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        loop();
    }

    private void loop() {
        while (true) {
            try {
                doStuff();
            } catch (Exception e) {
                // Evilly swallow the exception.
            }
            sleep(1);
        }
    }

    private void doStuff() throws Exception {
        if (isScary()) {
            doScaryThing();
        } else {
            throwMe();
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isScary() {
        return ++scaryCounter % 10 == 0;
    }

    private void throwMe() throws CommonException {
        throw new CommonException("Average exception message!");
    }

    private void doScaryThing() throws ScaryException {
        throw new ScaryException("Really quite scary exception message!!!");
    }
}
