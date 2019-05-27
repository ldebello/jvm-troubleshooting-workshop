package ar.com.javacuriosities.labs.gc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Worker implements Runnable {
    private final static int COUNT = 10_000;

    private final int id;
    private final Map<Integer, MapContent> data;

    public Worker(int id) {
        this.id = id;
        data = createMapWithInstances(COUNT);
    }

    private static Map<Integer, MapContent> createMapWithInstances(int count) {
        Map<Integer, MapContent> map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            map.put(i, new MapContent(i));
        }
        return map;
    }

    @Override
    public void run() {
        long yieldCounter = 0;
        while (true) {
            Collection<MapContent> values = data.values();
            for (MapContent c : values) {
                if (!data.containsKey(c.getId())) {
                    System.out.println("Now this is strange!");
                }
                if (++yieldCounter % 1000 == 0) {
                    Thread.yield();
                }

            }
        }
    }
}