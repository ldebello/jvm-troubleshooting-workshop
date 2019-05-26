package ar.com.javacuriosities.labs.multiple_classloaders;

public class DataObject {

    private long position;

    public DataObject() {
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        return (int) position;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DataObject) && (o.hashCode() == position);
    }
}
