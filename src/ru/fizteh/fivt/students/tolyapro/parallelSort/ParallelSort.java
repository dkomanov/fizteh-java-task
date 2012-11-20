package ru.fizteh.fivt.students.tolyapro.ParallelSort;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ru.fizteh.fivt.students.tolyapro.wordCounter.BufferCloser;

public class ParallelSort {

    public static void printResult(ArrayList<String> result,
            PrintStream output, boolean onlyUnique) throws Exception {

        if (!onlyUnique) {
            for (int i = 0; i < result.size(); ++i) {
                output.println(result.get(i));
            }
        } else {
            String prevString = "";
            for (int i = 0; i < result.size(); ++i) {
                String tmp = result.get(i);
                if (!prevString.equals(tmp)) {
                    output.println(result.get(i));
                    prevString = tmp;
                }

            }
        }
        BufferCloser.close(output);
    }

    public static void printFromDiffSources(String output,
            ArrayList<String> result, boolean onlyUnique) {
        try {
            if (output.equals("")) {
                printResult(result, System.out, onlyUnique);
            } else {
                File file = new File(output);
                PrintStream stream = new PrintStream(file);
                printResult(result, stream, onlyUnique);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        boolean caseSensitive = true;
        boolean onlyUnique = false;
        int numTreads = Runtime.getRuntime().availableProcessors();
        String output = "";
        ArrayList<String> files = new ArrayList<String>();
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            boolean endOfArgs = files.size() == 0 ? false : true;
            if (!endOfArgs) {
                if (arg.equals("-i")) {
                    caseSensitive = false;
                } else if (arg.equals("-u")) {
                    onlyUnique = true;
                } else if (arg.equals("-iu") || arg.equals("-ui")) {
                    caseSensitive = false;
                    onlyUnique = true;
                } else if (arg.equals("-o")) {
                    output = args[++i];
                } else if (arg.equals("-t")) {
                    try {
                        numTreads = Integer.parseInt(args[++i]);
                    } catch (Exception e) {
                        System.err.println("Incorrect number of threads");
                        System.exit(1);
                    }
                } else if (arg.charAt(0) == '-') {
                    System.err.println("Incorrect flag");
                    System.exit(1);
                } else {
                    try {
                        File tmp = new File(arg);
                        if (!tmp.exists()) {
                            System.err.println("File doesn't exist");
                            System.exit(1);
                        }
                        files.add(arg);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                }
            } else {
                try {
                    File tmp = new File(arg);
                    if (!tmp.exists()) {
                        System.err.println("File doesn't exist");
                        System.exit(1);
                    }
                    files.add(arg);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
        if (numTreads < 1) {
            System.err.println("Bad threads number");
            System.exit(1);
        }
        long startTime = System.currentTimeMillis();
        Reader reader = new Reader(files);
        ArrayList<String> allStrings = null;
        try {
            allStrings = reader.getStrings();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (allStrings.size() <= numTreads) {
            if (caseSensitive) {
                Collections.sort(allStrings);
                printFromDiffSources(output, allStrings, onlyUnique);
            } else {
                Collections.sort(allStrings, String.CASE_INSENSITIVE_ORDER);
                printFromDiffSources(output, allStrings, onlyUnique);
            }
            System.exit(0);
        }
        LinkedBlockingQueue<ArrayList<String>> result = new LinkedBlockingQueue<ArrayList<String>>();
        ExecutorService sorters = Executors.newFixedThreadPool(numTreads);
        int blockSize = allStrings.size() / numTreads;
        System.out.println(blockSize);
        for (int i = 0; i < numTreads; ++i) {
            if (i != numTreads - 1) {
                List<String> tmp = allStrings.subList(i * blockSize, (i + 1)
                        * blockSize);
                ArrayList<String> someStrings = new ArrayList<String>(tmp);
                Sorter sorter = new Sorter(someStrings, caseSensitive, result);
                sorters.execute(sorter);
            } else {
                ArrayList<String> someStrings = new ArrayList<String>(
                        allStrings.subList(i * blockSize, allStrings.size()));
                Sorter sorter = new Sorter(someStrings, caseSensitive, result);
                sorters.execute(sorter);
            }
        }
        if (numTreads > 1) {
            ExecutorService mergers = Executors
                    .newFixedThreadPool(numTreads - 1);
            for (int i = 0; i < numTreads - 1; ++i) {
                Merger merger = new Merger(caseSensitive, result);
                mergers.execute(merger);
            }
            mergers.shutdown();
            mergers.awaitTermination(100500, TimeUnit.MINUTES);
        }
        printFromDiffSources(output, result.take(), onlyUnique);
        sorters.shutdownNow();
        long time = System.currentTimeMillis();
        System.out.println(time - startTime);
    }
}
