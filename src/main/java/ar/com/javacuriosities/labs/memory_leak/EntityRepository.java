package ar.com.javacuriosities.labs.memory_leak;

import java.util.HashSet;
import java.util.Set;

public class EntityRepository {

    private Set<Entity> entities = new HashSet<>();

    public void put(Entity data) {
        if (!entities.contains(data)) {
            entities.add(data);
        }
    }
}