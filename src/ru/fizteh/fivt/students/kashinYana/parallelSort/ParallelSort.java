package ru.fizteh.fivt.students.kashinYana.parallelSort;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Student: Yana Kashinskaya, 195 gr.
 */

public class ParallelSort {

    static final int size = 32768;
    static LinkedBlockingQueue queue;
    static ExecutorService service;
    static String STOP = "stop";
    static int numberThreads;
    static Vector ans;
    static String outputFile = null;
    static Vector<String> inputString;
    static boolean isI = false;
    static boolean isU = false;
    static boolean isT = false;
    static boolean isO = false;

    public static void main(String[] args) throws Exception {

        inputString = new Vector<String>();
        ans = new Vector();
        if (args.length == 0) {
            System.err.println("[-iu] [-t THREAD_COUNT] [-o OUTPUT] [FILES...]");
            System.exit(1);
        }

        try {
            readKeys(args);
        } catch (Exception e) {
            System.err.println("Error in set keys :" + e.getMessage());
            System.exit(1);
        }

        queue = new LinkedBlockingQueue();
        service = Executors.newFixedThreadPool(numberThreads);
        Sorter sorter[] = new Sorter[numberThreads];

        for (int i = 0; i < numberThreads; i++) {
            sorter[i] = new Sorter(new Integer(i).toString());
            sorter[i].start();
        }
        try {
            reader(inputString);
        } catch (Exception e) {
            System.err.println("Error in reader");
            System.exit(1);
        }

        for (int i = 0; i < numberThreads; i++) {
            sorter[i].join();
        }

        service.shutdown(); //??????????????????????

        try {
            printAnswer(outputFile);
        } catch (Exception e) {
            System.err.println("Error in print answer");
            System.exit(1);
        }
    }

    static void readKeys(String[] args) throws Exception {
        boolean readFiles = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-i") && !readFiles) {
                isI = true;
            } else if (args[i].equals("-u") && !readFiles) {
                isU = true;
            } else if (args[i].equals("-o") && !readFiles) {
                outputFile = args[i + 1];
                i++;
                isO = true;
            } else if (args[i].equals("-t") && !readFiles) {
                numberThreads = Integer.parseInt(args[i + 1]);
                i++;
                isT = true;
            } else {
                readFiles = true;
                inputString.add(args[i]);
            }
        }
        if (!isT) {
            numberThreads = 4;  ///??????????
        }
        if (!((isI && !isU) || (!isI && isU))) {
            throw new Exception("bad isI and isU");
        }
    }

    static void reader(Vector<String> nameFile) throws Exception {
        for (int j = 0; j < nameFile.size(); j++) {
            BufferedReader in = null;
            FileReader file = null;
            try {
                file = new FileReader(nameFile.elementAt(j));
                in = new BufferedReader(file);
                while (in.ready()) {
                    String currentLine = in.readLine();
                    String[] words = currentLine.split("[ \\t\\n.!?,:;]+");
                    for (int i = 0; i < words.length; i++) {
                        queue.put(words[i]);
                    }
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (file != null) {
                    file.close();
                }
            }
        }
        for (int i = 0; i < numberThreads; i++) {
            queue.put(STOP);
        }
    }

    static class Sorter extends Thread {
        Vector array;
        String name;

        Sorter(String name_) {
            name = name_;
            array = new Vector();
        }

        public void run() {
            while (true) {
                boolean isStop = false;
                if (array.size() < size) {
                    try {
                        array.add(queue.take());
                        if (array.lastElement() == STOP) {
                            array.removeElementAt(array.size() - 1);
                            isStop = true;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                }
                if (array.size() >= size || isStop) {
                    class Merge implements Runnable {
                        Vector array = new Vector();

                        public Merge(Vector array_) {
                            array = array_;
                            if (isI) {
                                Collections.sort(array, new ComparatorLower());
                            } else {
                                Collections.sort(array, new ComparatorNotLower());
                            }
                        }

                        public void run() {
                            synchronized (ans) {                 //???????????????????
                                Vector tempArray = new Vector();
                                int idArray = 0;
                                int indexAns = 0;
                                while (idArray < array.size() && indexAns < ans.size()) {
                                    if (isI) {
                                        if (new ComparatorLower().compare(array.elementAt(idArray), ans.elementAt(indexAns)) < 0) {
                                            tempArray.add(array.elementAt(idArray));
                                            idArray++;
                                        } else {
                                            tempArray.add(ans.elementAt(indexAns));
                                            indexAns++;
                                        }
                                    } else {
                                        if (new ComparatorNotLower().compare(array.elementAt(idArray), ans.elementAt(indexAns)) < 0) {
                                            tempArray.add(array.elementAt(idArray));
                                            idArray++;
                                        } else {
                                            tempArray.add(ans.elementAt(indexAns));
                                            indexAns++;
                                        }
                                    }
                                }
                                while (idArray < array.size()) {
                                    tempArray.add(array.elementAt(idArray));
                                    idArray++;
                                }
                                while (indexAns < ans.size()) {
                                    tempArray.add(ans.elementAt(indexAns));
                                    indexAns++;
                                }
                                ans.setSize(0);
                                for (int i = 0; i < tempArray.size(); i++) {
                                    ans.add(tempArray.elementAt(i));
                                }
                            }
                        }
                    }
                    service.submit(new Merge(array));
                    array = new Vector();
                    if (isStop) {
                        return;
                    }
                }
            }
        }
    }

    static void printAnswer(String nameFile) throws Exception {
        FileWriter out = null;
        File file = null;
        try {
            if (isO) {
                file = new File(nameFile);
                out = new FileWriter(file);
                for (int i = 0; i < ans.size(); i++) {
                    out.write(ans.elementAt(i).toString() + "\n");
                }
            } else {
                for (int i = 0; i < ans.size(); i++) {
                    System.out.println(ans.elementAt(i).toString());
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    static class ComparatorLower implements Comparator {
        public int compare(Object string1, Object string2) {
            int ans = string1.toString().toLowerCase().compareTo(string2.toString().toLowerCase());
            if (ans < 0)
                return -1;
            else if (ans == 0)
                return 0;
            else
                return 1;
        }
    }

    static class ComparatorNotLower implements Comparator {
        public int compare(Object string1, Object string2) {
            int ans = string1.toString().compareTo(string2.toString());
            if (ans < 0)
                return -1;
            else if (ans == 0)
                return 0;
            else
                return 1;
        }
    }
}