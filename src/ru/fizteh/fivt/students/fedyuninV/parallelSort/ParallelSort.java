package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.ArrayList;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ParallelSort {

    static private ResultContainer result = null;
    static private boolean ignoreCase = false;
    static private boolean unique = false;
    static private int maxReaders = 3;
    static private int maxSorters;
    static private String fileName = null;
    static private ArrayList<ArrayList<String>> container;

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
        container = new ArrayList<ArrayList<String>>();
        ArrayList<Thread> readers = new ArrayList<Thread>(maxReaders);
        ArrayList<Object> readersStartSem = new ArrayList<Object>(maxReaders);
        ArrayList<Object> readersFinishSem = new ArrayList<Object>(maxReaders);
        for (int i = 0; i < maxReaders; i++) {
            readersStartSem.add(new Object());
            readersFinishSem.add(new Object());
        }
        ArrayList<String> fileNames = new ArrayList<String>(maxReaders);
        result = new ResultContainer(ignoreCase);
        int firstFileIndex;
        ArrayList<Thread> sorters = null;
        ArrayList<Object> sortersStartSem = null;
        ArrayList<Object> sortersFinishSem = null;
        if(args.length == 0  ||  (firstFileIndex = parseOptions(args)) == args.length) {
            maxReaders = 1;
            sorters = new ArrayList<Thread>(maxSorters);
            sortersStartSem = new ArrayList<Object>(maxSorters);
            sortersFinishSem = new ArrayList<Object>(maxSorters);
            for (int i = 0; i < maxSorters; i++) {
                sortersStartSem.add(new Object());
                sortersFinishSem.add(new Object());
                container.add(new ArrayList<String>());
            }
            for(int i = 0; i < maxSorters; i++) {
                sorters.add(i, new Thread(new StringSorter(container.get(i % maxSorters), ignoreCase,
                        sortersStartSem.get(i), sortersFinishSem.get(i), result)));
                sorters.get(i).start();
            }
            readers.add(0, new Thread(new FileWorker(null, container,
                    readersStartSem.get(0), readersFinishSem.get(0), sortersStartSem, sortersFinishSem)));
            readers.get(0).start();
        } else {
            sorters = new ArrayList<Thread>(maxSorters);
            sortersStartSem = new ArrayList<Object>(maxSorters);
            sortersFinishSem = new ArrayList<Object>(maxSorters);
            for (int i = 0; i < maxSorters; i++) {
                sortersStartSem.add(new Object());
                sortersFinishSem.add(new Object());
                container.add(new ArrayList<String>());
            }
            for(int i = 0; i < maxSorters; i++) {
                sorters.add(i, new Thread(new StringSorter(container.get(i % maxSorters), ignoreCase,
                        sortersStartSem.get(i), sortersFinishSem.get(i), result)));
                sorters.get(i).start();
            }
            for (int i = 0; i < maxReaders; i++) {
                readers.add(new Thread(new FileWorker(fileNames.get(i), container,
                        readersStartSem.get(i), readersFinishSem.get(i), sortersStartSem, sortersFinishSem)));
                readers.get(i).start();
            }
            for (int i = firstFileIndex; i < args.length; i++) {
                readersFinishSem.get(i % maxReaders).wait();
                fileNames.set(i % maxReaders, args[i]);
                readersStartSem.get(i % maxReaders).notify();
            }
            for (int i = 0; i < maxReaders; i++) {
                readers.get(i).interrupt();
            }
        }
        for(int i = 0; i < sorters.size(); i++) {
            sorters.get(i).interrupt();
        }
        for(int i = 0; i < sorters.size(); i++) {
            sorters.get(i).join();
        }
        result.print(unique, fileName);
        return;
    }
}
