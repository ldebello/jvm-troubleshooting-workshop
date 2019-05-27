package ar.com.javacuriosities.labs.latency;

import ar.com.javacuriosities.utils.Utils;

import java.util.List;

public class Main {

    private static final int NUMBER_OF_THREADS = 10;

    public static void main(String[] args) {
        Utils.start(args);

        List<Worker> workers = Utils.dispatchWorkers(NUMBER_OF_THREADS, Worker::new);

        Utils.addShutdownHook(() -> {
            Connection.shutdown();

            System.out.println("Total number of requests: " + workers.stream().mapToInt(Worker::getRequestsCount).sum());
            System.out.println("Average execution time: " + workers.stream().mapToLong(Worker::getAverageExecutionTime).average().getAsDouble());
        });

        Utils.waitForUser();
    }
}
