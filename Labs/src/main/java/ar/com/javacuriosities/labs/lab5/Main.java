package ar.com.javacuriosities.labs.lab5;

import ar.com.javacuriosities.utils.Utils;

public class Main {

    private static final int NUMBER_OF_THREADS = 1;

    public static void main(String[] args) {
        Utils.start(args);

        Utils.dispatchWorkers(NUMBER_OF_THREADS, false, id -> new Worker(id));

        Utils.addShutdownHook(() -> System.exit(0));

        Utils.waitForUser();
    }
}
