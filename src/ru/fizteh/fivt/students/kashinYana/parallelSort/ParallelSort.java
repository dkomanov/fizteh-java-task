package ru.fizteh.fivt.students.kashinYana.parallelSort;

import java.io.*;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Arrays;

/**
 * Student: Yana Kashinskaya, 195 gr.
 */

public class ParallelSort {

    static final int size = 4;
    static LinkedBlockingQueue queue;
    static ExecutorService service;
    static String STOP = "stop";
    static int numberThreads;
    static Object[] ans;
    static int maxSize = 2000;
    static int idAns = 0;
    static String outputFile;
    static Vector<String> inputString;
    static boolean isI = false;
    static boolean isU = false;
    static boolean isT = false;
    static boolean isO = false;

    public static void main(String[] args) throws Exception {
        inputString = new Vector<String>();
        try {
            readKeys(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        queue = new LinkedBlockingQueue();
        service = Executors.newFixedThreadPool(numberThreads);
        ans = new String[maxSize];
        Sorter sorter[] = new Sorter[numberThreads];
        for (int i = 0; i < numberThreads; i++) {
            sorter[i] = new Sorter(new Integer(i).toString());
            sorter[i].start();
        }
        reader(inputString);
        for (int i = 0; i < numberThreads; i++) {
            sorter[i].join();
        }
        service.shutdown();
        printAnswer(outputFile);
    }
    static void readKeys(String[] args) throws Exception{
        boolean readFiles = false;
        for(int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            if(args[i].equals("-i") && !readFiles) {
                isI = true;
            } else if(args[i].equals("-u") && !readFiles) {
                isU = true;
            } else if(args[i].equals("-o") && !readFiles) {
                outputFile = args[i + 1];
                i++;
                isO = true;
            } else if(args[i].equals("-t") && !readFiles) {
                numberThreads = Integer.parseInt(args[i + 1]);
                i++;
                isT = true;
            } else {
                readFiles = true;
                inputString.add(args[i]);
            }
        }
        if(!isT) {
            numberThreads = 4;
        }
        if(!isO) {
            outputFile = "answer.txt";
        }
        if(!((isI && !isU) || (!isI && isU) )) {
            throw new Exception("bad isI and isU");
        }
    }
    static void reader(Vector<String> nameFile) throws Exception {
        for(int j = 0; j < nameFile.size(); j++) {
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
                for (int i = 0; i < numberThreads; i++) {
                    queue.put(STOP);
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
    }

    static class Sorter extends Thread {
        Object array[];
        int last_id = 0;
        String name;

        Sorter(String name_) {
            name = name_;
        }

        public void run() {
            while (true) {
                boolean isStop = false;
                if (last_id == 0) {
                    array = new Object[size];
                }
                if (last_id < size) {
                    try {
                        array[last_id++] = queue.take();
                        if (array[last_id - 1] == STOP) {
                            last_id--;
                            isStop = true;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                }
                if (last_id >= size || isStop) {
                    class Merge implements Runnable {
                        Object[] array;

                        public Merge(Object[] array_) {
                            array = new Object[last_id];
                            for (int i = 0; i < last_id; i++) {
                                array[i] = array_[i];
                            }
                            Arrays.sort(array);
                        }

                        public void run() {
                            synchronized (ans) {
                                Object tempArray[] = new Object[idAns + array.length];
                                int idTempArray = 0;
                                int idArray = 0;
                                int indexAns = 0;
                                while (idArray < array.length && indexAns < idAns) {
                                    if(isI) {
                                        if (array[idArray].toString().toLowerCase().compareTo(ans[indexAns].toString().toLowerCase()) < 0) {
                                            tempArray[idTempArray++] = array[idArray++];
                                        } else {
                                            tempArray[idTempArray++] = ans[indexAns++];
                                        }
                                    } else {
                                        if (array[idArray].toString().compareTo(ans[indexAns].toString()) < 0) {
                                            tempArray[idTempArray++] = array[idArray++];
                                        } else {
                                            tempArray[idTempArray++] = ans[indexAns++];
                                        }
                                    }
                                }
                                while (idArray < array.length) {
                                    tempArray[idTempArray++] = array[idArray++];
                                }
                                while (indexAns < idAns) {
                                    tempArray[idTempArray++] = ans[indexAns++];
                                }
                                for (int i = 0; i < idTempArray; i++) {
                                    ans[i] = tempArray[i];
                                }
                                idAns = idTempArray;
                            }
                        }
                    }
                    service.submit(new Merge(array));
                    if (isStop) {
                        return;
                    }
                    last_id = 0;
                }
            }
        }
    }

    static void printAnswer(String nameFile) throws Exception {
        FileWriter out = null;
        File file = null;
        try {
            file = new File(nameFile);
            out = new FileWriter(file);
            for (int i = 0; i < idAns; i++) {
                out.write(ans[i].toString() + "\n");
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}