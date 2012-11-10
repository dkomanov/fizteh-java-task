package ru.fizteh.fivt.students.mysinYurii.wordCounter;

/*
 * Author: Mysin Yurii
 * 
 * Group: 196
 * 
 */

public class WordCounter {
    static boolean toAbsorb;
    
    static boolean caseSensivity;
    
    static boolean toCountLines;
    
    static boolean toCountUnique;
    
    static boolean toCountWords;
    
    static void initialize() {
        toCountWords = true;
        toCountUnique = false;
        toCountLines = false;
        toAbsorb = false;
        caseSensivity = false;
    }
    
    public static void main(String[] args) {
        initialize();
        if (args.length == 0) {
            System.out.println("Данная программа считает количесво слов, линий и различных слов");
            System.out.println("Описание: [флаги] файл1 файл2 ...");
            System.out.println("-w : посчитать количество слов");
            System.out.println("-l : посчитать количество линий");
            System.out.println("-u : вывести различные слова и их количество");
            System.out.println("-U : то же что и -u, только без учета регистра");
            System.out.println("-a : абсорбировать информацию по файлам");
            System.exit(1);
        }
        int lastKeyPos = -1;
        for (String s : args) {
            if (!s.isEmpty()) {
                if ((s.charAt(0) == '-') && (s.lastIndexOf('.') == -1)) {
                    toCountWords = false;
                    break;
                }
            }
        }
        for (int i = 0; i < args.length; ++i) {
            if (args[i].isEmpty()) {
                continue;
            }
            if ((args[i].charAt(0) == '-') && (args[i].lastIndexOf('.') == -1)) {
                lastKeyPos = i;
                for (int j = 1; j < args[i].length(); ++j) {
                    if (args[i].charAt(j) == 'w') {
                        toCountWords = true;
                    } else {
                        if (args[i].charAt(j) == 'l') {
                            toCountLines = true;
                        } else {
                            if (args[i].charAt(j) == 'U') {
                                toCountUnique = true;
                                caseSensivity = false;
                            } else {
                                if (args[i].charAt(j) == 'u') {
                                    toCountUnique = true;
                                    caseSensivity = true;
                                } else {
                                    if (args[i].charAt(j) == 'a') {
                                        toAbsorb = true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                break;
            }
        }
        
        
        /*
         * порядок вывода:
         * 1.количество строк
         * 2.количество слов
         * 3."словарь" с количеством слов
         */
        if (toAbsorb) {
            FileWorker resultGetter = new FileWorker(caseSensivity);
            for (int i = lastKeyPos + 1; i < args.length; ++i) {
                if (args[i].isEmpty()) {
                    continue;
                }
                if (toCountLines) {
                    if (resultGetter.toCountLines(args[i]) == -1) {
                        System.out.println("Error");
                        System.exit(1);
                    }
                }
                if (toCountUnique) {
                    if (resultGetter.toCountUniqueWords(args[i], caseSensivity) == -1) {
                        System.out.println("Error");
                        System.exit(1);
                    }
                }
                if (toCountWords) {
                    if (resultGetter.toCountWords(args[i]) == -1) {
                        System.out.println("Error");
                        System.exit(1);
                    }
                }
            }
            if (toCountLines) {
                System.out.println(resultGetter.lineCount);
            }
            if (toCountWords) {
                System.out.println(resultGetter.wordCount);
            }
            if (toCountUnique) {
                resultGetter.printUniqueWords();
            }
        } else {
            boolean isFirst = true;
            for (int i = lastKeyPos + 1; i < args.length; ++i) {
                if (args[i].isEmpty()) {
                    continue;
                }
                if (isFirst) {
                    System.out.println(args[i] + ":");
                    isFirst = false;
                } else {
                    System.out.println();
                    System.out.println(args[i] + ":");
                }
                if (toCountLines) {
                    FileWorker resultGetter = new FileWorker(caseSensivity);
                    int linesCount = resultGetter.toCountLines(args[i]);
                    if (linesCount != -1) {
                        System.out.println(linesCount);
                    } else {
                        System.out.println("Error");
                        System.exit(1);
                    }
                }
                if (toCountWords) {
                    FileWorker resultGetter = new FileWorker(caseSensivity);
                    int wordsCount = resultGetter.toCountWords(args[i]);
                    if (wordsCount != -1) {
                        System.out.println(wordsCount);
                    } else {
                        System.out.println("Error");
                        System.exit(1);
                    }
                }
                if (toCountUnique) {
                    FileWorker resultGetter = new FileWorker(caseSensivity);
                    if (resultGetter.toCountUniqueWords(args[i], caseSensivity) != -1) {
                        resultGetter.printUniqueWords();
                    } else {
                        System.out.println("Error");
                        System.exit(1);
                    }
                }
            }
        }
    }
}
