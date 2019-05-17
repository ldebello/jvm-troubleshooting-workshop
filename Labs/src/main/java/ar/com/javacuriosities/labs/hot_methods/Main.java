package ar.com.javacuriosities.labs.hot_methods;

import ar.com.javacuriosities.utils.Utils;

public class Main {

    private static final int NUMBER_OF_THREADS = 4;

    public static void main(String[] args) {
        Utils.start(args);

        Utils.dispatchWorkers(NUMBER_OF_THREADS, id -> new Worker(id));
        
        Utils.waitForUser();
    }
}
