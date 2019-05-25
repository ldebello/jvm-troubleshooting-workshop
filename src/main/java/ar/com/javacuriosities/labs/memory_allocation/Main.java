package ar.com.javacuriosities.labs.memory_allocation;

import ar.com.javacuriosities.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static final int COUNT = 10_000;

    public static void main(String[] args) {
        Utils.start(args);

        EntityRepository repository = new EntityRepository();

        int counter = 0;

        while (true) {
            Map<Integer, Entity> data = createMapWithInstances(COUNT);
            Collection<Entity> values = data.values();

            for (int i = 0; i < 10; i++) {
                for (Entity e : values) {
                    if (e.getId() % 10 == 0) {
                        repository.put(e);
                    }
                }
            }

            counter++;
            if (counter % 100 == 0) {
                System.out.println("Iterations: " + counter);
            }
        }
    }

    private static Map<Integer, Entity> createMapWithInstances(int count) {
        Map<Integer, Entity> data = new HashMap<>();
        for (int i = 0; i < count; i++) {
            data.put(i, new Entity(i));
        }
        return data;
    }
}
