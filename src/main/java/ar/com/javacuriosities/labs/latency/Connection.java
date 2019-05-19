package ar.com.javacuriosities.labs.latency;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Connection {

    private static final int WORKER_THREADS = 2;
    private static final int SHARED_RESOURCE_TIME = 200;
    private static final int QUERY_EXECUTION_TIME = 400;

    private static Connection INSTANCE = new Connection();

    private static ExecutorService executorService = Executors.newFixedThreadPool(WORKER_THREADS, new ConnectionDefaultThreadFactory());

    private Connection() {
    }

    public static void shutdown() {
        executorService.shutdownNow();
    }

    public synchronized void executeQuery(int id, int counter, int value) {
        Runnable runnable = () -> {
            System.out.printf("Executing complex database operation on worker %d -> request %d (result: %d) %n", id, counter, value);
            try {
                // Simulate that this takes a little while.
                ioIntensiveWork();
            } catch (Exception e) {
                // Don't care.
            }
        };

        try {
            Future<?> future = executorService.submit(runnable);
            future.get();
        } catch (Exception e) {
            // Don't care.
        }
    }

    private void ioIntensiveWork() throws IOException {
        URL url = new URL("http://slowwly.robertomurray.co.uk/delay/" + QUERY_EXECUTION_TIME + "/url/http://www.google.com");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.getResponseCode();
    }

    private void ioSimulatingWork() throws InterruptedException {
        Thread.sleep(QUERY_EXECUTION_TIME);
    }

    public static class ConnectionFactory {
        public static synchronized Connection getConnection() {
            try {
                // Simulate that this takes a little while.
                Thread.sleep(SHARED_RESOURCE_TIME);
            } catch (InterruptedException e) {
                // Don't care.
            }
            return INSTANCE;
        }
    }

    private static class ConnectionDefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ConnectionDefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
