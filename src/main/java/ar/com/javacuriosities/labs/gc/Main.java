package ar.com.javacuriosities.labs.gc;

import ar.com.javacuriosities.utils.Utils;

public class Main {
    private static final int NUMBER_OF_THREADS = 2;

    public static void main(String[] args) {
        Utils.start(args);

        Utils.dispatchWorkers(NUMBER_OF_THREADS, Worker::new);

        Utils.waitForUser();
    }
}