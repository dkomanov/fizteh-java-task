package ru.fizteh.fivt.students.kashinYana.counter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: yana
 * Date: 01.10.12
 * Time: 0:03
 * To change this template use File | Settings | File Templates.
 */

public class Counter {

    static boolean isA = false;
    static boolean isU = false;
    static boolean isu = false;
    static boolean isl = false;
    static boolean isw = false;

    static String[] files;
    static Integer filesSize;
    static Character[] flags;
    static Integer flagsSize;

    public static void main(String[] args) throws Exception {

        files = new String[args.length];
        filesSize = 0;
        int maxFlags = 0;
        for (int i = 0; i < args.length; i++) {
            maxFlags += args[i].length();
        }
        flags = new Character[maxFlags];
        flagsSize = 0;

        fullArray(args);

        try {
            setFlags();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        if (filesSize == 0) {
            System.out.println("-l - countLines\n-w - countWords \n-u - count dif words -U\n" +
                    "-u - count dif words in lower case\n-a - don't write name file");
            System.exit(1);
        }
        try {
            if (isw) {
                countWords(files, filesSize);
            } else if (isl) {
                countLines(files, filesSize);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void fullArray(String[] args) throws Exception {
        try {
            int stop = args.length;
            for (int i = 0; i < args.length; i++) {
                if (args[i].charAt(0) != '-') {
                    stop = i;
                    break;
                } else {
                    for (int j = 1; j < args[i].length(); j++) {
                        flags[flagsSize++] = args[i].charAt(j);
                    }
                }
            }
            for (int i = stop; i < args.length; i++) {
                files[filesSize++] = args[i];
            }
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("Array full. Write letter to coder.");
        }
    }

    private static void setFlags() throws Exception {

        for (int i = 0; i < flagsSize; i++) {
            if (!isCorrect(flags[i])) {
                throw new Exception("Don't use keys " + flags[i]);
            }
        }

        for (int i = 0; i < flagsSize; i++) {
            if (flags[i] == 'w') {
                if (isw) {
                    throw new Exception("Don't use > 1 '-w'.");
                }
                isw = true;
            } else if (flags[i] == 'l') {
                if (isl) {
                    throw new Exception("Don't use > 1 '-l'.");
                }
                isl = true;
            } else if (flags[i] == 'a') {
                if (isA) {
                    throw new Exception("Don't use > 1 '-a'.");
                }
                isA = true;
            } else if (flags[i] == 'u') {
                if (isu) {
                    throw new Exception("Don't use > 1 '-u'.");
                }
                isu = true;
            } else if (flags[i] == 'U') {
                if (isU) {
                    throw new Exception("Don't use > 1 '-U'.");
                }
                isU = true;
            }
        }
        if (!(isl || isw)) {
            isw = true;     // w - активен по умолчанию
        }
        if (!((isw || isl) && (isw != isl) && !(isu && isU))) {
            throw new Exception("Don't use this set keys.");
        }
    }

    private static boolean isCorrect(char flag) {
        return (flag == 'a') || (flag == 'l') || (flag == 'U') || (flag == 'u') || (flag == 'w');
    }

    private static void countWords(String[] files, int filesSize) throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        int totalNumber = 0;
        for (int i = 0; i < filesSize; i++) {
            if (!isA) {
                map.clear();
                totalNumber = 0;
            }
            BufferedReader in = null;
            FileReader file = null;
            try {
                file = new FileReader(files[i]);
                in = new BufferedReader(file);
                while (in.ready()) {
                    String currentLine = in.readLine();
                    String[] words = currentLine.split("[ \\t\\n.!?,:;]+");
                    for (int j = 0; j < words.length; j++) {
                        String currentWord = words[j];
                        if (isU) {
                            currentWord = currentWord.toLowerCase();
                        }
                        Integer count = map.get(currentWord);
                        if (count == null) {
                            count = 0;
                        }
                        map.put(currentWord, count + 1);
                        totalNumber++;
                    }
                }
                if (!isA) {
                    System.out.println(files[i] + ":");
                    if (isu || isU) {
                        for (Object it : map.keySet()) {
                            System.out.println(it + " " + map.get(it));
                        }
                    } else {
                        System.out.println(totalNumber);
                    }
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (file != null) {
                    file.close();
                }
            }
        }
        if (isA) {
            if (isu || isU) {
                for (Object it : map.keySet()) {
                    System.out.println(it + " " + map.get(it));
                }
            } else {
                System.out.println(totalNumber);
            }
        }
    }

    private static void countLines(String[] files, int filesSize) throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        int totalNumber = 0;
        for (int i = 0; i < filesSize; i++) {
            if (!isA) {
                totalNumber = 0;
                map.clear();
            }
            BufferedReader in = null;
            FileReader file = null;
            try {
                file = new FileReader(files[i]);
                in = new BufferedReader(file);
                while (in.ready()) {
                    String currentWord = in.readLine();
                    if (isU) {
                        currentWord = currentWord.toLowerCase();
                    }
                    Integer count = map.get(currentWord);
                    if (count == null) {
                        count = 0;
                    }
                    map.put(currentWord, count + 1);
                    totalNumber++;
                }
                if (!isA) {
                    System.out.println(files[i] + ":");
                    if (!isu && !isU) {
                        System.out.println(totalNumber);
                    } else {
                        for (Object it : map.keySet()) {
                            System.out.println(it + " " + map.get(it));
                        }
                    }
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (file != null) {
                    file.close();
                }
            }
        }
        if (isA) {
            if (!isu && !isU) {
                System.out.println(totalNumber);
            } else {
                for (Object it : map.keySet()) {
                    System.out.println(it + " " + map.get(it));
                }
            }
        }
    }
}
