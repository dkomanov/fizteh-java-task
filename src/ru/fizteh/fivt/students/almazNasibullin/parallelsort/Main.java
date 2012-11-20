package ru.fizteh.fivt.students.almazNasibullin.parallelsort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import ru.fizteh.fivt.students.almazNasibullin.IOUtils;

/**
 * 23.10.12
 * @author almaz
 */

public class Main {
    // соответствует ключу -u
    private static boolean uniqLines = false;
    // кол-во потоков
    private static int countThreads = 0;
    // имя файла, в который необходимо вывести результат
    private static String outputFile = "";
    private static Comparator<String> com = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    };

    public static void main(String[] args) {
        // файлы для считывания
        List<String> files = new ArrayList<String>();

        // строки из входных файлов или из stdin
        List<String> lines;

        if (args.length > 0) {
            readArguments(args, files);
        }

        // считывание строк
        lines = readLines(files);
        
        files.clear();

        int size = lines.size();
        if (size == 0) {
            System.exit(0);
        }

        int countOfLinesForOneThread = 40000;

        if (countThreads == 0) {
            if (size > countOfLinesForOneThread) {
                // каждый поток сортирует примерно countOfLinesForOneThread строк
                countThreads = size / countOfLinesForOneThread + 1;
            }
        } else {
            if (size / countThreads < countOfLinesForOneThread) {
                // если заданное число потоков слишком большое, то используем меньшее кол-во
                countThreads = size / countOfLinesForOneThread + 1;
            }
        }

        if (countThreads > 1) {
            // каждый поток сортирует свою часть и  записывает результат в result
            List<List<String> > result;

            // работа с потоками: сортировка
            result = startingThreadsToSort(lines);
            
            // работа с потоками: слияние
            List<String> res = startingThreadsToMerge(result);
            
            printResult(res);
            res.clear();
        } else {
            // работа без запуска дополнительных потоков по причине малого
            // количества входных строчек

            Collections.sort(lines, com);
            
            printResult(lines);
            lines.clear();
        }
    }

    public static void checkOrderArguments(List<String> files) {
        // предпологается, что все ключи должны стоять перед
        // именами файлов, из которых будет считываться информация
        if (!files.isEmpty()) {
            IOUtils.printErrorAndExit("Bad order. Usage: [-iu] [-t THREAD_COUNT] [-o OUTPUT] [FILES...]");
        }
    }

    public static void readArguments(String[] args, List<String> files) {
        StringBuilder sb = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; ++i) {
            sb.append(" ");
            sb.append(args[i]);
        }
        StringTokenizer st = new StringTokenizer(sb.toString(), " \t");
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            if (str.equals("-i")) {
                checkOrderArguments(files);
                com = String.CASE_INSENSITIVE_ORDER;
            } else if (str.equals("-u")) {
                checkOrderArguments(files);
                uniqLines = true;
            } else if (str.equals("-ui") || str.equals("-iu")) {
                checkOrderArguments(files);
                com = String.CASE_INSENSITIVE_ORDER;
                uniqLines = true;
            } else if (str.equals("-t")) {
                if (files.isEmpty()) {
                    if (st.hasMoreTokens()) {
                        try {
                            countThreads = Integer.parseInt(st.nextToken());
                        } catch (Exception e) {
                            IOUtils.printErrorAndExit("Usage: [-t THREAD_COUNT]." + e.getMessage());
                        }
                    } else {
                        IOUtils.printErrorAndExit("Usage: [-t THREAD_COUNT]");
                    }
                } else {
                    IOUtils.printErrorAndExit("Bad order. Usage: [-iu] [-t THREAD_COUNT]"
                        + "[-o OUTPUT] [FILES...]");
                }
            } else if (str.equals("-o")) {
                if (files.isEmpty()) {
                    if (st.hasMoreTokens()) {
                        outputFile = st.nextToken();
                    } else {
                        IOUtils.printErrorAndExit("Usage: [-o OUTPUT]");
                    }
                } else {
                    IOUtils.printErrorAndExit("Bad order. Usage: [-iu] [-t THREAD_COUNT]"
                        + "[-o OUTPUT] [FILES...]");
                }
            } else {
                files.add(str);
            }
        }
    }

    public static void readFromBufferedReader(BufferedReader br, boolean fromConsole,
            FileReader fr, List<String> lines) {
        try {
            String str;
            while ((str = br.readLine()) != null) {
                lines.add(str);
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad reading from BufferedReader: " + e.getMessage());
        } finally {
            if (!fromConsole) {
                IOUtils.closeOrExit(fr);
            }
            IOUtils.closeOrExit(br);
        }
    }

    public static List<String> readLines(List<String> files) {
        BufferedReader br = null;
        List<String> lines = new ArrayList<String>();
        if (files.isEmpty()) { // считывание из stdin
            try {
                 br = new BufferedReader(new InputStreamReader(System.in));
                 readFromBufferedReader(br, true, null, lines);
            } catch (Exception e) {
                IOUtils.printErrorAndExit("Bad opening BufferedReader: " + e.getMessage());
            } finally {
                IOUtils.closeOrExit(br);
            }
        } else { // считывание из файлов
            for (String file : files) {
                FileReader fr = null;
                try {
                    fr = new FileReader(new File(file));
                    br = new BufferedReader(fr);
                    readFromBufferedReader(br, false, fr, lines);
                } catch (Exception e) {
                    IOUtils.printErrorAndExit("Bad opening BufferedReader: " +
                            e.getMessage());
                } finally {
                    IOUtils.closeOrExit(br);
                }
            }
        }
        return lines;
    }

    public static List<List<String> > startingThreadsToSort(List<String> lines) {
        List<List<String> > result = new ArrayList<List<String> >();

        for (int i = 0; i < countThreads; ++i) {
            result.add(new ArrayList<String>());
        }
        try{
            List<Thread> threads = new ArrayList<Thread>(countThreads);
            int size = lines.size();
            int start = 0;
            int end = size / countThreads - 1;

            for (int i = 0; i < countThreads; ++i) {
                if (i + 1 == countThreads) {
                    end = size - 1;
                }
                Sorter s = new Sorter(lines, start, end, result.get(i), com);
                threads.add(new Thread(s));
                threads.get(i).start();
                start = end + 1;
                end += size / countThreads;
            }

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (Exception e) {
                    IOUtils.printErrorAndExit("Bad joining: " + e.getMessage());
                }
            }
            lines.clear();
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Smth bad happened while threads were sorting: " +
                    e.getMessage());
        }
        return result;
    }

    public static List<String> startingThreadsToMerge(List<List<String> > result) {
        try {
            int size = result.size();
            List<List<String> > res;

            while (size > 1) {
                int curSize = (size + 1) / 2;
                List<Thread> threads = new ArrayList<Thread>(curSize);
                res = new ArrayList<List<String> >(curSize);
                for (int i = 0; i < curSize; ++i) {
                    res.add(new ArrayList<String>());
                }
                int start = 0;
                int end = 1;

                for (int i = 0; i < curSize; ++i) {
                    Merger m;
                    if (i == 0 && size % 2 != 0) {
                        m = new Merger(res.get(i), result, start, end, 0,
                                result.get(start).size() - 1, 0,
                                result.get(end).size() / 2, com);
                        start += 1;
                        end += 1;
                    } else if (i == 1 && size % 2 != 0) {
                        m = new Merger(res.get(i), result, start, end,
                                result.get(start).size() / 2 + 1,
                                result.get(start).size() - 1, 0,
                                result.get(end).size() - 1, com);
                        start += 2;
                        end += 2;
                    } else {
                        m = new Merger(res.get(i), result, start, end, 0,
                                result.get(start).size() - 1, 0,
                                result.get(end).size() - 1, com);
                        start += 2;
                        end += 2;
                    }
                    threads.add(new Thread(m));
                    threads.get(i).start();
                }

                for (Thread t : threads) {
                    try {
                        t.join();
                    } catch (Exception e) {
                        IOUtils.printErrorAndExit("Bad joining: " + e.getMessage());
                    }
                }

                if (start != size) {
                    for (int i = start; i < size; ++i) {
                        res.add(result.get(i));
                    }
                }
                result = res;
                size = result.size();
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Smth bad happened while threads were merging: " +
                    e.getMessage());
        }
        return result.get(0);
    }

    /*public static void removeAndAdd(List<List<String> > result, List<String> res,
            TreeMap<String, List<Pair> > tm) {
        // достаем строчку наименьшую из tm и добавляем в res
        String cur = tm.firstKey();
        Pair p = tm.get(cur).get(0);
        res.add(result.get(p.first).get(p.second));
        tm.get(cur).remove(p);
        if (tm.get(cur).isEmpty()) {
            tm.remove(cur);
        }

        int index = p.second + 1;
        if (withoutReg) {
            if (index < result.get(p.first).size()) {
                if( !cur.equalsIgnoreCase(
                    result.get(p.first).get(index))) {
                        cur = result.get(p.first).get(index);
                        while (index < result.get(p.first).size() && cur.equalsIgnoreCase(
                            result.get(p.first).get(index))) {
                        String toAdd = result.get(p.first).get(index);
                        if (!tm.containsKey(toAdd)) {
                            tm.put(toAdd, new ArrayList<Pair>());
                        }
                        tm.get(toAdd).add(new Pair(p.first, index));
                        ++index;
                    }
                }
            }
        } else {
            if (index < result.get(p.first).size()) {
                if( !cur.equals(
                    result.get(p.first).get(index))) {
                    cur = result.get(p.first).get(index);
                    while (index < result.get(p.first).size() && cur.equals(
                            result.get(p.first).get(index))) {
                        String toAdd = result.get(p.first).get(index);
                        if (!tm.containsKey(toAdd)) {
                            tm.put(toAdd, new ArrayList<Pair>());
                        }
                        tm.get(toAdd).add(new Pair(p.first, index));
                        ++index;
                    }
                }
            }
        }
    }

    public static List<String> MergeResult(List<List<String> > result) {
        // res - результат слияния result
        TreeMap<String, List<Pair> > tm;
        List<String> res= new ArrayList();

        if (withoutReg) {
            tm = new TreeMap<String, List<Pair> >(
                    String.CASE_INSENSITIVE_ORDER);
        } else {
            tm = new TreeMap<String, List<Pair> >();
        }
            
        for (int i = 0; i < result.size(); ++i) {
            String cur = result.get(i).get(0);
            int index = 0;

            if (withoutReg) {
                while (index < result.get(i).size() && cur.equalsIgnoreCase(
                        result.get(i).get(index))) {
                    String toAdd = result.get(i).get(index);
                    if (!tm.containsKey(toAdd)) {
                        tm.put(toAdd, new ArrayList<Pair>());
                    }
                    tm.get(toAdd).add(new Pair(i, index));
                    ++index;
                }
            } else {
                while (index < result.get(i).size() && cur.equals(
                        result.get(i).get(index))) {
                    String toAdd = result.get(i).get(index);
                    if (!tm.containsKey(toAdd)) {
                        tm.put(toAdd, new ArrayList<Pair>());
                    }
                    tm.get(toAdd).add(new Pair(i, index));
                    ++index;
                }
            }
        }

        while (!tm.isEmpty()) {
            removeAndAdd(result, res, tm);
        }
        tm.clear();
        return res;
    }*/

    public static void writeLineToBufferedWriter(BufferedWriter bw,
            String s) {
        try {
            bw.write(s + "\n", 0, s.length() + 1);
        } catch (IOException e) {
            IOUtils.printErrorAndExit("Bad writing to BufferedWriter: " + e.getMessage());
        }
    }

    public static void printResult(List<String> lines) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            if (outputFile.equals("")) {
                bw = new BufferedWriter(new OutputStreamWriter(System.out));
            } else {
                fw = new FileWriter(new File(outputFile));
                bw = new BufferedWriter(fw);
            }
            if (uniqLines) { // вывод уникальных строк
                String prev = lines.get(0);
                writeLineToBufferedWriter(bw, prev);

                for (String s: lines) {
                    if (com.compare(prev, s) != 0) {
                        writeLineToBufferedWriter(bw, s);
                        prev = s;
                    }
                }
            } else {
                for (String s: lines) {
                    writeLineToBufferedWriter(bw, s);
                }
            }
            bw.flush();
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        } finally {
            if (!(outputFile.equals(""))) {
                IOUtils.closeOrExit(fw);
                IOUtils.closeOrExit(bw);
            }
        }
    }
}
