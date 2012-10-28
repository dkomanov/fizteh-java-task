package ru.fizteh.fivt.students.almazNasibullin.parallelsort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * 23.10.12
 * @author almaz
 */

public class Main {

    public static void main(String[] args) {
        WrapperPrimitive<Boolean> withoutReg = new WrapperPrimitive<Boolean>(false);
        // соответствует ключу -i
        WrapperPrimitive<Boolean> uniqLines = new WrapperPrimitive<Boolean>(false);
        // соответствует ключу -u
        WrapperPrimitive<Integer> countThreads = new WrapperPrimitive<Integer>(0);
        // кол-во потоков
        WrapperPrimitive<String> outputFile = new WrapperPrimitive<String>("");
        // имя файла, в который необходимо вывести результат
        List<String> files = new ArrayList<String>();
        // файлы для считывания
        List<String> lines = new ArrayList<String>();
        // строки из входных файлов или из stdin

        if (args.length > 0) {
            readArguments(args, withoutReg, uniqLines, countThreads, outputFile, files);
        }
        readLines(lines, files, withoutReg);
        // считывание строк
        
        files.clear();

        int size = lines.size();
        if (size == 0) {
            System.exit(0);
        }

        int countOfLinesForOneThread = 0;
        if (lines.size()  < 10000) {
            countOfLinesForOneThread = 200;
        } else if (lines.size()  < 50000) {
            countOfLinesForOneThread = 290;
        } else if (lines.size()  < 10000) {
            countOfLinesForOneThread = 400;
        } else {
            countOfLinesForOneThread = 600;
        }
        // эти значения найдены примерно для каждого кол-ва входных строк

        if (countThreads.t == 0) {
            if (size > countOfLinesForOneThread) {
                // каждый поток сортирует примерно countOfLinesForOneThread строк
                countThreads.t = size / countOfLinesForOneThread + 1;
            }
        } else {
            if (size / countThreads.t < countOfLinesForOneThread) {
                // если заданное число потоков слишком большое, то используем меньшее кол-во
                countThreads.t = size / countOfLinesForOneThread + 1;
            }
        }

        if (countThreads.t > 1) {
            long time = System.currentTimeMillis();
            List<List<String> > result = new ArrayList<List<String> >();
            // каждый поток сортирует свою часть и  записывает результат в result
            for (int i = 0; i < countThreads.t - 1; ++i) {
                result.add(new ArrayList<String>());
            }
            List<Thread> threads = new ArrayList<Thread>();
            
            startingThreads(lines, countThreads, result, threads);
            // работа с потоками: запуск и ожидание завершения их работы

            List<String> res = new ArrayList<String>();
            // результат слияния полученных результатов всех потоков

            MergeResult(result, res);
            printResult(uniqLines, outputFile, res);
            res.clear();
            System.out.println(System.currentTimeMillis() - time);
        } else {
            // работа без запуска дополнительных потоков по причине малого
            // количества входных строчек
            Collections.sort(lines);
            printResult(uniqLines, outputFile, lines);
            lines.clear();
        }
    }


    public static void printErrorAndExit(String error) {
        System.err.println(error);
        System.exit(1);
    }
    
    public static void checkOrderArguments(WrapperPrimitive<Boolean> key,
            List<String> files) {
        if (files.isEmpty()) {
            // предпологается, что все ключи должны стоять перед 
            // именами файлов, из которых будет считываться информация 
            key.t = true;
        } else {
            printErrorAndExit("Bad order. Usage: [-iu] [-t THREAD_COUNT] [-o OUTPUT] [FILES...]");
        }
    }

