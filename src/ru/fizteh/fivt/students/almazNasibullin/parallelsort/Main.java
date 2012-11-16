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
import java.util.List;
import java.util.StringTokenizer;
import ru.fizteh.fivt.students.almazNasibullin.IOUtils;

/**
 * 23.10.12
 * @author almaz
 */

public class Main {
    // соответствует ключу -i
    private static boolean withoutReg = false;
    // соответствует ключу -u
    private static boolean uniqLines = false;
    // кол-во потоков
    private static int countThreads = 0;
    // имя файла, в который необходимо вывести результат
    private static String outputFile = "";

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

            if (withoutReg) {
                Collections.sort(lines, String.CASE_INSENSITIVE_ORDER);
            } else {
                Collections.sort(lines);
            }
            
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
                withoutReg = true;
            } else if (str.equals("-u")) {
                checkOrderArguments(files);
                uniqLines = true;
            } else if (str.equals("-ui") || str.equals("-iu")) {
                checkOrderArguments(files);
                withoutReg = true;
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
            for (int i = 0; i < files.size(); ++i) {
                FileReader fr = null;
                try {
                    fr = new FileReader(new File(files.get(i)));
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
                if (i == 0 && countThreads > 3) {
                    end *= 7;
                    end /= 5;
                }
                if (i + 1 == countThreads) {
                    end = size - 1;
                }
                Sorter s = new Sorter(lines, start, end, result.get(i), withoutReg);
                threads.add(new Thread(s));
                threads.get(i).start();
                start = end + 1;
                end += size / countThreads;
            }

            for (int i = 0; i < threads.size(); ++i) {
                try {
                    threads.get(i).join();
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
                List<Thread> threads = new ArrayList<Thread>(size / 2);
                res = new ArrayList<List<String> >(size / 2);
                for (int i = 0; i < size / 2; ++i) {
                    res.add(new ArrayList<String>());
                }
                int start = 0;
                int end = 1;

                for (int i = 0; i < size / 2; ++i) {
                    Merger m = new Merger(res.get(i), result, start, end, withoutReg);
                    threads.add(new Thread(m));
                    threads.get(i).start();
                    start += 2;
                    end += 2;
                }

                for (int i = 0; i < size / 2; ++i) {
                    try {
                        threads.get(i).join();
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
    
    public static void writeLineToBufferedWriter(BufferedWriter bw,
            List<String> lines, int i) {
        try {
            bw.write(lines.get(i) + "\n", 0, lines.get(i).length() + 1);
            bw.flush();
        } catch (IOException e) {
            IOUtils.printErrorAndExit("Bad writing to BufferedWriter: " + e.getMessage());
        }
    }

    public static void printResult(List<String> lines) {
        int size = lines.size();
        if (outputFile.equals("")) { // вывод в stdout
            if (uniqLines) { // вывод уникальных строк
                String prev;
                if (withoutReg) {
                    prev = lines.get(0).toLowerCase();
                } else {
                    prev = lines.get(0);
                }
                System.out.println(lines.get(0));
                for (int i = 1; i < size; ++i) {
                    if (withoutReg) {
                        if (!lines.get(i).toLowerCase().equals(prev)) {
                            System.out.println(lines.get(i));
                            prev = lines.get(i).toLowerCase();
                        }
                    } else {
                        if (!lines.get(i).equals(prev)) {
                            System.out.println(lines.get(i));
                            prev = lines.get(i);
                        }
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
                fw = new FileWriter(new File(outputFile));
                bw = new BufferedWriter(fw);
                if (uniqLines) { // вывод уникальных строк
                    writeLineToBufferedWriter(bw, lines, 0);
                    String prev;
                    if (withoutReg) {
                        prev = lines.get(0).toLowerCase();
                    } else {
                        prev = lines.get(0);
                    }
                    for (int i = 1; i < size; ++i) {
                        if (withoutReg) {
                            if (!lines.get(i).toLowerCase().equals(prev)) {
                                writeLineToBufferedWriter(bw, lines, i);
                                prev = lines.get(i).toLowerCase();
                            }
                        } else {
                            if (!lines.get(i).equals(prev)) {
                                writeLineToBufferedWriter(bw, lines, i);
                                prev = lines.get(i);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < size; ++i) {
                        writeLineToBufferedWriter(bw, lines, i);
                    }
                }
            } catch (Exception e) {
                IOUtils.printErrorAndExit(e.getMessage());
            } finally {
                IOUtils.closeOrExit(fw);
                IOUtils.closeOrExit(bw);
            }
        }
    }
}
