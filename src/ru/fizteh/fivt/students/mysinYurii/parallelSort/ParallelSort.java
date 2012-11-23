package ru.fizteh.fivt.students.mysinYurii.parallelSort;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ParallelSort {
    static int threadNum;
    
    static boolean writeInFile;
    
    static String fileName;
        
    static boolean onlyUnique;
    
    static boolean caseSense;
        
    static ExecutorService threads;
    
    static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }
    
    static void readFrom(BufferedReader inputStream, LinkedBlockingQueue<String> inputStrings) {
        try {
            String newString = inputStream.readLine();
            while (newString != null) {
                inputStrings.add(newString);
                newString = inputStream.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeStream(inputStream);
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        int i = 0;
        threadNum = 2;
        writeInFile = false;
        fileName = null;
        onlyUnique = false;
        caseSense = true;
        LinkedBlockingQueue<String> inputStrings = new LinkedBlockingQueue<String>();
        while (i < args.length) {
            if (args[i].equals("-o")) {
                ++i;
                if (i < args.length) {
                    writeInFile = true;
                    fileName = args[i];
                } else {
                    System.out.println("-o: Too few arguments");
                    System.exit(1);
                }
            } else if (args[i].equals("-t")) {
                ++i;
                if (i < args.length) {
                    int number = 0;
                    try {
                        number = Integer.parseInt(args[i]);
                    } catch (NumberFormatException e) {
                        System.out.println("-t: Not a number: " + args[i]);
                        System.exit(1);
                    }
                    threadNum = number;
                } else {
                    System.out.println("-t: Too few arguments");
                    System.exit(1);
                }
            } else if (args[i].charAt(0) == '-') {
                if (args[i].length() == 0) {
                    System.out.println("No any comand with -");
                    System.exit(1);
                }
                for (int j = 1; j < args[i].length(); ++j) {
                    if (args[i].charAt(j) == 'i') {
                        caseSense = false;
                    } else if (args[i].charAt(j) == 'u') {
                        onlyUnique = true;
                    } else {
                        System.out.println("Uknown flag: " + args[i].charAt(j));
                        System.exit(1);
                    }
                }
            } else break;
            ++i;
        }
        ArrayList<String> inputFiles = new ArrayList<String>();
        if (i < args.length) {
            for (int j = i; j < args.length; ++j) {
                inputFiles.add(args[j]);
            }
        }
        ArrayList< List<String> > toMerge = new ArrayList< List<String> >();
        for (int i1 = 0; i1 < threadNum; ++i1) {
            toMerge.add(Collections.synchronizedList(new ArrayList<String>()));
        }
        threads = Executors.newFixedThreadPool(threadNum);
        for (int i1 = 0; i1 < threadNum; ++i1) {
            threads.execute(new MyThread(inputStrings, toMerge.get(i1), caseSense));
        }

        if (inputFiles.size() == 0) {
           BufferedReader systemIn = new BufferedReader(
                   new InputStreamReader(System.in));
           readFrom(systemIn, inputStrings);
        } else {
            for (String s : inputFiles) {
                BufferedReader inputStream = null;
                FileReader tempReader = null;
                try {    
                    tempReader = new FileReader(s);
                    inputStream = new BufferedReader(tempReader);
                } catch (FileNotFoundException e1) {
                    System.out.println(e1.getMessage());
                    threads.shutdownNow();
                    closeStream(tempReader);
                    closeStream(inputStream);
                    System.exit(1);
                }
                try {
                    readFrom(inputStream, inputStrings);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    threads.shutdown();
                    closeStream(inputStream);
                    System.exit(1);
                }
                closeStream(inputStream);
            }
        }
        
        try {
            for (int i1 = 0; i1 < threadNum; ++i1) {
                inputStrings.add("\n");
            }
            threads.shutdown();
            threads.awaitTermination(10, TimeUnit.MINUTES);
        } catch(InterruptedException e) {
            threads.shutdownNow();
            System.out.println(e.getMessage());
            System.exit(1);
        }
        PrintWriter outputStream = null;
        if (writeInFile) {
            File outputFile = new File(fileName);
            if (outputFile.exists()) {
                outputFile.delete();
            }
            try {
                outputStream = new PrintWriter(fileName);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        } else {
            outputStream = new PrintWriter(System.out);
        }
        String prevMin = "\n";
        while (true) {
            String minString = null;
            int minPosition = -1;
            for (int i1 = 0; i1 < toMerge.size(); ++i1) {
                if (toMerge.get(i1).isEmpty()) {
                    continue;
                }
                if (minPosition == -1) {
                    minString = toMerge.get(i1).get(0);
                    minPosition = i1;
                } else {
                    if (caseSense) {
                        if (minString.compareTo(toMerge.get(i1).get(0)) > 0) {
                            minString = toMerge.get(i1).get(0);
                            minPosition = i1;
                        }
                    } else {
                        if (minString.compareToIgnoreCase(toMerge.get(i1).get(0)) > 0) {
                            minString = toMerge.get(i1).get(0);
                            minPosition = i1;
                        }
                    }
                }
            }
            if (minPosition == -1) {
                break;
            } else {
                toMerge.get(minPosition).remove(0);
                if (onlyUnique) {
                    if (caseSense) {
                        if (!minString.equals(prevMin)) {
                            write(outputStream, minString);
                            prevMin = minString;
                        }
                    } else {
                        if (minString.compareToIgnoreCase(minString) != 0) {
                            write(outputStream, minString);
                            prevMin = minString;
                        }
                    }
                } else {
                    write(outputStream, minString);
                }
            }
        }
        if (writeInFile) {
            closeStream(outputStream);
        }
    }

    private static void write(PrintWriter outputStream, String minString) {
        outputStream.println(minString);
        outputStream.flush();
    }
}
