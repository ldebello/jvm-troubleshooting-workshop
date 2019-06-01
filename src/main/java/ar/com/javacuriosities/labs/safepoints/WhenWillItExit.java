package ar.com.javacuriosities.labs.safepoints;

/**
 * Ejemplo extraído de http://psy-lob-saw.blogspot.com/2016/02/wait-for-it-counteduncounted-loops.html
 */
public class WhenWillItExit {

    public static void main(String[] arg) {
        try {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100_000; i++) {
                countOdds(10);
            }
            Thread t = new Thread(() -> {
                long l = countOdds(Integer.MAX_VALUE);
                System.out.println("How Odd:" + l);
            });
            t.setDaemon(true);
            t.start();
            System.out.println("Before Sleep");
            Thread.sleep(5000);
            System.out.println("After Sleep");
            long end = System.currentTimeMillis();
            System.out.println("Total Time: " + (end - start));
            System.out.println("Program Done");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static long countOdds(int limit) {
        long l = 0;
        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < limit; j++) {
                if ((j & 1) == 1) {
                    l++;
                }
            }
        }
        return l;
    }
}
