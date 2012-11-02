package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.ArrayList;

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
        //args = new String[1];
        //args[0] = "../input.txt";
        maxSorters = Runtime.getRuntime().availableProcessors();
        BlockingQueue<ArrayList<String>> queue = new BlockingQueue<ArrayList<String>>();
        ThreadPool sorters = null;
        ThreadPool readers = null;
        int firstFileIndex;
        if (args.length == 0  ||  (firstFileIndex = parseOptions(args)) == args.length) {
            maxReaders = 1;
            sorters = new ThreadPool(maxSorters);
            readers = new ThreadPool(maxReaders);
            finish = new ResultContainer(ignoreCase);
            readers.add(new Reader(null, sorters, ignoreCase, finish));
        } else {
            if (args.length - firstFileIndex < maxReaders) {
                maxReaders = args.length - firstFileIndex;
            }
            sorters = new ThreadPool(maxSorters);
            readers = new ThreadPool(maxReaders);
            finish = new ResultContainer(ignoreCase);
            for (int i = firstFileIndex; i < args.length; i++) {
                readers.add(new Reader(args[i], sorters, ignoreCase, finish));
            }
        }
        //System.out.println("OK");
        readers.start();
        readers.join();
        //System.out.println("OK");
        sorters.start();
        sorters.join();
        finish.print(unique, fileName);
        return;
    }
}
