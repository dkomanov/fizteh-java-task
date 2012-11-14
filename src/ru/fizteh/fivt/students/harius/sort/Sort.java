package ru.fizteh.fivt.students.harius.sort;

import java.util.concurrent.*;
import java.io.IOException;
import java.util.*;

public class Sort {
    private static final int nMerge = 3;

    public static void main(String[] args) {
        ExecutorService manager =
            Executors.newFixedThreadPool(10);
        CompletionService<List<String>> monitor =
            new ExecutorCompletionService<>(manager);

        List<Future<List<String>>> readers = new ArrayList<>(); 

        if (args.length == 0) {
            readers.add(monitor.submit(LinesInputFactory.create()));
        } else {
            for (String file : args) {
                try {
                    readers.add(monitor.submit(LinesInputFactory.create(file)));
                } catch (IOException ioEx) {
                    System.err.println("Cannot open " + file);
                    System.exit(1);
                }
            }
        }

        List<Future<List<String>>> sorters = new ArrayList<>();
        List<Future<List<String>>> mergers = new ArrayList<>();

        List<List<String>> mergeQueue = new ArrayList<>();

        while(true) {
            try {
                Future<List<String>> done = monitor.take();
                if (readers.contains(done)) {
                    System.err.println("Reader");
                    List<String> result = done.get();
                    sorters.add(monitor.submit(new LinesSort(done.get())));
                } else if (sorters.contains(done) || mergers.contains(done)) {
                    System.err.println("Sorter or merger");
                    mergeQueue.add(done.get());
                } else {
                    System.err.println("Internal error: unexpected thread");
                    System.exit(1);
                }
                System.err.println(mergeQueue.size());
                if (mergeQueue.size() == nMerge) {
                    mergers.add(monitor.submit(new LinesMerge(mergeQueue)));
                    mergeQueue = new ArrayList<>();
                }
            } catch (InterruptedException interrupted) {
                System.err.println("Thread was interrupted unexpectedly");
                System.exit(1);
            } catch (ExecutionException exec) {
                System.err.println("Execution error: " + exec.getMessage());
                System.exit(1);
            }
        }
    }
}