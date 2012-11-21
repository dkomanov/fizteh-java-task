package ru.fizteh.fivt.students.verytable.sort;

import ru.fizteh.fivt.students.verytable.IOUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.List;
import java.util.Vector;

public class ParallelSort {

    static int addStrings(ArrayList<String> fileNames,
                          ArrayList<Pair> stringsToSort) {
        int stringsNumber = 0;
        if (fileNames.isEmpty()) {
            try {
                InputStreamReader isr = new InputStreamReader(System.in, "Unicode");
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
                    isr = new InputStreamReader(curFileStream, "Unicode");
                    br = new BufferedReader(isr);
                    while ((curString = br.readLine()) != null) {
                        if (stringsNumber == 0) {
                        }
                        ++stringsNumber;
                        stringsToSort.add(new Pair(curString, stringsNumber));
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    System.exit(1);
                } finally {
                    IOUtils.closeFile("System.in", br);
                }
            }
        }
        return stringsNumber;
    }

    static void writeStrings(ArrayList<Pair> stringsToSort, String output,
                             int stringsNumber, boolean isUniqueKey,
                             boolean isSensitiveKey) {
        Writer writer = null;
        FileOutputStream fos;

        try {
            if (output.isEmpty()) {
                writer = new OutputStreamWriter(System.out);
            } else {
                fos = new FileOutputStream(output);
                writer = new OutputStreamWriter(fos, "Unicode");
            }

            String lineSeparator = System.lineSeparator();
            writer.write(stringsToSort.get(0).getValue() + lineSeparator);
            for (int i = 1; i < stringsNumber - 1; ++i) {
                if (isUniqueKey) {
                    if (!isSensitiveKey) {
                        if (stringsToSort.get(i).getValue()
                            .compareToIgnoreCase(stringsToSort.get(i - 1).getValue()) != 0) {
                            writer.write(stringsToSort.get(i).getValue() + lineSeparator);
                        }
                    } else {
                        if (stringsToSort.get(i).getValue()
                            .compareTo(stringsToSort.get(i - 1).getValue()) != 0) {
                            writer.write(stringsToSort.get(i).getValue() + lineSeparator);
                        }
                    }
                } else {
                    writer.write(stringsToSort.get(i).getValue() + lineSeparator);
                }
            }
            if (isUniqueKey) {
                if (!isSensitiveKey) {
                    if (stringsToSort.get(stringsNumber - 1).getValue()
                        .compareToIgnoreCase(stringsToSort.get(stringsNumber - 2).getValue()) != 0) {
                        writer.write(stringsToSort.get(stringsNumber - 1).getValue());
                    }
                } else {
                    if (stringsToSort.get(stringsNumber - 1).getValue()
                        .compareTo(stringsToSort.get(stringsNumber - 2).getValue()) != 0) {
                        writer.write(stringsToSort.get(stringsNumber - 1).getValue());
                    }
                }
            } else {
                writer.write(stringsToSort.get(stringsNumber - 1).getValue());
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } finally {
            IOUtils.closeFile(output, writer);
        }
    }

    public static void main(String[] args){

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

        final ExecutorService executor = Executors.newFixedThreadPool(threadsNumber);
        List<Future> futures = new Vector<>();
        SortTask rootTask = new SortTask (executor, futures, stringsToSort,
                                          0, stringsNumber - 1,
                                          isSensitiveKey);
        futures.add(executor.submit(rootTask));
        while(!futures.isEmpty()){
            Future topFeature = futures.remove(0);
            try{
                if(topFeature != null) {
                    topFeature.get();
                }
            }catch(InterruptedException ie){
                System.err.println(ie.getMessage());
                ie.printStackTrace();
                System.exit(1);
            }catch(ExecutionException ee){
                System.err.println(ee.getMessage());
                ee.printStackTrace();
                System.exit(1);
            }
        }

        executor.shutdown();

        writeStrings(stringsToSort, output, stringsNumber, isUniqueKey, isSensitiveKey);
    }

}
