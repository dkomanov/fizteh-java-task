package ru.fizteh.fivt.students.harius.sort;

import java.util.concurrent.*;
import java.io.*;
import java.util.*;
import ru.fizteh.fivt.students.harius.argparse.*;

class SortSettings {
    @Flag(name = "-i")
    public boolean caseInsensitive = false;

    @Flag(name = "-u")
    public boolean uniqueOnly = false;

    @IntOpt(name = "-t")
    public int threads = Runtime.getRuntime().availableProcessors();

    @StrOpt(name = "-o")
    public String output = null;

    @TailOpt
    public List<String> files = null;
}

class SensitiveComp implements Comparator<String> {
    public int compare(String s1, String s2) {
        return s1.compareTo(s2);
    }
}

public class Sort {
    private final static int maxChunk = 200000;

    public static void main(String[] args) {
        Argparser parser = new Argparser(args);
        SortSettings settings = new SortSettings();
        try {
            parser.load(settings);
        } catch (ArgparseException parseEx) {
            System.err.println("Argument parsing exception: " + parseEx.getMessage());
            System.exit(1);
        }

        ExecutorService manager =
            Executors.newFixedThreadPool(settings.threads);
        CompletionService<List<String>> monitor =
            new ExecutorCompletionService<>(manager);

        int trace = 0;

        if (settings.files.isEmpty()) {
            monitor.submit(LinesInputFactory.create());
            ++trace;
        } else {
            for (String file : settings.files) {
                try {
                    monitor.submit(LinesInputFactory.create(file));
                    ++trace;
                } catch (IOException ioEx) {
                    System.err.println("Expected filename, got " + file);
                    System.exit(1);
                }
            }
        }

        Queue<List<String>> mergeQueue =
            new java.util.concurrent.LinkedBlockingQueue<>();

        Comparator<String> comp = settings.caseInsensitive ? 
            String.CASE_INSENSITIVE_ORDER : new SensitiveComp();

        while (trace > 0) {
            try {
                Future<List<String>> future = monitor.take();
                List<String> next = future.get();
                --trace;
                if (next != null) {
                    if (next.size() > maxChunk) {
                        int chunkStart = 0;
                        while (chunkStart < next.size()) {
                            int chunkEnd = Math.min(chunkStart + maxChunk, next.size());
                            monitor.submit(new LinesSort(
                                next.subList(chunkStart, chunkEnd), mergeQueue, comp), null);
                            ++trace;
                            chunkStart = chunkEnd;
                        }
                    } else {
                        monitor.submit(new LinesSort(next, mergeQueue, comp), null);
                        ++trace;
                    }
                }
            } catch (InterruptedException inter) {
                System.err.println("Unexpected thread interrupt");
                System.exit(1);
            } catch (ExecutionException exec) {
                System.err.println("Execution error: " + exec.getMessage());
                System.exit(1);
            }
        }

        while (mergeQueue.size() > 1 || trace > 0) {
            if (mergeQueue.size() > 1) {
                List<String> li1 = mergeQueue.poll();
                List<String> li2 = mergeQueue.poll();
                monitor.submit(new LinesMerge(li1, li2, mergeQueue, comp), null);
            }
            try {
                Future<List<String>> future = monitor.take();
                future.get();
            } catch (InterruptedException inter) {
                System.err.println("Unexpected thread interrupt");
                System.exit(1);
            } catch (ExecutionException exec) {
                System.err.println("Execution error: " + exec.getMessage());
                System.exit(1);
            }
        }
        manager.shutdown();

        if (mergeQueue.isEmpty()) {
            System.err.println("Internal error: empty queue");
            System.exit(1);
        } else {
            List<String> result = mergeQueue.poll();
            if (settings.uniqueOnly) {
                Set<String> unique = new TreeSet<>(comp);
                unique.addAll(result);
                result.clear();
                result.addAll(unique);
            }
            BufferedWriter outp = null;
            try {
                if (settings.output == null) {
                    outp = new BufferedWriter(new java.io.OutputStreamWriter(System.out));
                } else {
                    outp = new BufferedWriter(new FileWriter(settings.output));
                }
                for (String line : result) {
                    outp.write(line);
                    outp.write("\n");
                }
            } catch (IOException ioEx) {
                System.err.println("I/O exception while writing output: "
                    + ioEx.getMessage());
            } finally {
                try {
                    if (outp != null) {
                        outp.close();
                    }
                } catch (IOException ex) {}
            }
        }
    }
}