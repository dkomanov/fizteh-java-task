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
                          String[] stringsToSort) {
        int stringsNumber = 0;
        if (fileNames.isEmpty()) {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String curString;
            try {
                while ((curString = br.readLine()) != null) {
                    stringsToSort[stringsNumber] = curString;
                    ++stringsNumber;
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            } finally {
                IOUtils.closeFile(isr.toString(), br);
            }
        } else {
            File curFile;
            FileReader fr;
            BufferedReader br = null;
            String curString;
            for (int i = 0; i < fileNames.size(); ++i) {
                try {
                    curFile = new File(fileNames.get(i));
                    fr = new FileReader(curFile);
                    br = new BufferedReader(fr);
                    while ((curString = br.readLine()) != null) {
                        stringsToSort[stringsNumber] = curString;
                        ++stringsNumber;
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


    public static void main(String [] args){

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

        String[] stringsToSort = new String[100000000];
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

        FileOutputStream fos;
        OutputStreamWriter osw;
        BufferedWriter bw = null;
        if (output.isEmpty()) {
            bw = new BufferedWriter(new OutputStreamWriter(System.out));
        } else {
            try {
                fos = new FileOutputStream(output);
                osw = new OutputStreamWriter(fos);
                bw = new BufferedWriter(osw);
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                IOUtils.closeFile(output, bw);
                System.exit(1);
            }
        }

        String lineSeparator = System.lineSeparator();
        try {
            bw.write(stringsToSort[0] + lineSeparator);
            for (int i = 1; i < stringsNumber; ++i) {
                if (isUniqueKey) {
                    if (!isSensitiveKey) {
                        if (stringsToSort[i].compareToIgnoreCase(stringsToSort[i - 1]) != 0) {
                            bw.write(stringsToSort[i] + lineSeparator);
                        }
                    } else {
                        if (stringsToSort[i].compareTo(stringsToSort[i - 1]) != 0) {
                            bw.write(stringsToSort[i] + lineSeparator);
                        }
                    }
                } else {
                    bw.write(stringsToSort[i] + lineSeparator);
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } finally {
            IOUtils.closeFile(output, bw);
        }
        executor.shutdown();
    }

}