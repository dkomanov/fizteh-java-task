package ru.fizteh.fivt.students.kashinYana.parallelSort;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ParallelSort {

    static public class Pair {
        String string;
        int idWord;

        Pair(String stringNew, int idWordNew) {
            string = stringNew;
            idWord = idWordNew;
        }

        public String toString() {
            return string;
        }
    }

    static int numberWord = 0;
    static final int size = 1024 * 1024;
    static LinkedBlockingQueue<Pair> queue;
    static ExecutorService service;
    static Pair STOP = new Pair("stop", -1);
    static int numberThreads;
    static ArrayList<Pair> ans;
    static Comparator<Pair> comparator;
    static Comparator<String> comparatorString;

    static ComparatorLower comparatorLower = new ComparatorLower();
    static ComparatorNotLower comparatorNotLower = new ComparatorNotLower();


    static String outputFile = null;
    static ArrayList<String> inputString;
    static boolean isI = false;
    static boolean isU = false;
    static boolean isT = false;
    static boolean isO = false;
    static boolean isInput = false;

    public static void main(String[] args) throws Exception {
        Date date = new Date();
        inputString = new ArrayList<String>();
        ans = new ArrayList<Pair>();
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

        if (isI) {
            comparator = new ComparatorLower();
            comparatorString = new ComparatorLowerString();
        } else {
            comparator = new ComparatorNotLower();
            comparatorString = new ComparatorNotLowerString();
        }

        queue = new LinkedBlockingQueue<Pair>();
        service = Executors.newFixedThreadPool(numberThreads);
        Sorter sorter[] = new Sorter[numberThreads];

        try {
            reader(inputString);
        } catch (Exception e) {
            System.err.println("Error in reader");
            System.exit(1);
        }

        for (int i = 0; i < numberThreads; i++) {
            sorter[i] = new Sorter(i);
            sorter[i].start();
        }

        for (int i = 0; i < numberThreads; i++) {
            sorter[i].join();
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.DAYS);

        try {
            printAnswer(outputFile);
        } catch (Exception e) {
            System.err.println("Error in print answer");
            System.exit(1);
        }
        Date date2 = new Date();
        System.out.println(date2.getTime() - date.getTime());
    }

    static void readKeys(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if (!isInput && args[i].charAt(0) == '-') {
                if (args[i].equals("-i")) {
                    isI = true;
                } else if (args[i].equals("-u")) {
                    isU = true;
                } else if (args[i].equals("-iu") || args[i].equals("-ui")) {
                    isI = true;
                    isU = true;
                } else if (args[i].equals("-o")) {
                    outputFile = args[i + 1];
                    i++;
                    isO = true;
                } else if (args[i].equals("-t")) {
                    numberThreads = Integer.parseInt(args[i + 1]);
                    i++;
                    isT = true;
                    if (numberThreads < 1) {
                        throw new Exception("Error in number thread");
                    }
                } else {
                    throw new Exception("Error key " + args[i]);
                }
            } else {
                isInput = true;
                inputString.add(args[i]);
            }
        }
        if (!isT) {
            numberThreads = Runtime.getRuntime().availableProcessors();
        }
    }

    static void reader(ArrayList<String> nameFile) throws Exception {

        BufferedReader in = null;
        FileReader file = null;
        try {
            if (isInput) {
                for (int j = 0; j < nameFile.size(); j++) {     // read from files
                    file = new FileReader(nameFile.get(j));
                    in = new BufferedReader(file);
                    String currentLine;
                    while ((currentLine = in.readLine()) != null) {
                        queue.put(new Pair(currentLine, numberWord));
                        numberWord++;
                    }
                    in.close();
                    file.close();
                }
            } else {                                           // read from stdin
                in = new BufferedReader(new InputStreamReader(System.in));
                String currentLine;
                while ((currentLine = in.readLine()) != null) {
                    queue.put(new Pair(currentLine, numberWord));
                    numberWord++;
                }
            }
        } finally {
            if (in != null && isInput) {
                in.close();
            }
            if (file != null) {
                file.close();
            }
        }
        for (int i = 0; i < numberThreads; i++) {
            queue.put(STOP);
        }
    }

    static class Sorter extends Thread {
        ArrayList<Pair> array;
        int name;

        Sorter(int nameNew) {
            name = nameNew;
            array = new ArrayList<Pair>(size);
        }

        public void run() {
            while (true) {
                boolean isStop = false;
                if (array.size() < size) {
                    try {
                        array.add(queue.take());
                        if (array.get(array.size() - 1) == STOP) {
                            array.remove(array.size() - 1);
                            isStop = true;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                }
                if (array.size() >= size || isStop) {
                    service.submit(new Merge(array, name));
                    array = new ArrayList<Pair>();
                    if (isStop) {
                        return;
                    }
                }
            }
        }
    }

    static class Merge extends Thread {
        ArrayList<Pair> array = new ArrayList<Pair>();
        int id;

        public Merge(ArrayList<Pair> arrayNew, int idNew) {
            id = idNew;
            array = arrayNew;
            Collections.sort(array, comparator);
        }

        public void run() {
            ArrayList<Pair> mergeArray;
            mergeArray = ans;
            synchronized (ans) {
                ArrayList<Pair> tempArray = new ArrayList<Pair>();
                int idArray = 0;
                int indexAns = 0;
                while (idArray < array.size() && indexAns < mergeArray.size()) {
                    int resultComparator = comparator.compare(array.get(idArray), mergeArray.get(indexAns));
                    if (resultComparator < 0) {
                        tempArray.add(array.get(idArray));
                        idArray++;
                    } else {
                        tempArray.add(mergeArray.get(indexAns));
                        indexAns++;
                    }
                }
                while (idArray < array.size()) {
                    tempArray.add(array.get(idArray));
                    idArray++;
                }
                while (indexAns < mergeArray.size()) {
                    tempArray.add(mergeArray.get(indexAns));
                    indexAns++;
                }
                mergeArray.clear();
                for (int i = 0; i < tempArray.size(); i++) {
                    mergeArray.add(tempArray.get(i));
                }
            }
        }
    }

    static void printAnswer(String nameFile) throws Exception {
        BufferedWriter log = null;
        try {
            if (isO) {
                log = new BufferedWriter(new PrintWriter(nameFile));
            } else {
                log = new BufferedWriter(new OutputStreamWriter(System.out));
            }
            Pair last = new Pair("", 0);
            for (int i = 0; i < ans.size(); i++) {
                Pair now = ans.get(i);
                if (i == 0 || !isU || comparatorString.compare(last.toString(), now.toString()) != 0) {
                    log.write(now.toString() + "\n");
                    last = now;
                }
            }
        } catch (Exception e) {
            throw new Exception("Error in writting result.");
        } finally {
            if (log != null && isO) {
                log.close();
            }
        }
    }

    static class ComparatorLower implements Comparator<Pair> {
        public int compare(Pair string1, Pair string2) {
            int ans = String.CASE_INSENSITIVE_ORDER.compare(string1.toString(), string2.toString());
            if (ans == 0) {
                return string1.idWord - string2.idWord;
            } else {
                return ans;
            }
        }
    }

    static class ComparatorLowerString implements Comparator<String> {
        public int compare(String string1, String string2) {
            return String.CASE_INSENSITIVE_ORDER.compare(string1, string2);
        }
    }

    static class ComparatorNotLower implements Comparator<Pair> {
        public int compare(Pair string1, Pair string2) {
            int ans = string1.toString().compareTo(string2.toString());
            if (ans == 0) {
                return string1.idWord - string2.idWord;
            } else {
                return ans;
            }
        }
    }

    static class ComparatorNotLowerString implements Comparator<String> {
        public int compare(String string1, String string2) {
            return string1.compareTo(string2);
        }
    }
}