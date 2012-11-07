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
    
    WordCounter() {
    
        toAbsorb = false;
        caseSensivity = true;
        toCountLines = false;
        toCountUnique = false;
        toCountWords = true;
    }
    
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Данная программа считает количесво слов, линий и различных слов");
            System.out.println("Описание: [флаги] список файлов");
            System.out.println("-w : посчитать количество слов");
            System.out.println("-l : посчитать количество линий");
            System.out.println("-u : вывести различные слова и их количество без учета регистра");
            System.out.println("-U : то же что и -u, только с учетом регистра");
            System.out.println("-a : абсорбировать информацию по файлам");
            System.exit(1);
        }
        int lastKeyPos = 0;
        
        if ((args[0].charAt(0) == '-') && (args[0].lastIndexOf('.') == -1)) {
            toCountWords = false;
            for (int i = 0; i < args.length; ++i) {
                if (args[i].charAt(0) == '-') {
                    if (args[i].lastIndexOf('.') == -1) {
                        lastKeyPos = i;
                        for (int j = 1; j < args[i].length(); ++j) {
                            if (args[i].charAt(j) == 'w') {
                                toCountWords = true;
                            }
                            if (args[i].charAt(j) == 'l') {
                                toCountLines = true;
                            }
                            if (args[i].charAt(j) == 'U') {
                                toCountUnique = true;
                                caseSensivity = true;
                            }
                            if (args[i].charAt(j) == 'u') {
                                toCountUnique = true;
                                caseSensivity = false;
                            }
                            if (args[i].charAt(j) == 'a') {
                                toAbsorb = true;
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        
        /*
         * порядок вывода:
         * 1.количество строк
         * 2.количество слов
         * 3."словарь" с количеством слов
         */
        if (toAbsorb) {
            FileWorker resultGetter = new FileWorker();
            for (int i = lastKeyPos + 1; i < args.length; ++i) {
                if (toCountLines) {
                    resultGetter.toCountLines(args[i]);
                }
                if (toCountUnique) {
                    resultGetter.toCountUniqueWords(args[i], caseSensivity);
                }
                if (toCountWords) {
                    resultGetter.toCountWords(args[i]);
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
            for (int i = lastKeyPos + 1; i < args.length; ++i) {
                if ((args.length - lastKeyPos - 1) > 1) {
                    if (i != lastKeyPos + 1) {
                        System.out.println();
                    }
                    System.out.println(args[i] + ':');
                }
                if (toCountLines) {
                    FileWorker resultGetter = new FileWorker();
                    int linesCount = resultGetter.toCountLines(args[i]);
                    if (linesCount != -1) {
                        System.out.println(linesCount);
                    }
                }
                if (toCountWords) {
                    FileWorker resultGetter = new FileWorker();
                    int wordsCount = resultGetter.toCountWords(args[i]);
                    if (wordsCount != -1) {
                        System.out.println(wordsCount);
                    }
                }
                if (toCountUnique) {
                    FileWorker resultGetter = new FileWorker();
                    if (resultGetter.toCountUniqueWords(args[i], caseSensivity) != -1) {
                        resultGetter.printUniqueWords();
                    }
                }
            }
        }
    }
}
