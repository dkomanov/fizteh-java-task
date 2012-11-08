package ru.fizteh.fivt.students.frolovNikolay.wordCounter;

import java.util.TreeMap;

/*
 * WordCounter
 * Фролов Николай
 * 196 группа
 */
public class Handler {
    
    public static void main(String[] argv) {
        if (argv.length == 0) {
            System.out.println("Usage: java WordCounter [keys] FILE1 FILE2 ...");
            System.exit(1);
        }
        int i = 0;
        boolean countLines = false;
        boolean countWords = false;
        boolean uniqWithReg = false;
        boolean uniqWithoutReg = false;
        boolean agregate = false;
        
        // Расчет и проверка корректности входных данных.
        try {
            for (; i < argv.length; ++i) {
                if (argv[i].isEmpty()) {
                    throw new Exception("Error! Empty argument");
                }
                if (argv[i].charAt(0) == '-') {
                    if (argv[i].length() < 2) {
                        throw new Exception("Error! Incorrect keys");
                    } else {
                        for (int j = 1; j < argv[i].length(); ++j) {
                            switch (argv[i].charAt(j)) {
                            case 'l':
                                if (countLines) {
                                    throw new Exception("Error! Incorrect keys");
                                }
                                countLines = true;
                                break;
                            case 'w':
                                if (countWords) {
                                    throw new Exception("Error! Incorrect keys");
                                }
                                countWords = true;
                                break;
                            case 'u':
                                if (uniqWithReg) {
                                    throw new Exception("Error! Incorrect keys");
                                }
                                uniqWithReg = true;
                                break;
                            case 'U':
                                if (uniqWithoutReg) {
                                    throw new Exception("Error! Incorrect keys");
                                }
                                uniqWithoutReg = true;
                                break;
                            case 'a':
                                if (agregate) {
                                    throw new Exception("Error! Incorrect keys");
                                }
                                agregate = true;
                                break;
                            default:
                                throw new Exception("Error! Incorrect keys");
                            }
                        }
                    }
                } else {
                    break;
                }
            }
            if (countWords && countLines) {
                throw new Exception("Error! Incorrect keys");
            } else if (!countLines) {
                countWords = true;
            }
            if (uniqWithoutReg && uniqWithReg) {
                throw new Exception("Error! Incorrect keys");
            }
            if (i == argv.length) {
                throw new Exception("Error! No input files");
            }
        } catch (Exception keyCrush) {
            System.err.println(keyCrush.getMessage());
            System.exit(1);
        }
        
        if (uniqWithoutReg || uniqWithReg) {
            boolean ignoreReg = uniqWithoutReg;
            TreeMap<String, Integer> totalTable = null;
            TreeMap<String, Integer> table = null;
            for (; i < argv.length; ++i) {
                try {
                    if (countWords) {
                        table = WordCounter.countUniqWords(argv[i], ignoreReg);
                    } else {
                        table = WordCounter.countUniqLines(argv[i], ignoreReg);
                    }
                    if (!agregate) {
                        System.out.println(argv[i] + ':');
                        for (String iter : table.keySet()) {
                            System.out.println(iter + ' ' + table.get(iter));
                        }
                        table.clear();
                    } else {
                        if (totalTable == null) {
                            totalTable = table;
                        } else {
                            for (String iter : table.keySet()) {
                                if (totalTable.containsKey(iter)) {
                                    totalTable.put(iter, totalTable.get(iter) + table.get(iter));
                                } else {
                                    totalTable.put(iter, table.get(iter));
                                }
                            }
                        }
                        
                    }
                } catch (Exception crush) {
                    System.err.println(crush.getMessage());
                    System.exit(1);
                }
            }
            if (agregate) {
                for (String iter : totalTable.keySet()) {
                    System.out.println(iter + ' ' + totalTable.get(iter));
                }
            }
        } else {
            long number = 0;
            for (; i < argv.length; ++i) {
                try {
                    if (countLines) {
                        number += WordCounter.countLines(argv[i]);
                    } else {
                        number += WordCounter.countWords(argv[i]);
                    }
                } catch (Exception crush) {
                    System.err.println(crush.getMessage());
                    System.exit(1);
                }
                if (!agregate) {
                    System.out.println(argv[i] + ':');
                    System.out.println(number);
                    number = 0;
                }
            }
            if (agregate) {
                System.out.println(number);
            }
        }
    }
}