package ar.com.javacuriosities.labs.hidden_deadlock;

import ar.com.javacuriosities.utils.Utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SelfDeadlock {

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        Utils.start(args);
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();

        readLock.lock();
        writeLock.lock();
        Utils.waitForUser();
    }
}
