package ar.com.javacuriosities.labs.memory_allocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static final int COUNT = 10_000;

    public static void main(String[] args) {
        EntityRepository repository = new EntityRepository();

        int counter = 0;

        while (true) {
            Map<Integer, Entity> data = createMapWithInstances(COUNT);
            Collection<Entity> values = data.values();


            for (Entity e : values) {
                if (!data.containsKey(e.getId())) {
                    System.out.println("This can not happen!!!");
                }
            }

            for (int i = 0; i < 10; i++) {
                for (Entity e : values) {
                    if (data.get(e.getId()).toString().length() == 1) {
                        System.out.println("This can not happen!!!");
                    }
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
