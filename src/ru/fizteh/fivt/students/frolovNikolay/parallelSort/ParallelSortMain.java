package ru.fizteh.fivt.students.frolovNikolay.parallelSort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
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
        if (processorsNumber < 1 || processorsNumber > 100) {
            System.err.println("Error! Incorrect number of threads.");
            System.exit(1);
        }
        PrintWriter output = null;
        try {
            if (outputFileName != null) {
                output = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
            } else {
                output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
            }
        } catch (Exception crush) {
            System.err.println(crush.getMessage());
            System.exit(1);
        }

        // Считывание и раздача потокам информации.
        String EOF = new String();
        LinkedBlockingQueue<String> threadStringsStream = new LinkedBlockingQueue<String>();
        List< LinkedList<String> > result = Collections.synchronizedList(new LinkedList< LinkedList<String> >());
        ExecutorService sorters = Executors.newFixedThreadPool(processorsNumber);
        for (int j = 0; j < processorsNumber; ++j) {
            sorters.execute(new StringSorter(EOF, threadStringsStream, withoutReg, result));
        }
        if (i == args.length) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String current = null;
            try {
                while ((current = reader.readLine()) != null) {
                    threadStringsStream.add(current);
                }
            } catch(Exception crush) {               
                sorters.shutdown();
                if (outputFileName != null) {
                    Closer.close(output);
                }
                System.err.println(crush.getMessage());
                System.exit(1);
            } finally {
                sorters.shutdown();
                if (outputFileName != null) {
                    Closer.close(output);
                }
            }
        } else {
            for (; i < args.length; ++i) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(args[i]));
                    String current = null;
                    while ((current = reader.readLine()) != null) {
                        threadStringsStream.add(current);
                    }
                } catch(Exception crush) {
                    if (outputFileName != null) {
                        Closer.close(output);
                    }
                    sorters.shutdown();
                    Closer.close(reader);
                    System.err.println(crush.getMessage());
                    System.exit(1);
                } finally {
                    sorters.shutdown();
                    Closer.close(reader);
                }
            }
        }

        // Завершаем потоки.
        for (int j = 0; j < processorsNumber; ++j) {
            threadStringsStream.add(EOF);
        }
        try {
            sorters.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception crush) {
            if (outputFileName != null) {
                Closer.close(output);
            }
            System.err.println(crush.getMessage());
            System.exit(1);
        }
        
        // Слияния + вывод результатов.
        try {
            Merger.merge(result, withoutReg);
        } catch (Exception crush) {
            if (outputFileName != null) {
                Closer.close(output);
            }
            System.err.println(crush.getMessage());
            System.exit(1);
        }
        try {
            if (uniqLines) {
                if (withoutReg) {
                    for (int j = 0; j < result.get(0).size(); ++j) {
                        if (j + 1 == result.get(0).size() || !result.get(0).get(j).equalsIgnoreCase(result.get(0).get(j + 1))) {
                            output.println(result.get(0).get(j));
                        }
                    } 
                } else {
                    for (int j = 0; j < result.get(0).size(); ++j) {
                        if (j + 1 == result.get(0).size() || !result.get(0).get(j).equals(result.get(0).get(j + 1))) {
                            output.println(result.get(0).get(j));
                        }
                    }
                }
            } else {
                for (int j = 0; j < result.get(0).size(); ++j) {
                    output.println(result.get(0).get(j));
                }
            }
        } catch (Exception crush) {
            if (outputFileName != null) {
                Closer.close(output);
            }
            System.err.println(crush.getMessage());
            System.exit(1);
        }
        Closer.close(output);
    }
}