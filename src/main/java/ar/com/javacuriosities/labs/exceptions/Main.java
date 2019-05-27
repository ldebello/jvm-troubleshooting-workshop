package ar.com.javacuriosities.labs.exceptions;

import ar.com.javacuriosities.utils.Utils;

public class Main {

    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() - 1;

    public static void main(String[] args) {
        Utils.start(args);

        Utils.dispatchWorkers(NUMBER_OF_THREADS, Worker::new);

        Utils.waitForUser();
    }
}