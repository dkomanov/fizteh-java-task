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

    public static void main(String[] args) {
        Argparser parser = new Argparser(args);
        SortSettings settings = new SortSettings();
        try {
            parser.load(settings);
        } catch (ArgparseException parseEx) {
            System.err.println("Argument parsing exception: " + parseEx.getMessage());
            System.exit(1);
        }

        ExecutorService manager = null;
        try {
            manager = Executors.newFixedThreadPool(settings.threads);
        } catch (IllegalArgumentException bad) {
            System.err.println("Invalid thread count");
            System.exit(1);
        }
        CompletionService<List<String>> monitor =
            new ExecutorCompletionService<>(manager);

        int trace = 0;

        List<List<String>> input =
            new ArrayList<>();

        if (settings.files.isEmpty()) {
            input.add(null);
            monitor.submit(LinesInputFactory.create(input, trace), null);
            ++trace;
        } else {
            for (String file : settings.files) {
                try {
                    input.add(null);
                    monitor.submit(LinesInputFactory.create(file, input, trace), null);
                    ++trace;
                } catch (IOException ioEx) {
                    System.err.println("Expected filename, got " + file);
                    System.exit(1);
                }
            }
        }

        while (trace > 0) {
            try {
                monitor.take().get();
                --trace;
            } catch (InterruptedException | ExecutionException ex) {
                System.err.println("Error while input: " + ex.getMessage());
                System.exit(1);
            }
        }

        int size = 0;
        for (List<String> list : input) {
            size += list.size();
        }

        int maxChunk = size / settings.threads;
        maxChunk = Math.max(maxChunk, 300000);

        Comparator<String> comp = settings.caseInsensitive ? 
            String.CASE_INSENSITIVE_ORDER : new SensitiveComp();


        List<List<String>> sorted = new ArrayList<>();
        for (List<String> data : input) {
            int chunkBegin = 0;
            while (chunkBegin < data.size()) {
                int chunkEnd = Math.min(
                    chunkBegin + maxChunk, data.size());

                List<String> part = data.subList(chunkBegin, chunkEnd);
                sorted.add(part);
                monitor.submit(new LinesSort(part, comp), null);
                ++trace;
                chunkBegin = chunkEnd;
            }
        }

        while (trace > 0) {
            try {
                monitor.take().get();
                --trace;
            } catch (InterruptedException | ExecutionException ex) {
                System.err.println("Error while sorting: " + ex.getMessage());
                System.exit(1);
            }
        }

        while (sorted.size() > 1) {
            List<List<String>> level = new ArrayList<>();
            int i;
            for (i = 0; i < sorted.size(); i += 2) {
                level.add(null);
                List<String> second = (i + 1) == sorted.size() ?
                    null : sorted.get(i + 1);

                monitor.submit(new LinesMerge(
                    sorted.get(i), second, level, trace, comp), null);

                ++trace;
            }

            while (trace > 0) {
                try {
                    monitor.take().get();
                    --trace;
                } catch (InterruptedException | ExecutionException ex) {
                    System.err.println("Error while merging: " + ex.getMessage());
                    System.exit(1);
                }
            }

            sorted = level;
        }

        manager.shutdown();

        if (sorted.isEmpty()) {
            System.err.println("Internal error: empty queue");
            System.exit(1);
        } else {
            List<String> result = sorted.get(0);
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
                outp.flush();
            } catch (IOException ioEx) {
                System.err.println("I/O exception while writing output: "
                    + ioEx.getMessage());
            } finally {
                try {
                    if (outp != null && settings.output != null) {
                        outp.close();
                    }
                } catch (IOException ex) {}
            }
        }
    }
}