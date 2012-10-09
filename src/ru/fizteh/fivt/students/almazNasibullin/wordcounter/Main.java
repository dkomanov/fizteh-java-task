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
            boolean [] keys = new boolean[5]; // ключи среди аргументов
            for (int i = 0; i < 0; ++i) {
                keys[i] = false;
            }

            if (len == 0) {
                throw new Exception("No arguments!");
            }
            for (int i = 0; i < len; ++i) {
                if (args[i].charAt(0) == '-') {
                    // выделяем все входные ключи
                    getKey(args[i], keys);
                } else {
                    startFile = i;
                    break;
                }
            }
            if (startFile == -1) {
                throw new Exception("There are no files in arguments!");
            }
            if (startFile == 0) {
                keys[0] = true;
            }
            checkKey(keys);
            if (keys[4]) { // указан ключ агрегировать результаты
                printInfo(args, keys, startFile);
            } else {
                for (int i = startFile; i < args.length; ++i) {
                    printInfo(args[i], keys);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    static void getKey(String s, boolean [] keys) throws Exception {
        for (int j = 1; j < s.length(); ++j) {
            switch (s.charAt(j)) {
                case 'l':
                    keys[0] = true;
                    break;
                case 'w':
                    keys[1] = true;
                    break;
                case 'u':
                    keys[2] = true;
                    break;
                case 'U':
                    keys[3] = true;
                    break;
                case 'a':
                    keys[4] = true;
                    break;
                default:
                    throw new Exception("Invalid keys!");
            }
        }
    }

    static void checkKey(boolean [] keys) throws Exception {
        if (keys[0] && keys[1]) { // указаны ключи -w и -l одновременно
            throw new Exception("Keys are not compatible!");
        }
        if (keys[2] && keys[3]) { // указаны ключи -U и -u одновременно
            throw new Exception("Keys are not compatible!");
        }
    }

    static void printInfo(String s, boolean [] keys) throws Exception {
        // печатаем запрашиваемую информацию для одного файла
        if (keys[0] && !keys[2] && !keys[3]) { // кол-во строк
            int count = FileRead.countLines(s);
            System.out.println(s + ":");
            System.out.println(count);
        } else if (keys[1] && !keys[2] && !keys[3]) { // кол-во слов
            int count = FileRead.countWords(s);
            System.out.println(s + ":");
            System.out.println(count);
        } else if (keys[1] && keys[2]) { // уникальные слова с учетом регистра
            Map m = new TreeMap();
            m = FileRead.countUniqWordsWithRegistr(s);
            System.out.println(s + ":");
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                System.out.println(pairs.getKey() + " " + pairs.getValue());
            }
            m.clear();
        } else if (keys[1] && keys[3]) {// уникальные слова без учетом регистра
            Map m = new TreeMap();
            m = FileRead.countUniqWordsWithoutRegistr(s);
            System.out.println(s + ":");
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                System.out.println(pairs.getKey() + " " + pairs.getValue());
            }
            m.clear();
        } else {
            throw new Exception("Keys are not compatible!");
        }
    }

    static void printInfo(String [] args, boolean [] keys, int startFile)
            throws Exception {
        // печатаем запрашиваемую информацию для всех файлов
        if (keys[0] && !keys[2] && !keys[3]) { // кол-во строк
            int count = 0;
            for (int i = startFile; i < args.length; ++i) {
                count += FileRead.countLines(args[i]);
            }
            System.out.println(count);
        } else if (keys[1] && !keys[2] && !keys[3]) { // кол-во слов
            int count = 0;
            for (int i = startFile; i < args.length; ++i) {
                count += FileRead.countWords(args[i]);
            }
            System.out.println(count);
        } else if (keys[1] && keys[2]) { // уникальные слова с учетом регистра
            Map m = new TreeMap();
            for (int i = startFile; i < args.length; ++i) {
                m.putAll(FileRead.countUniqWordsWithRegistr(args[i]));
            }
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                System.out.println(pairs.getKey() + " " + pairs.getValue());
            }
            m.clear();
        } else if (keys[1] && keys[3]) {// уникальные слова без учетом регистра
            Map m = new TreeMap();
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
            throw new Exception("Keys are not compatible!");
        }
    }
}
