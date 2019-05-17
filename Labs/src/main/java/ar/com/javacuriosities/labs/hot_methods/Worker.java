package ar.com.javacuriosities.labs.hot_methods;

import java.util.Random;

public class Worker implements Runnable {

    private final int id;

    public Worker(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            Random r = new Random();
            NumberGenerator g1 = new NumberGenerator();
            NumberGenerator g2 = new NumberGenerator();
            g1.initialize(r.nextInt(7) + 3);
            g2.initialize(r.nextInt(7) + 5);
            int intersectionSize = g1.countIntersection(g2);
            System.out.println("Intersection Size: " + intersectionSize);
            Thread.yield();
        }
    }
}
