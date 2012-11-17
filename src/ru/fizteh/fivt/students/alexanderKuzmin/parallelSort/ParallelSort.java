package ru.fizteh.fivt.students.alexanderKuzmin.parallelSort;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;
import ru.fizteh.fivt.students.alexanderKuzmin.parallelSort.ParallelMergeSort;
import ru.fizteh.fivt.students.alexanderKuzmin.parallelSort.WorkWithStream;

/**
 * @author Alexander Kuzmin group 196 Class ParallelSort
 * 
 */

public class ParallelSort {

    public static class MyComparator implements Comparator<String> {
        @Override
        public int compare(String arg0, String arg1) {
            return arg0.compareTo(arg1);
        }
    }

    private static void readAndSort(ArrayList<String> inputFiles,
            boolean insensitive, boolean unique, int threadCount,
            boolean outputToFile, String output) {
        InputStream stream = null;
        ArrayList<String> inputLines = new ArrayList<String>();
        if (!inputFiles.isEmpty()) {
            for (int i = 0; i < inputFiles.size(); ++i) {
                try {
                    stream = new FileInputStream(inputFiles.get(i));
                    WorkWithStream.readerFromStream(inputLines, stream);
                } catch (Exception e) {
                    Closers.printErrAndExit(e.getMessage());
                } finally {
                    Closers.closeStream(stream);
                }
            }
        } else {
            try {
                WorkWithStream.readerFromStream(inputLines, System.in);
            } catch (Exception e) {
                Closers.printErrAndExit(e.getMessage());
            }
        }
        ParallelMergeSort mySort = new ParallelMergeSort(inputLines, 0,
                inputLines.size(), threadCount,
                inputLines.size() / threadCount, insensitive);
        mySort.run();
        printOutput(mySort.result, outputToFile, output, unique, insensitive);
    }

    static void printOutput(ArrayList<String> result, boolean outputToFile,
            String output, boolean unique, boolean insensitive) {
        PrintStream pStream = null;
        try {
            pStream = outputToFile ? new PrintStream(output) : System.out;
            WorkWithStream.printToStream(result.get(0), pStream);
            Comparator<String> cmp = insensitive ? String.CASE_INSENSITIVE_ORDER
                    : new MyComparator();
            for (int i = 1; i < result.size(); ++i) {
                if (unique) {
                    compareAndPrintToStream(result.get(i), result.get(i - 1),
                            cmp, pStream);
                } else {
                    WorkWithStream.printToStream(result.get(i), pStream);
                }
            }
        } catch (Throwable e) {
            Closers.printErrAndExit(e.getMessage());
        } finally {
            if (outputToFile) {
                WorkWithStream.closeStream(pStream);
            }
        }
    }

    private static void compareAndPrintToStream(String string, String string2,
            Comparator<String> cmp, PrintStream pStream) {
        if (cmp.compare(string, string2) != 0) {
            WorkWithStream.printToStream(string, pStream);
        }
    }

    /**
     * @param args
     * [-iu] - i = insensitive comparison, u = print only unique
     * lines;
     * [-t THREAD_COUNT] - the number of threads to perform
     * the sort (default: the max number of threads); 
     * [-o OUTPUT] the name of file in which to record the result 
     * (default: printing to stdout);
     * [FILES...] the names of files which the program
     *  have to sort (default: reading from stdin);
     */

    public static void main(String[] args) {
        boolean insensitive = false; // key -i
        boolean unique = false; // key -u
        int threadCount = Runtime.getRuntime().availableProcessors();
        boolean outputToFile = false; // key -o
        String output = null;
        ArrayList<String> inputFiles = new ArrayList<String>();
        int i = 0;
        for (; i < args.length; ++i) {
            if (args[i].equals("-o") && args.length > i + 1) {
                output = args[++i];
                outputToFile = true;
            } else if (args[i].charAt(0) == '-'
                    && (args[i].charAt(1) == 'i' || args[i].charAt(1) == 'u')
                    && args[i].length() <= 3) {
                for (int j = 1; j < args[i].length(); ++j) {
                    if (args[i].charAt(j) == 'i') {
                        insensitive = true;
                    } else if (args[i].charAt(j) == 'u') {
                        unique = true;
                    } else {
                        Closers.printErrAndExit("Invalid argument. Use: java ParallelSort [-iu] [-t THREAD_COUNT] [-o OUTPUT] [FILES...]");
                    }
                }
            } else if (args[i].equals("-t") && args.length > i + 1) {
                try {
                    int tmp = Integer.parseInt(args[++i]);
                    if (tmp < threadCount && tmp > 0) {
                        threadCount = tmp;
                    }
                } catch (Exception e) {
                    Closers.printErrAndExit("!Invalid argument. Use: java ParallelSort [-iu] [-t THREAD_COUNT] [-o OUTPUT] [FILES...]");
                }
            } else {
                inputFiles.add(args[i]);
            }
        }
        readAndSort(inputFiles, insensitive, unique, threadCount, outputToFile,
                output);
    }
}