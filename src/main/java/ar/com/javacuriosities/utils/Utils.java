package ar.com.javacuriosities.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Utils {

    private static String TIMEOUT_PARAMETER = "-t";

    private static long DEFAULT_EXIT_TIMEOUT_IN_SECONDS = 180;

    public static void start(String[] args) {
        System.out.println("Running Application...");

        long timeout = 0;
        boolean hasTimeout = Arrays.stream(args).anyMatch(arg -> arg.equals(TIMEOUT_PARAMETER));
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(TIMEOUT_PARAMETER)) {
                if (i + 1 < args.length) {
                    timeout = Long.valueOf(args[i + 1]);
                }
            }
        }

        if (hasTimeout) {
            timeout = timeout != 0 ? timeout : Utils.DEFAULT_EXIT_TIMEOUT_IN_SECONDS;
            Utils.exitApplication(timeout);
            System.out.printf("Application will run for %d seconds %n", timeout);
        }


    }

    public static void exitApplication(long timeout) {
        if (timeout > 0) {
            Timer timer = new Timer();
            timer.schedule(new ExitApplicationTask(), TimeUnit.SECONDS.toMillis(timeout));
        }
    }

    public static <T extends Runnable> List<T> dispatchWorkers(int numberOfThreads, Function<Integer, T> provider) {
       return dispatchWorkers(numberOfThreads, provider, true);
    }

    public static <T extends Runnable> List<T> dispatchWorkers(int numberOfThreads, Function<Integer, T> provider, boolean daemon) {
        List<T> workers = new ArrayList<>();

        ThreadGroup threadGroup = new ThreadGroup("Workers");
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            T worker = provider.apply(i);
            workers.add(worker);

            threads[i] = new Thread(threadGroup, worker, String.format("Worker Thread %d", i));
            threads[i].setDaemon(daemon);
            threads[i].start();
        }

        return workers;
    }

    public static void waitForUser() {
        System.out.println("Press enter to exit!!!");
        System.out.flush();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addShutdownHook(Runnable r) {
        Runtime.getRuntime().addShutdownHook(new Thread(r));
    }

    private static class ExitApplicationTask extends TimerTask {

        private long startTime;

        public ExitApplicationTask() {
            startTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            System.out.println("Exiting application now...");
            System.out.println("Total time: " + (System.currentTimeMillis() - startTime));
            System.exit(0);
        }
    }
}
