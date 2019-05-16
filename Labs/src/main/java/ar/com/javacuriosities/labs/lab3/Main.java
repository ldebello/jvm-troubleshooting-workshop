package ar.com.javacuriosities.labs.lab3;

import ar.com.javacuriosities.utils.Utils;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    // Objeto usado para flat lock
    private static final Object mutex = new Object();
    // Lock para Write & Read
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        Utils.start(args);

        Thread worker1 = new Thread(() -> lockReadFlat());
        Thread worker2 = new Thread(() -> lockFlatWrite());

        worker1.start();
        worker2.start();
    }

    public static void lockReadFlat() {
        System.out.println("Worker 1 - Attempt to acquire a Read lock");
        lock.readLock().lock();

        System.out.println("Worker 1 - Sleep for a while");
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Worker 1 - Attempt to acquire a Flat lock");
            synchronized (mutex) {
            }
        } finally {
            System.out.println("Worker 1 - Release Read lock");
            lock.readLock().unlock();
        }
        System.out.println("Worker 1 Done!");
    }

    public static void lockFlatWrite() {
        System.out.println("Worker 2 - Attempt to acquire a Flat lock");
        synchronized (mutex) {

            System.out.println("Worker 2 - Sleep for a while");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Worker 2 - Attempt to acquire a Write lock");
            lock.writeLock().lock();
            try {
                // Do nothing
            } finally {
                System.out.println("Worker 2 - Release Write lock");
                lock.writeLock().unlock();
            }
        }
        System.out.println("Worker 2 Done!");
    }

}
