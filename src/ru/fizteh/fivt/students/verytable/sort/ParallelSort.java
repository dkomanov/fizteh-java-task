package ru.fizteh.fivt.students.verytable.sort;

import ru.fizteh.fivt.students.verytable.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelSort {

    static int addStrings(ArrayList<String> fileNames,
                          ArrayList<Pair> stringsToSort) {
        int stringsNumber = 0;
        if (fileNames.isEmpty()) {
            try {
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(isr);
                String curString;
                while ((curString = br.readLine()) != null) {
                    ++stringsNumber;
                    stringsToSort.add(new Pair(curString, stringsNumber));
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        } else {
            FileInputStream curFileStream;
            InputStreamReader isr;
            BufferedReader br = null;
            String curString;
            for (int i = 0; i < fileNames.size(); ++i) {
                try {
                    curFileStream = new FileInputStream(fileNames.get(i));
                    isr = new InputStreamReader(curFileStream);
                    br = new BufferedReader(isr);
                    while ((curString = br.readLine()) != null) {
                        ++stringsNumber;
                        stringsToSort.add(new Pair(curString, stringsNumber));
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    System.exit(1);
                } finally {
                    IOUtils.closeFile(fileNames.get(i), br);
                }
            }
        }
        return stringsNumber;
    }

    static class SensitiveComparator implements Comparator<Pair> {
        public int compare(Pair pair1, Pair pair2) {
            return pair1.getValue().compareTo(pair2.getValue());
        }
    }

    static class InsensitiveComparator implements Comparator<Pair> {
        public int compare(Pair pair1, Pair pair2) {
            return pair1.getValue().compareToIgnoreCase(pair2.getValue());
        }
    }

    static void writeStrings(ArrayList<Pair> stringsToSort, String output,
                             int stringsNumber, boolean isUniqueKey,
                             Comparator comparator) {
        Writer writer = null;
        FileOutputStream fos;

        try {
            if (output.isEmpty()) {
                writer = new OutputStreamWriter(System.out);
            } else {
                fos = new FileOutputStream(output);
                writer = new OutputStreamWriter(fos);
            }

            String lineSeparator = System.lineSeparator();
            if (stringsNumber == 0) {
                writer.write("");
            } else {
                Pair last = stringsToSort.get(0);
                writer.write(last.getValue() + lineSeparator);
                for (int i = 1; i < stringsNumber; ++i) {
                    if (isUniqueKey) {
                        if (comparator.compare(stringsToSort.get(i), last) != 0) {
                            last = stringsToSort.get(i);
                            writer.write(last.getValue() + lineSeparator);
                        }
                    } else {
                        writer.write(stringsToSort.get(i).getValue() + lineSeparator);
                    }
                }
            }
            writer.flush();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } finally {
            if (!output.isEmpty()) {
                IOUtils.closeFile(output, writer);
            }
        }
    }

    public static void main(String[] args) {

        String output = "";
        boolean isUniqueKey = false;
        boolean isSensitiveKey = true;
        int threadsNumber = Runtime.getRuntime().availableProcessors();
        ArrayList<String> files = new ArrayList<>();

        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-i":
                    isSensitiveKey = false;
                    break;
                case "-u":
                    isUniqueKey = true;
                    break;
                case "-iu":
                    isSensitiveKey = false;
                    isUniqueKey = true;
                    break;
                case "-ui":
                    isSensitiveKey = false;
                    isUniqueKey = true;
                    break;
                case "-o":
                    if (i + 1 < args.length) {
                        output = args[i + 1];
                        ++i;
                    } else {
                        System.err.println("No output file declared after -o.");
                        System.exit(1);
                    }
                    break;
                case "-t":
                    if (i + 1 < args.length) {
                        threadsNumber = Integer.parseInt(args[i + 1]);
                        ++i;
                        if (threadsNumber > 10 || threadsNumber < 1) {
                            System.err.println("Number of execution threads"
                                               + "must not be in [1, 10].");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("No number of execution threads"
                                           + "declared after -t. ");
                        System.exit(1);
                    }
                    break;
                default:
                    files.add(args[i]);
                    break;
            }
        }

        ArrayList<Pair> stringsToSort = new ArrayList<>();

        int stringsNumber = addStrings(files, stringsToSort);
        if (stringsNumber > 0) {
            final ExecutorService executor = Executors.newFixedThreadPool(threadsNumber);
            List<Future> futures = new Vector<>();
            SortTask rootTask = new SortTask(executor, futures, stringsToSort, 0,
                                             stringsNumber - 1, isSensitiveKey);
            futures.add(executor.submit(rootTask));
            while (!futures.isEmpty()) {
                Future topFeature = futures.remove(0);
                try {
                    if (topFeature != null) {
                        topFeature.get();
                    }
                } catch (InterruptedException ie) {
                    System.err.println(ie.getMessage());
                    System.exit(1);
                } catch (ExecutionException ee) {
                    System.err.println(ee.getMessage());
                    System.exit(1);
                }
            }

            executor.shutdown();
        }

        Comparator<Pair> comparator = new SensitiveComparator();
        if (!isSensitiveKey) {
            comparator = new InsensitiveComparator();
        }

        writeStrings(stringsToSort, output, stringsNumber, isUniqueKey, comparator);
    }

}
