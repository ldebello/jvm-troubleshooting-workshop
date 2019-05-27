package ar.com.javacuriosities.labs.biased_locks;

import java.util.concurrent.locks.LockSupport;
import java.util.stream.Stream;

public class Main {
    private static synchronized void contend() {
        LockSupport.parkNanos(100_000);
    }

    public static void main(String[] args) throws InterruptedException {

        Thread.sleep(5_000);

        Stream.generate(() -> new Thread(Main::contend))
                .limit(10)
                .forEach(Thread::start);
    }
}
