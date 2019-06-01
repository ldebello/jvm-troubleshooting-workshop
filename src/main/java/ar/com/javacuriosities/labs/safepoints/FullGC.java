package ar.com.javacuriosities.labs.safepoints;


import java.util.ArrayList;
import java.util.Collection;

/**
 * Ejecutar este ejemplo con los siguientes parámetros
 * -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDetails
 *
 * Vamos a notar que todos los SWT coinciden con GC pauses.
 */
public class FullGC {

    private static final Collection<Object> leak = new ArrayList<>();
    private static volatile Object sink;

    public static void main(String[] args) {
        while (true) {
            try {
                leak.add(new byte[1024 * 1024]);

                sink = new byte[1024 * 1024];
            } catch (OutOfMemoryError e) {
                leak.clear();
            }
        }
    }
}