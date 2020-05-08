package ar.com.javacuriosities.labs.out_of_memory;

import ar.com.javacuriosities.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static List<Entity> entities = new ArrayList<>();

    public static void main(String[] args) {
        Utils.start(args);

        Random random = new Random();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            int id = random.nextInt(Integer.MAX_VALUE);
            entities.add(new Entity(id, "Value" + id));
        }
    }

    private static class Entity {

        private Integer id;

        private String value;

        public Entity(Integer id, String value) {
            this.id = id;
            this.value = value;
        }
    }
}
