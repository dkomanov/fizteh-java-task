package ru.fizteh.fivt.students.almazNasibullin.wordcounter;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author almaz
 */

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            int len = args.length;
            int startFile = -1; // номер, с которого начинаются имена файлов
            boolean lines = false;
            boolean words = false;
            boolean uniqWithReg = false;
            boolean uniqWithoutReg = false;
            boolean all = false;
            if (len == 0) {
                throw new Exception("Use format: [keys] FILE1 FILE2 ...");
            }
            for (int i = 0; i < len; ++i) {
                if (args[i].length() == 0) {
                    throw new Exception("Use format: [keys] FILE1 FILE2 ...");
                }
                if (args[i].charAt(0) == '-') {
                    // выделяем все входные ключи
                    for (int j = 1; j < args[i].length(); ++j) {
                        switch (args[i].charAt(j)) {
                            case 'l':
                                lines = true;
                                break;
                            case 'w':
                                words = true;
                                break;
                            case 'u':
                                uniqWithReg = true;
                                break;
                            case 'U':
                                uniqWithoutReg = true;
                                break;
                            case 'a':
                                all = true;
                                break;
                            default:
                                throw new Exception("Invalid keys: " +
                                        args[i].charAt(j));
                        }
                    }
                } else {
                    startFile = i;
                    break;
                }
            }
            if (startFile == -1) {
                throw new Exception("There are no files in arguments!");
            }
            if (startFile == 0) {
                words = true;
            }
            if ((uniqWithReg || uniqWithoutReg) && !lines && !words) {
                words = true; // случай когда указан только ключ -U или -u
            }
            checkKey(words, lines, uniqWithReg, uniqWithoutReg);
            if (all) { // указан ключ агрегировать результаты
                printInfo(args, words, lines, uniqWithReg, uniqWithoutReg,
                        startFile);
            } else {
                for (int i = startFile; i < args.length; ++i) {
                    printInfo(args[i], words, lines, uniqWithReg, uniqWithoutReg);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    static void checkKey(boolean words, boolean lines,
            boolean uniqWithReg, boolean uniqWithoutReg) {
        if (words && lines) { // указаны ключи -w и -l одновременно
            System.out.println("Keys '-w', '-l' are not compatible!");
            System.exit(0);
        }
        if (uniqWithReg && uniqWithoutReg) { // указаны ключи -U и -u одновременно
            System.out.println("Keys '-U', '-u' are not compatible!");
            System.exit(0);
        }
        if (lines && uniqWithoutReg) { // указаны ключи -U и -l одновременно
            System.out.println("Keys '-U', '-l' are not compatible!");
            System.exit(0);
        }
        if (lines && uniqWithReg) { // указаны ключи -u и -l одновременно
            System.out.println("Keys '-u', '-l' are not compatible!");
            System.exit(0);
        }
    }

    static void printInfo(String s, boolean words, boolean lines,
            boolean uniqWithReg, boolean uniqWithoutReg) {
        // печатаем запрашиваемую информацию для одного файла
        if (lines) { // кол-во строк
            int count = FileRead.countLines(s);
            System.out.println(s + ":");
            System.out.println(count);
        } else if (words && !uniqWithReg && !uniqWithoutReg) { // кол-во слов
            int count = FileRead.countWords(s);
            System.out.println(s + ":");
            System.out.println(count);
        } else if (words && uniqWithReg) { // уникальные слова с учетом регистра
            Map<String, Integer> m = new TreeMap<String, Integer>();
            m = FileRead.countUniqWordsWithRegistr(s);
            System.out.println(s + ":");
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                System.out.println(pairs.getKey() + " " + pairs.getValue());
            }
            m.clear();
        } else if (words && uniqWithoutReg) {// уникальные слова без учетом регистра
            Map<String, Integer> m = new TreeMap<String, Integer>();
            m = FileRead.countUniqWordsWithoutRegistr(s);
            System.out.println(s + ":");
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                System.out.println(pairs.getKey() + " " + pairs.getValue());
            }
            m.clear();
        }
    }

    static void printInfo(String [] args, boolean words, boolean lines,
            boolean uniqWithReg, boolean uniqWithoutReg, int startFile) {
        // печатаем запрашиваемую информацию для всех файлов
        if (lines) { // кол-во строк
            int count = 0;
            for (int i = startFile; i < args.length; ++i) {
                count += FileRead.countLines(args[i]);
            }
            System.out.println(count);
        } else if (words && !uniqWithReg && !uniqWithoutReg) { // кол-во слов
            int count = 0;
            for (int i = startFile; i < args.length; ++i) {
                count += FileRead.countWords(args[i]);
            }
            System.out.println(count);
        } else if (words && uniqWithReg) { // уникальные слова с учетом регистра
            Map<String, Integer> m = new TreeMap<String, Integer>();
            for (int i = startFile; i < args.length; ++i) {
                m.putAll(FileRead.countUniqWordsWithRegistr(args[i]));
            }
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                System.out.println(pairs.getKey() + " " + pairs.getValue());
            }
            m.clear();
        } else if (words && uniqWithoutReg) {// уникальные слова без учетом регистра
            Map<String, Integer> m = new TreeMap<String, Integer>();
            for (int i = startFile; i < args.length; ++i) {
                m.putAll(FileRead.countUniqWordsWithoutRegistr(args[i]));
            }
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                System.out.println(pairs.getKey() + " " + pairs.getValue());
            }
            m.clear();
        } else {
            System.out.println("Keys are not compatible!");
            System.exit(0);
        }
    }
}
