package ru.fizteh.fivt.students.tolyapro.parallelSort;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ru.fizteh.fivt.students.tolyapro.wordCounter.BufferCloser;

public class ParallelSort {

    static class NormalComparator implements Comparator<String> {
        @Override
        public int compare(String string1, String string2) {
            return string1.compareTo(string2);
        }
    }

    public static void printResult(ArrayList<String> result,
            PrintStream output, boolean onlyUnique,
            Comparator<String> comparator) throws Exception {
        if (!onlyUnique) {
            for (int i = 0; i < result.size(); ++i) {
                output.println(result.get(i));
            }
        } else {
            String prevString = "";
            for (int i = 0; i < result.size(); ++i) {
                String tmp = result.get(i);
                if (comparator.compare(tmp, prevString) != 0) {
                    output.println(result.get(i));
                    prevString = tmp;
                }

            }
        }
    }

    public static void printFromDiffSources(String output,
            ArrayList<String> result, boolean onlyUnique,
            Comparator<String> comparator) {
        PrintStream stream = null;
        boolean needToClose = true;
        try {
            if (output.equals("")) {
                printResult(result, System.out, onlyUnique, comparator);
                needToClose = false;
            } else {
                File file = new File(output);
                stream = new PrintStream(file);
                printResult(result, stream, onlyUnique, comparator);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            if (needToClose) {
                BufferCloser.close(stream);
            }
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
        Reader reader = new Reader(files);
        ArrayList<String> allStrings = null;
        try {
            allStrings = reader.getStrings();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        Comparator<String> comparator;
        if (!caseSensitive) {
            comparator = String.CASE_INSENSITIVE_ORDER;
        } else {
            comparator = new NormalComparator();
        }

        if (allStrings.size() <= numTreads) {
            Collections.sort(allStrings, comparator);
            printFromDiffSources(output, allStrings, onlyUnique, comparator);
            System.exit(0);
        }
        LinkedBlockingQueue<ArrayList<String>> result = new LinkedBlockingQueue<ArrayList<String>>();
        ExecutorService sorters = Executors.newFixedThreadPool(numTreads);
        int blockSize = allStrings.size() / numTreads;
        for (int i = 0; i < numTreads; ++i) {
            if (i != numTreads - 1) {
                List<String> tmp = allStrings.subList(i * blockSize, (i + 1)
                        * blockSize);
                ArrayList<String> someStrings = new ArrayList<String>(tmp);
                Sorter sorter = new Sorter(someStrings, comparator, result);
                sorters.execute(sorter);
            } else {
                ArrayList<String> someStrings = new ArrayList<String>(
                        allStrings.subList(i * blockSize, allStrings.size()));
                Sorter sorter = new Sorter(someStrings, comparator, result);
                sorters.execute(sorter);
            }
        }
        if (numTreads > 1) {
            ExecutorService mergers = Executors
                    .newFixedThreadPool(numTreads - 1);
            for (int i = 0; i < numTreads - 1; ++i) {
                Merger merger = new Merger(comparator, result);
                mergers.execute(merger);
            }
            mergers.shutdown();
            mergers.awaitTermination(100500, TimeUnit.MINUTES);
        }
        printFromDiffSources(output, result.take(), onlyUnique, comparator);
        sorters.shutdownNow();
    }
}
