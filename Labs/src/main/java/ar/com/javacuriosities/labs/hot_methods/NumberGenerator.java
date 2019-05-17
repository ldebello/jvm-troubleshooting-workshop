package ar.com.javacuriosities.labs.hot_methods;

import java.util.Collection;
import java.util.LinkedList;

public class NumberGenerator {
    private Collection<Integer> numbers;

    public NumberGenerator() {
        numbers = new LinkedList<>();
    }

    public void initialize(int module) {
        for (int i = 1; i < 10000; i++) {
            if ((i % module) != 0)
                numbers.add(i);
        }
    }

    public int countIntersection(NumberGenerator other) {
        int count = 0;
        for (Integer i : numbers) {
            if (other.numbers.contains(i)) {
                count++;
            }
        }
        return count;
    }
}
