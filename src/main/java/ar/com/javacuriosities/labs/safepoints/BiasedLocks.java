package ar.com.javacuriosities.labs.safepoints;

import java.util.concurrent.locks.LockSupport;
import java.util.stream.Stream;

/**
 * Ejecutar este ejemplo con los siguientes parámetros
 * -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDetails
 *
 * Vamos a notar que hay muchos STW pero no hay actividad del GC, esto se debe a que -XX:+PrintGCApplicationStoppedTime
 * muestra todas las STW pauses.
 *
 * Para obtener mas detalle acerca de lo que esta pasando vamos a utilizar
 * -XX:+PrintSafepointStatistics  -XX:PrintSafepointStatisticsCount=1
 *
 * Esto nos va a revelar que los safepoints son por biased lock revocations.
 *
 * Biased locks esta activado por defecto para desactivarlo podemos usar -XX:-UseBiasedLocking
 */
public class BiasedLocks {

    private static synchronized void contend() {
        LockSupport.parkNanos(100_000);
    }

    public static void main(String[] args) {
        try {
            Thread.sleep(5_000); // Hacemos un sleep inicial por BiasedLockingStartupDelay

            Stream.generate(() -> new Thread(BiasedLocks::contend))
                    .limit(10)
                    .forEach(Thread::start);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}