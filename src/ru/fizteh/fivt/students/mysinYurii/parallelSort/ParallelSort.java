package ru.fizteh.fivt.students.mysinYurii.parallelSort;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    
    static void readFrom(BufferedReader inputStream, ArrayList<String> inputStrings) {
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
        ArrayList<String> inputStrings = new ArrayList<String>();
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
        ArrayList< ArrayList<String> > toMerge = new ArrayList< ArrayList<String> >();
        for (int i1 = 0; i1 < threadNum; ++i1) {
            toMerge.add(new ArrayList<String>());
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
                     closeStream(tempReader);
                     closeStream(inputStream);
                     System.exit(1);
                 }
                 try {
                     readFrom(inputStream, inputStrings);
                 } catch (Exception e) {
                     System.out.println(e.getMessage());
                     closeStream(inputStream);
                     System.exit(1);
                 }
                 closeStream(inputStream);
             }
        }
        threads = Executors.newFixedThreadPool(threadNum);
        int forThreadSize = inputStrings.size() / threadNum;
        int currPos = 0;
        for (int i1 = 0; i1 < threadNum; ++i1) {
            if (i1 < inputStrings.size() % threadNum) {
                threads.execute(new MyThread(inputStrings, toMerge.get(i1), caseSense, currPos, forThreadSize + 1));
                currPos += forThreadSize + 1;
            } else {
                threads.execute(new MyThread(inputStrings, toMerge.get(i1), caseSense, currPos, forThreadSize));
                currPos += forThreadSize;
            }
        }
        try {
            threads.shutdown();
            threads.awaitTermination(10, TimeUnit.MINUTES);
        } catch(InterruptedException e) {
            threads.shutdownNow();
            System.out.println(e.getMessage());
            System.exit(1);
        }
        PrintStream outputStream = null;
        if (writeInFile) {
            File outputFile = new File(fileName);
            if (outputFile.exists()) {
                outputFile.delete();
            }
            try {
                outputStream = new PrintStream(fileName);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        } else {
            outputStream = new PrintStream(System.out);
        }
        String prevMin = "\n";
        int[] currArrPos = new int[threadNum];
        for (int i1 = 0; i1 < threadNum; ++i1) {
            currArrPos[i1] = 0;
        }
        while (true) {
            String minString = null;
            int minPosition = -1;
            for (int i1 = 0; i1 < toMerge.size(); ++i1) {
                if (currArrPos[i1] == toMerge.get(i1).size()) {
                    continue;
                }
                if (minPosition == -1) {
                    minString = toMerge.get(i1).get(currArrPos[i1]);
                    minPosition = i1;
                } else {
                    if (caseSense) {
                        if (minString.compareTo(toMerge.get(i1).get(currArrPos[i1])) > 0) {
                            minString = toMerge.get(i1).get(currArrPos[i1]);
                            minPosition = i1;
                        }
                    } else {
                        if (minString.compareToIgnoreCase(toMerge.get(i1).get(currArrPos[i1])) > 0) {
                            minString = toMerge.get(i1).get(currArrPos[i1]);
                            minPosition = i1;
                        }
                    }
                }
            }
            if (minPosition == -1) {
                break;
            } else {
                ++currArrPos[minPosition];
                if (onlyUnique) {
                    if (caseSense) {
                        if (minString.compareTo(prevMin) != 0) {
                            outputStream.println(minString);
                            prevMin = minString;
                        }
                    } else {
                        if (minString.compareToIgnoreCase(prevMin) != 0) {
                            outputStream.println(minString);
                            prevMin = minString;
                        }
                    }
                } else {
                    outputStream.println(minString);
                }
            }
        }
        if (writeInFile) {
            closeStream(outputStream);
        }
    }
}
