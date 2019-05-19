package ar.com.javacuriosities.labs.latency;

import ar.com.javacuriosities.labs.latency.Connection.ConnectionFactory;

import java.util.Random;

public class Worker implements Runnable {

    private static final int LOOP_COUNT = 60_000_000;
    private static final Random random = new Random();

    private int id;
    private long totalTime;
    private int counter = 1;

    public Worker(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            trace(() -> {
                int result = cpuIntensiveTask();
                Connection connection = ConnectionFactory.getConnection();
                connection.executeQuery(id, counter, result);
            });
        }
    }

    public int getId() {
        return id;
    }

    public int getRequestsCount() {
        return counter;
    }

    public long getAverageExecutionTime() {
        return totalTime / counter;
    }

    public int cpuIntensiveTask() {
        int x = 1;
        int y = 1;
        for (int i = 1; i < random.nextInt(LOOP_COUNT); i++) {
            x += 1;
            y = y % (LOOP_COUNT + 3);
            if (x % (LOOP_COUNT + 4) == 0 || y == 0) {
                System.out.println("Should not happen, used to avoid code optimization");
            }
        }
        return x + y;
    }

    private void trace(Runnable code) {
        long start = System.currentTimeMillis();
        code.run();
        long end = System.currentTimeMillis();
        totalTime = totalTime + (end - start);
        counter++;
    }
}