    public static void readArguments(String[] args, WrapperPrimitive<Boolean> withoutReg,
            WrapperPrimitive<Boolean> uniqLines, WrapperPrimitive<Integer> countThreads,
            WrapperPrimitive<String> outputFile, List<String> files) {
        StringBuilder sb = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; ++i) {
            sb.append(" ");
            sb.append(args[i]);
        }
        StringTokenizer st = new StringTokenizer(sb.toString(), " \t");
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            if (str.equals("-i")) {
                checkOrderArguments(withoutReg, files);
            } else if (str.equals("-u")) {
                checkOrderArguments(uniqLines, files);
            } else if (str.equals("-ui") || str.equals("-iu")) {
                checkOrderArguments(withoutReg, files);
                checkOrderArguments(uniqLines, files);
            } else if (str.equals("-t")) {
                if (files.isEmpty()) {
                    if (st.hasMoreTokens()) {
                        try {
                            countThreads.t = Integer.parseInt(st.nextToken());
                        } catch (Exception e) {
                            printErrorAndExit("Usage: [-t THREAD_COUNT]." + e.getMessage());
                        }
                    } else {
                        printErrorAndExit("Usage: [-t THREAD_COUNT]");
                    }
                } else {
                    printErrorAndExit("Bad order. Usage: [-iu] [-t THREAD_COUNT]"
                        + "[-o OUTPUT] [FILES...]");
                }
            } else if (str.equals("-o")) {
                if (files.isEmpty()) {
                    if (st.hasMoreTokens()) {
                        outputFile.t = st.nextToken();
                    } else {
                        printErrorAndExit("Usage: [-o OUTPUT]");
                    }
                } else {
                    printErrorAndExit("Bad order. Usage: [-iu] [-t THREAD_COUNT]"
                        + "[-o OUTPUT] [FILES...]");
                }
            } else {
                files.add(str);
            }
        }
    }

    public static void closeOrExit(FileReader fr, BufferedReader br) {
        try {
            if (fr != null) {
                fr.close();
            }
        } catch (IOException e) {
            printErrorAndExit("Bad closing: " + e.getMessage());
        }
        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            printErrorAndExit("Bad closing: " + e.getMessage());
        }
    }

    public static void closeOrExit(FileWriter fw, BufferedWriter bw) {
        try {
            if (fw != null) {
                fw.close();
            }
        } catch (IOException e) {
            printErrorAndExit("Bad closing: " + e.getMessage());
        }
        try {
            if (bw != null) {
                bw.close();
            }
        } catch (IOException e) {
            printErrorAndExit("Bad closing: " + e.getMessage());
        }
    }

    public static void readLines(List<String> lines, List<String> files,
            WrapperPrimitive<Boolean> withoutReg) {
        if (files.isEmpty()) { // считывание из stdin
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(System.in));
                String str;
                while ((str = br.readLine()) != null) {
                    if (withoutReg.t) {
                        lines.add(str.toLowerCase());
                    } else {
                        lines.add(str);
                    }
                }
            } catch (Exception e) {
                printErrorAndExit("Bad reading from console: " + e.getMessage());
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    printErrorAndExit("Bad closing: " + e.getMessage());
                }
            }
        } else { // считывание из файлов
            for (int i = 0; i < files.size(); ++i) {
                BufferedReader br = null;
                FileReader fr = null;
                try {
                    fr = new FileReader(new File(files.get(i)));
                    br = new BufferedReader(fr);
                    String str;
                    while ((str = br.readLine()) != null) {
                        if (withoutReg.t) {
                            lines.add(str.toLowerCase());
                        } else {
                            lines.add(str);
                        }
                    }
                } catch (IOException e) {
                    printErrorAndExit("Bad reading from files: " + e.getMessage());
                } finally {
                    closeOrExit(fr, br);
                }
            }
        }
    }

    public static void startingThreads(List<String> lines,
            WrapperPrimitive<Integer> countThreads, List<List<String> > result,
            List<Thread> threads) {
        try{
            int size = lines.size();
            int start = 0;
            int end = size / (countThreads.t - 1) - 1;

            for (int i = 0; i < countThreads.t - 1; ++i) {
                Sorter s = new Sorter(lines, start, end, result.get(i));
                threads.add(new Thread(s));
                threads.get(i).start();
                start = end + 1;
                end += size / (countThreads.t - 1);
            }
            if (start != size) { // последний поток сортирует оставшуюся последнюю
                // часть массива line, ее размер может быть меньше size / (countThreads.t - 1)
                result.add(new ArrayList<String>());
                Sorter s = new Sorter(lines, start, size - 1, result.get(countThreads.t - 1));
                threads.add(new Thread(s));
                threads.get(countThreads.t - 1).start();
            }

            for (int i = 0; i < threads.size(); ++i) {
                try {
                    threads.get(i).join();
                } catch (Exception e) {
                    printErrorAndExit("Bad joining: " + e.getMessage());
                }
            }
            lines.clear();

            threads.clear();
        } catch (Exception e) {
            printErrorAndExit("Smth bad happened while threads were working: " +
                    e.getMessage());
        }
    }

    public static void removeAndAdd(List<List<String> > result, List<String> res,
            Map<String, List<Pair> > m) {
        // достаем строчку наименьшую из m и добавляем в res
        Iterator it = m.entrySet().iterator();
        String cur = (String)((Map.Entry)it.next()).getKey();
        res.add(cur);
        Pair p = m.get(cur).get(0);
        m.get(cur).remove(p);
        if (m.get(cur).isEmpty()) {
            m.remove(cur);
        }
        if (p.second + 1 < result.get(p.first).size()) {
            String toAdd = result.get(p.first).get(p.second + 1);
            if (!m.containsKey(toAdd)) {
                m.put(toAdd, new ArrayList<Pair>());
            }
            m.get(toAdd).add(new Pair(p.first, p.second + 1));
        }
    }

    public static void MergeResult(List<List<String> > result, List<String> res) {
        // res - результат слияния result
        Map<String, List<Pair> > m = new TreeMap<String, List<Pair> >();
        // хранит в качестве ключа входные слова, в качестве значения массив
        // пар координаты в двумерном массиве result
        for (int i = 0; i < result.size(); ++i) {
            String cur = result.get(i).get(0);
            if (!m.containsKey(cur)) {
                m.put(cur, new ArrayList<Pair>());
            }
            m.get(cur).add(new Pair(i, 0));
        }
        while (!m.isEmpty()) {
            removeAndAdd(result, res, m);
        }
        m.clear();
    }

    public static void printResult(WrapperPrimitive<Boolean> uniqLines,
             WrapperPrimitive<String> outputFile, List<String> lines) {
        int size = lines.size();
        if (outputFile.t.equals("")) { // вывод в stdout
            if (uniqLines.t) { // вывод уникальных строк
                String prev = lines.get(0);
                System.out.println(prev);
                for (int i = 1; i < size; ++i) {
                    if (!lines.get(i).equals(prev)) {
                        System.out.println(lines.get(i));
                        prev = lines.get(i);
                    }
                }
             } else {
                for (int i = 0; i < size; ++i) {
                    System.out.println(lines.get(i));
                }
            }
        } else { // вывод в файл
            BufferedWriter bw = null;
            FileWriter fw = null;
            try {
                fw = new FileWriter(new File(outputFile.t));
                bw = new BufferedWriter(fw);
                if (uniqLines.t) { // вывод уникальных строк
                    String prev = lines.get(0);
                    bw.write(lines.get(0), 0, lines.get(0).length());
                    bw.flush();
                    for (int i = 1; i < size; ++i) {
                        if (!lines.get(i).equals(prev)) {
                            bw.write("\n" + lines.get(i), 0, lines.get(i).length() + 1);
                            bw.flush();
                            prev = lines.get(i);
                        }
                    }
                } else {
                    for (int i = 0; i < size; ++i) {
                        bw.write(lines.get(i), 0, lines.get(i).length());
                        if (!(i + 1 == size)) {
                            bw.write("\n", 0, 1);
                        }
                        bw.flush();
                    }
                }
            } catch (Exception e) {
                printErrorAndExit(e.getMessage());
            } finally {
                closeOrExit(fw, bw);
            }
        }
    }
}
