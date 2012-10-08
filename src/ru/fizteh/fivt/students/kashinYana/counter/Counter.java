package ru.fizteh.fivt.students.kashinYana.counter;

import java.io.FileInputStream;
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
        for(int i = 0; i < args.length; i++) {
            maxFlags += args[i].length();
        }
        flags = new Character[maxFlags];
        flagsSize = 0;

        fullArray(args);

        try {
            setFlags();
        } catch (Exception e) {
            System.out.print(e.getMessage());
            System.exit(1);
        }
        if (filesSize == 0) {
            System.out.println("-l - countLines\n-w - countWords \n-u - count dif words -U\n" +
                    "-u - count dif words in lower case\n-a - don't write name file");
            System.exit(0);
        }
        try {
            if (isw) {
                countWords(files, filesSize);
            }  else if (isl) {
                countLines(files, filesSize);
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
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
        if (!(isl || isw )) {
            isw = true;     // w - активен по умолчанию
        }
        if(!((isw || isl) && (isw != isl) && !(isu && isU))) {
            throw new Exception("Don't use this set keys.");
        }
    }

    private static boolean isCorrect(char flag) {
        return (flag == 'a') || (flag == 'l') || (flag == 'U') || (flag == 'u') || (flag == 'w');
    }

    private static void countWords(String[] files, int filesSize) throws Exception {
        for (int i = 0; i < filesSize; i++) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            FileInputStream inFile = new FileInputStream(files[i]);
            Scanner in = new Scanner(inFile);
            while (in.hasNext()) {
                String currentWord = in.next();
                if (isU) {
                    currentWord = currentWord.toLowerCase();
                }
                if (map.containsKey(currentWord)) {
                    map.put(currentWord, map.get(currentWord) + 1);
                } else {
                    map.put(currentWord, 1);
                }
            }
            if (!isA) {
                System.out.println(files[i] + ":");
            }
            if (isu || isU) {
                for (Object it : map.keySet()) {
                    System.out.println(it + " " + map.get(it));
                }
            } else {
                System.out.println(map.size());
            }
            inFile.close();
            in.close();
        }
    }

    private static void countLines(String[] files, int filesSize) throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < filesSize; i++) {
            FileInputStream inFile = new FileInputStream(files[i]);
            Scanner in = new Scanner(inFile);
            while (in.hasNext()) {
                String currentWord = in.nextLine();
                if (isU) {
                    currentWord = currentWord.toLowerCase();
                }
                if (map.containsKey(currentWord)) {
                    map.put(currentWord, map.get(currentWord) + 1);
                } else {
                    map.put(currentWord, 1);
                }
            }
            if (!isA) {
                System.out.println(files[i] + ":");
            }
            if(!isu && !isU) {
                System.out.println(map.size());
            } else {
                for (Object it : map.keySet()) {
                    System.out.println(it + " " + map.get(it));
                }
            }
            in.close();
            inFile.close();
        }
    }
}
