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
        int idFile, idWord;

        Pair(String stringNew, int idFileNew, int idWordNew) {
            string = stringNew;
            idFile = idFileNew;
            idWord = idWordNew;
        }

        public String merge() {
            return string + " " + idFile + " " + idWord;
        }

        public String toString() {
            return string;
        }
    }

    static final int size = 1024 * 1024;
    static LinkedBlockingQueue queue;
    static ExecutorService service;
    static String STOP = "stop";
    static int numberThreads;
    static ArrayList<Pair> ans;
    static ArrayList<Pair> ans2;

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
        ans = new ArrayList();
        ans2 = new ArrayList();
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

        Merge merge = new Merge(ans2, 0);
        merge.run();
        merge.join();

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
            if(!isInput && args[i].charAt(0) == '-') {
                if(args[i].equals("-i")) {
                    isI = true;
                } else if(args[i].equals("-u")) {
                    isU = true;
                } else if(args[i].equals("-iu") || args[i].equals("-ui")) {
                    isI = true;
                    isU = true;
                } else if(args[i].equals("-o")) {
                    outputFile = args[i + 1];
                    i++;
                    isO = true;
                } else if(args[i].equals("-t")) {
                    numberThreads = Integer.parseInt(args[i + 1]);
                    i++;
                    isT = true;
                    if(numberThreads < 1) {
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
                    int numberWord = 0;
                    file = new FileReader(nameFile.get(j));
                    in = new BufferedReader(file);
                    while (in.ready()) {
                        String currentLine = in.readLine();
                        queue.put(new Pair(currentLine, j, numberWord));
                        numberWord++;
                    }
                    in.close();
                    file.close();
                }
            } else {                                           // read from stdin
                in = new BufferedReader(new InputStreamReader(System.in));
                String currentLine;
                int numberWord = 0;
                while ((currentLine = in.readLine()) != null) {
                    queue.put(new Pair(currentLine, 1, numberWord));
                    numberWord++;
                }
                in.close();
            }
        } finally {
            if (in != null) {
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
        ArrayList array;
        int name;

        Sorter(int nameNew) {
            name = nameNew;
            array = new ArrayList();
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
                    array = new ArrayList();
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
            if (isI) {
                Collections.sort(array, new ComparatorLower());
            } else {
                Collections.sort(array, new ComparatorNotLower());
            }
        }

        public void run() {
            ArrayList<Pair> mergeArray;
            if (id % 2 == 0) {
                mergeArray = ans;
            } else {
                mergeArray = ans2;
            }
            synchronized (mergeArray) {
                ArrayList<Pair> tempArray = new ArrayList<Pair>();
                int idArray = 0;
                int indexAns = 0;
                while (idArray < array.size() && indexAns < mergeArray.size()) {
                    if (isI) {
                        if (new ComparatorLower().compare(array.get(idArray), mergeArray.get(indexAns)) < 0) {
                            tempArray.add(array.get(idArray));
                            idArray++;
                        } else {
                            tempArray.add(mergeArray.get(indexAns));
                            indexAns++;
                        }
                    } else {
                        if (new ComparatorNotLower().compare(array.get(idArray), mergeArray.get(indexAns)) < 0) {
                            tempArray.add(array.get(idArray));
                            idArray++;
                        } else {
                            tempArray.add(mergeArray.get(indexAns));
                            indexAns++;
                        }
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
        FileWriter out = null;
        File file = null;
        try {
            if (isO) {
                file = new File(nameFile);
                out = new FileWriter(file);
                for (int i = 0; i < ans.size(); i++) {
                    if (!isU || i == 0) {
                        out.write(ans.get(i).toString() + "\n");
                    } else {
                        String last = ans.get(i - 1).toString();
                        String now = ans.get(i).toString();
                        if (isI) {
                            last = last.toLowerCase();
                            now = now.toLowerCase();
                        }
                        if (!last.equals(now)) {
                            out.write(ans.get(i).toString() + "\n");
                        }
                    }
                }
            } else {
                for (int i = 0; i < ans.size(); i++) {
                    System.out.println(ans.get(i).toString());
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    static class ComparatorLower implements Comparator<Pair> {
        public int compare(Pair string1, Pair string2) {
            int ans = String.CASE_INSENSITIVE_ORDER.compare(string1.toString(), string2.toString());
            if (ans < 0) {
                return -1;
            } else if (ans == 0) {
                if(string1.idFile < string2.idFile) {
                    return -1;
                } else if (string1.idFile > string2.idFile) {
                    return 1;
                } else {
                    if(string1.idWord < string2.idWord) {
                        return -1;
                    } else if (string1.idWord > string2.idWord) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } else {
                return 1;
            }
        }
    }

    static class ComparatorNotLower implements Comparator<Pair> {
        public int compare(Pair string1, Pair string2) {
            int ans = string1.toString().compareTo(string2.toString());
            if (ans < 0) {
                return -1;
            } else if (ans == 0) {
                if(string1.idFile < string2.idFile) {
                    return -1;
                } else if (string1.idFile > string2.idFile) {
                    return 1;
                } else {
                    if(string1.idWord < string2.idWord) {
                        return -1;
                    } else if (string1.idWord > string2.idWord) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } else {
                return 1;
            }
        }
    }
}