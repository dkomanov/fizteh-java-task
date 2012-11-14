package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ParallelSort {
    static private ResultContainer finish = null;
    static private boolean ignoreCase = false;
    static private boolean unique = false;
    static private int maxReaders = 3;
    static private int maxSorters;
    static private String fileName = null;
    static private int queueNum = 4; //experimental constant, seems to be good enough...

    private static void printUsage() {
        System.out.println("");
    }

    private static int parseOptions(String[] args) {
        int currIndex = 0;
        while (currIndex < args.length  &&  args[currIndex].charAt(0) == '-') {
            if (args[currIndex].equals("-o")) {
                if (currIndex + 1 == args.length) {
                    printUsage();
                    System.exit(1);
                } else {
                    fileName = args[currIndex + 1];
                }
                currIndex += 2;
            } else if (args[currIndex].equals("-t")) {
                if (currIndex + 1 == args.length) {
                    printUsage();
                    System.exit(1);
                } else {
                    maxSorters = Integer.parseInt(args[currIndex + 1]);
                }
                currIndex += 2;
            } else {
                for(int i = 1; i < args[currIndex].length(); i++) {
                    switch(args[currIndex].charAt(i)) {
                        case ('u') :
                            unique = true;
                            break;
                        case ('i') :
                            ignoreCase = true;
                            break;
                        default:
                            printUsage();
                            System.exit(1);
                    }
                }
                currIndex++;
            }
        }
        return currIndex;
    }

    public static void main(String[] args) throws Exception{
        maxSorters = Runtime.getRuntime().availableProcessors();
        ExecutorService sorters = null;
        ExecutorService readers = null;
        int firstFileIndex;
        if (args.length == 0  ||  (firstFileIndex = parseOptions(args)) == args.length) {
            maxReaders = 1;
            sorters = Executors.newFixedThreadPool(maxSorters);
            readers = Executors.newFixedThreadPool(maxReaders);
            finish = new ResultContainer(ignoreCase, new ArrayList<StringContainer>());
            readers.execute(new Reader(null, 0, sorters, ignoreCase, finish));
        } else {
            if (args.length - firstFileIndex < maxReaders) {
                maxReaders = 1; //it seems to be the best variant
            }
            sorters = Executors.newFixedThreadPool(maxSorters);
            readers = Executors.newFixedThreadPool(maxReaders);
            finish = new ResultContainer(ignoreCase, new ArrayList<StringContainer>());
            for (int i = firstFileIndex; i < args.length; i++) {
                readers.execute(new Reader(args[i], i, sorters, ignoreCase, finish));
            }
        }
        readers.shutdown();
        readers.awaitTermination(1, TimeUnit.DAYS);
        sorters.shutdown();
        sorters.awaitTermination(1, TimeUnit.DAYS);
        finish.print(unique, fileName);
    }
}
