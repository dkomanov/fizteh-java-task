package ru.fizteh.fivt.students.harius.sort;

import java.util.concurrent.*;
import java.io.IOException;
import java.util.*;

public class Sort {
    private static final int nMerge = 3;

    public static void main(String[] args) {
        ExecutorService manager =
            Executors.newSingleThreadExecutor();
        CompletionService<List<String>> monitor =
            new ExecutorCompletionService<>(manager);

        int trace = 0;

        if (args.length == 0) {
            monitor.submit(LinesInputFactory.create());
            ++trace;
        } else {
            for (String file : args) {
                try {
                    monitor.submit(LinesInputFactory.create(file));
                    ++trace;
                } catch (IOException ioEx) {
                    System.err.println("Cannot open " + file);
                    System.exit(1);
                }
            }
        }

        Queue<List<String>> mergeQueue =
            new java.util.concurrent.SynchronousQueue<>();

        while(trace > 0) {
            try {
                Future<List<String>> future = monitor.take();
                List<String> next = future.get();
                --trace;
                if (next != null) {
                    monitor.submit(new LinesSort(next, mergeQueue), null);
                    ++trace;
                }
            } catch (InterruptedException inter) {
                System.err.println("Unexpected thread interrupt");
                System.exit(1);
            } catch (ExecutionException exec) {
                System.err.println("Execution error");
                System.exit(1);
            }
        }

        System.err.println(mergeQueue);
    }
}