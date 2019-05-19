package ar.com.javacuriosities.labs.memory_allocation;

public class Entity {

    private int id;

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}