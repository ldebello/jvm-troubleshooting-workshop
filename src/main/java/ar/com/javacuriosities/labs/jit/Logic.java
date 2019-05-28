package ar.com.javacuriosities.labs.jit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Logic {

    private volatile boolean condition = true;

    private int intValue = (int) (Math.random() * Integer.MAX_VALUE);
    private double doubleValue = Math.random() * Integer.MAX_VALUE;

    public int inlining(int a, int b) {
        return a + b;
    }

    public double deadCode() {
        for (int i = 0; i < 1_000_000; i++) ;
        return Math.sqrt(doubleValue);
    }

    public int autoboxingElimination() {
        return new Integer(intValue);
    }

    public int escapeAnalysis() {
        return new Convict(intValue).id;
    }

    public void lockElision() {
        long total = 0;
        Object object = new Object();
        Blackhole bh = new Blackhole();
        List<Integer> data = new ArrayList<>();
        double random = Math.random() * 200_000;
        synchronized (object) {
            for (int i = 0; i < random; i++) {
                total += i;
            }
        }
        data.add(Long.valueOf(total).intValue());
        bh.consume(data);
    }

    public void loopConditionals() {
        Blackhole bh = new Blackhole();
        final boolean condition = this.condition;

        for (int i = 0; i < 100_000; i++) {
            if (condition) {
                bh.consume(new ArrayList<>());
            } else {
                bh.consume(new LinkedList<>());
            }
        }
    }

    public static class Convict {
        public final int id;

        public Convict(int id) {
            this.id = id;
        }
    }

    public static class Blackhole {
        public void consume(List<Integer> data) {
            double random = Math.random() * 300_000;
            for (int i = 0; i < random; i++) {
                data.add(i);
            }
        }
    }
}
