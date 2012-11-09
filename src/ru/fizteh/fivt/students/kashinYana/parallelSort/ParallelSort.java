package ru.fizteh.fivt.students.kashinYana.parallelSort;

import java.io.*;
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
    public static void main(String[] args) throws Exception {
        queue = new LinkedBlockingQueue();
        numberThreads = 4;
        service = Executors.newFixedThreadPool(numberThreads);
        ans = new String[maxSize];
        System.out.println("ok1");
        Sorter sorter[] = new Sorter[numberThreads];
        for (int i = 0; i < numberThreads; i++)
        {
            System.out.println("ok" + (i + 1));
            sorter[i] = new Sorter("" + i);
            sorter[i].start();
        }
        reader("input.txt");

        for(int i = 0; i < numberThreads; i++) {
            sorter[i].join();
        }
        service.shutdown();
        System.out.println("all stops");
        printAnswer("answer.txt");
    }
    static void reader(String nameFile) throws  Exception{
        BufferedReader in = null;
        FileReader file = null;
        try {
            file = new FileReader(nameFile);
            in = new BufferedReader(file);
            while (in.ready()) {
                String currentLine = in.readLine();
                String[] words = currentLine.split("[ \\t\\n.!?,:;]+");
                for (int i = 0; i < words.length; i++) {
                    queue.put(words[i]);
                    System.out.println("put -> " + words[i] + ",");
                }
            }
            for (int i = 0; i < numberThreads; i++)
            {
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
        System.out.println("end reader");
    }
    static class Sorter extends Thread {
        Object array[];
        int last_id = 0;
        String name;
        Sorter (String name_) {
            name = name_;
            System.out.println("create" + name);
        }
        public void run() {
            while(true) {
                boolean isStop = false;
                if(last_id == 0){
                    array = new Object[size];
                }
                if(last_id < size)
                {
                    try {
                        array[last_id++] = queue.take();
                        if(array[last_id - 1] == STOP) {
                            last_id--;
                            isStop = true;
                        }
                        if(last_id > 0 ) {
                            System.out.println("take ->"   +  name + "-> "+ array[last_id - 1].toString());
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                }
                if (last_id >= size || isStop){
                    System.out.println("sort");
                    class Merge implements Runnable {
                        Object[] array;
                        public Merge(Object[] array_) {
                            array = new Object[last_id];
                            for(int i = 0; i < last_id; i++) {
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
                                while(idArray < array.length && indexAns < idAns) {
                                    if(array[idArray].toString().compareTo(ans[indexAns].toString()) < 0) {
                                        tempArray[idTempArray++] = array[idArray++];
                                    } else {
                                        tempArray[idTempArray++] = ans[indexAns++];
                                    }
                                }
                                while(idArray < array.length) {
                                    tempArray[idTempArray++] = array[idArray++];
                                }
                                while(indexAns < idAns) {
                                    tempArray[idTempArray++] = ans[indexAns++];
                                }
                                for(int i = 0; i < idTempArray; i++) {
                                    ans[i] = tempArray[i];
                                }
                                idAns = idTempArray;
                            }
                        }
                    }
                    service.submit(new Merge(array));
                    if(isStop) {
                        System.out.println("stop " + name);
                        return;
                    }
                    last_id = 0;
                }
            }
        }
    }

    static void printAnswer(String nameFile) throws Exception{
        FileWriter out = null;
        File file = null;
        try {
            file = new File(nameFile);
            out =  new FileWriter(file);
            for(int i = 0; i < idAns; i++)
            {
                out.write(ans[i].toString() + "\n");
            }
        }  finally {
            if (out != null) {
                out.close();
            }
        }
    }
}