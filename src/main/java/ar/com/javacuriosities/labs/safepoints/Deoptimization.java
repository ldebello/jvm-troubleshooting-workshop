package ar.com.javacuriosities.labs.safepoints;

/**
 * Ejecutar este ejemplo con los siguientes parámetros
 * -XX:+PrintSafepointStatistics  -XX:PrintSafepointStatisticsCount=1 -XX:+PrintCompilation
 *
 * Vamos a notar que no aparece ningun safepoint por deoptimizzation, esto se debe a que la JVM
 * hace trigger de "no vm operation" cada un minuto.
 *
 * Si desactivamos esto
 * -XX:+UnlockDiagnosticVMOptions -XX:GuaranteedSafepointInterval=0
 *
 * Podremos ver el safepoint de deoptimization
 */
public class Deoptimization {

    public static void main(String[] args) {
        runManyTimes(new Burner1()); // JIT asume de forma especulativa un monomorphic callsite
        runManyTimes(new Burner2()); // La suposición falla
    }

    private static void runManyTimes(Burner burner) {
        for (int i = 0; i < 100_000; i++) {
            burner.burn(10);
        }
    }

    private interface Burner {
        void burn(long nanos);
    }

    private static class Burner1 implements Burner {

        @Override
        public void burn(long nanos) {
            long endTime = System.nanoTime() + nanos;
            while (System.nanoTime() < endTime) ;
        }
    }

    private static class Burner2 implements Burner {
        @Override
        public void burn(long nanos) {

        }
    }
}