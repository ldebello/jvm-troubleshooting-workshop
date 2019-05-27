package ar.com.javacuriosities.labs.safepoints;

import ar.com.javacuriosities.utils.Utils;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Utils.start(args);

        BiasedLocks.main(args);
    }
}
