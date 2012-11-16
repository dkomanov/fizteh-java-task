package ru.fizteh.fivt.students.alexanderKuzmin.parallelSort;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;
import ru.fizteh.fivt.students.alexanderKuzmin.parallelSort.ParallelMergeSort;
import ru.fizteh.fivt.students.alexanderKuzmin.parallelSort.WorkWithStream;

/**
 * @author Alexander Kuzmin group 196 Class ParallelSort
 * 
 */

public class ParallelSort {

    private static void readAndSort(String[] args, int n, boolean insensitive,
            boolean unique, int threadCount, boolean outputToFile,
            String output, boolean inputFromFile) {
        InputStream stream = null;
        ArrayList<String> input = new ArrayList<String>();
        if (n < args.length) {
            for (int i = n; i < args.length; ++i) {
                try {
                    stream = new FileInputStream(args[i]);
                    readerFromStream(input, stream);
                } catch (Exception e) {
                    Closers.printErrAndExit(e.getMessage());
                } finally {
                    Closers.closeStream(stream);
                }
            }
        } else {
            try {
                readerFromStream(input, System.in);
            } catch (Exception e) {
                Closers.printErrAndExit(e.getMessage());
            }
        }
        ParallelMergeSort mySort = new ParallelMergeSort(input, 0,
                input.size(), threadCount, input.size() / threadCount,
                insensitive);
        mySort.run();
        printOutput(mySort.result, outputToFile, output, unique, insensitive);
    }

    static void printOutput(ArrayList<String> result, boolean outputToFile,
            String output, boolean unique, boolean insensitive) {
        PrintStream pStream = null;
        if (outputToFile) {
            try {
                pStream = new PrintStream(output);
            } catch (Exception e) {
                WorkWithStream.closeStream(pStream);
                Closers.printErrAndExit(e.getMessage());
            }
        } else {
            pStream = System.out;
        }
        WorkWithStream.printToStream(result.get(0), pStream);
        for (int i = 1; i < result.size(); ++i) {
            if (unique) {
                if (insensitive) {
                    if (result.get(i).compareToIgnoreCase(result.get(i - 1)) != 0) {
                        WorkWithStream.printToStream(result.get(i), pStream);
                    }
                } else {
                    if (result.get(i).compareTo(result.get(i - 1)) != 0) {
                        WorkWithStream.printToStream(result.get(i), pStream);
                    }
                }
            } else {
                WorkWithStream.printToStream(result.get(i), pStream);
            }
        }
        WorkWithStream.closeStream(pStream);
    }

    private static void readerFromStream(ArrayList<String> answer,
            InputStream stream) throws IOException {
        BufferedReader bufReader = null;
        InputStreamReader inputReader = null;
        try {
            inputReader = new InputStreamReader(stream);
            bufReader = new BufferedReader(inputReader);
            String str;
            while ((str = bufReader.readLine()) != null) {
                answer.add(str);
            }
        } catch (Exception e) {
            Closers.printErrAndExit(e.getMessage());
        } finally {
            Closers.closeStream(bufReader);
            Closers.closeStream(inputReader);
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
        // long start = System.currentTimeMillis();// TODO delete
        boolean insensitive = false; // key -i
        boolean unique = false; // key -u
        int threadCount = Runtime.getRuntime().availableProcessors();
        boolean outputToFile = false; // key -o
        boolean inputFromFile = false;
        String output = null;
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
                    if (tmp < threadCount) {
                        threadCount = tmp;
                    }
                } catch (Exception e) {
                    Closers.printErrAndExit("!Invalid argument. Use: java ParallelSort [-iu] [-t THREAD_COUNT] [-o OUTPUT] [FILES...]");
                }
            } else {
                break;
            }
        }
        readAndSort(args, i, insensitive, unique, threadCount, outputToFile,
                output, inputFromFile);
        // System.out.println(System.currentTimeMillis() - start); TODO delete
    }
}