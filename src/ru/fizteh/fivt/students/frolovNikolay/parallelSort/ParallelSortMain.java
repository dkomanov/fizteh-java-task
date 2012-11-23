package ru.fizteh.fivt.students.frolovNikolay.parallelSort;

import java.io.BufferedReader;
import ru.fizteh.fivt.students.frolovNikolay.Closer;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Parallel Sort. Фролов Николай 196 группа.
 * Исполнительный класс.
 */
public class ParallelSortMain {
    private static void incorrectArgumentHandler() {
        System.err.println("Error! Incorrect arguments.");
        System.exit(1);
    }

    public static void main(String[] args) {

        // Обработка аргументов.
        boolean isSetProcessorNumber = false;
        boolean withoutReg = false;
        boolean uniqLines = false;
        int processorsNumber = 0;
        String outputFileName = null;
        int i = 0;
        for (; i < args.length; ++i) {
            if (args[i].isEmpty()) {
                incorrectArgumentHandler();
            } else {
                if (args[i].charAt(0) == '-') {
                    if (args[i].length() < 2 || args[i].length() > 3) {
                        incorrectArgumentHandler();
                    } else if (args[i].length() == 3) {
                        for (int j = 1; j < args[i].length(); ++j) {
                            switch (args[i].charAt(j)) {
                            case 'i': 
                                if (withoutReg) {
                                    incorrectArgumentHandler();
                                } else {
                                    withoutReg = true;;
                                }
                                break;
                            case 'u':
                                if (uniqLines) {
                                    incorrectArgumentHandler();
                                } else {
                                    uniqLines = true;
                                }
                                break;
                            default:
                                incorrectArgumentHandler();
                            }
                        }
                    } else {
                        switch (args[i].charAt(1)) {
                        case 'i': 
                            if (withoutReg) {
                                incorrectArgumentHandler();
                            } else {
                                withoutReg = true;;
                            }
                            break;
                        case 'u':
                            if (uniqLines) {
                                incorrectArgumentHandler();
                            } else {
                                uniqLines = true;
                            }
                            break;
                        case 't':
                            if (isSetProcessorNumber) {
                                incorrectArgumentHandler();
                            } else {
                                isSetProcessorNumber= true;
                                if (i + 1 == args.length) {
                                    incorrectArgumentHandler();
                                } else {
                                    ++i;
                                    for (int j = 0; j < args[i].length(); ++j) { 
                                        if (!Character.isDigit(args[i].charAt(j))) {
                                            incorrectArgumentHandler();
                                        }
                                    }
                                    try {
                                        processorsNumber = Integer.valueOf(args[i]);
                                    } catch (Exception crush) {
                                        System.err.println(crush.getMessage());
                                        System.exit(1);
                                    }
                                }
                            }
                            break;
                        case 'o':
                            if (outputFileName != null) {
                                incorrectArgumentHandler();
                            } else if (i + 1 == args.length) {
                                    incorrectArgumentHandler();
                            } else {
                                ++i;
                                outputFileName = args[i];
                            }
                            break;
                        default:
                            incorrectArgumentHandler();
                        }
                    }
                } else {
                    break;
                }
            }
        }
        if (!isSetProcessorNumber) {
            Runtime info = Runtime.getRuntime();
            processorsNumber = info.availableProcessors();
        }
        if (processorsNumber < 1) {
            System.err.println("Error! Incorrect number of threads.");
            System.exit(1);
        }
        PrintStream output = null;
        try {
            if (outputFileName != null) {
                output = new PrintStream(outputFileName);
            } else {
                output = new PrintStream(System.out);
            }
        } catch (Exception crush) {
            System.err.println(crush.getMessage());
            System.exit(1);
        }

        // Считывание и раздача потокам информации.
        try {
            ArrayList<String> result = new ArrayList<String>();
            if (i == args.length) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String current = null;
                while ((current = reader.readLine()) != null) {
                    result.add(current);
                }
            } else {
                FileReader fReader = null;
                BufferedReader reader = null;
                try {
                    for (; i < args.length; ++i) {
                        fReader = new FileReader(args[i]);
                        reader = new BufferedReader(fReader);
                        String current = null;
                        while ((current = reader.readLine()) != null) {
                            result.add(current);
                        }
                        Closer.close(fReader);
                        Closer.close(reader);
                    }
                } finally {
                    Closer.close(fReader);
                    Closer.close(reader);
                }
            }
            ExecutorService sorters = Executors.newFixedThreadPool(processorsNumber);
            int length = result.size() / processorsNumber;
            ArrayList<StringSorter> stringSorters = new ArrayList<StringSorter>();
            for (int j = 0; j < processorsNumber; ++j) {
                if (j + 1 != processorsNumber) {
                    stringSorters.add(new StringSorter(result, withoutReg, length * j, length * (j + 1)));
                    
                } else {
                    stringSorters.add(new StringSorter(result, withoutReg, length * j, result.size()));
                }
                sorters.execute(stringSorters.get(j));
            }
    
            // Завершаем потоки.
            sorters.shutdown();
            sorters.awaitTermination(1, TimeUnit.DAYS);
            
            // Слияния + вывод результатов.
            Merger merger = new Merger(stringSorters, withoutReg);
            String last = null;
            String current = null;
            while ((current = merger.getNext()) != null) {
                if (!uniqLines || last == null 
                    || (withoutReg && !last.equalsIgnoreCase(current))
                    || (!withoutReg && !last.equals(current))) {
                    output.println(current);
                }
                last = current;
            }
        } catch (Exception crush) {
            if (outputFileName != null) {
                Closer.close(output);
            }
            System.err.println(crush.getMessage());
            System.exit(1);
        } finally {
            if (outputFileName != null) {
                Closer.close(output);
            }
        }
    }
}