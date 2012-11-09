package ru.fizteh.fivt.students.frolovNikolay.wordCounter;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.util.TreeMap;

/*
 * Класс по имени файла
 * выдает количество
 * либо таблицу
 * слов / строк.
 * Пустая строка = строка.
 */
public class WordCounter {
    static void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (Exception closeError) {
            System.err.println(closeError.getMessage());
        }
    }
    
    public static long countWords(String fileName) throws Exception {
        FileReader fReader = null;
        BufferedReader reader = null;
        long result = 0;
        try {
            fReader = new FileReader(fileName);
            reader = new BufferedReader(fReader);
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                String[] words = temp.split("[\\s\\.\\?\\,\\-\\!\\;\\:\\'\\(\\)\\[\\]]+");
                result += words.length;
                if (words[0].isEmpty()) {
                    --result;
                }
            }
        } finally {
            closeStream(reader);
            closeStream(fReader);
        }
        return result;
    }
    
    public static long countLines(String fileName) throws Exception {
        FileReader fReader = null;
        BufferedReader reader = null;
        long result = 0;
        try {
            fReader = new FileReader(fileName);
            reader = new BufferedReader(fReader);
            while (reader.readLine() != null) {
                ++result;
            }
        } finally {
            closeStream(reader);
            closeStream(fReader);
        }
        return result;
    }

    public static TreeMap<String, Integer> countUniqWords(String fileName, boolean ignoreReg) throws Exception {
        FileReader fReader = null;
        BufferedReader reader = null;
        TreeMap<String, Integer> result = null;
        try {
            if (!ignoreReg) {
                result = new TreeMap<String, Integer>();
            } else {
                result = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
            }
            fReader = new FileReader(fileName);
            reader = new BufferedReader(fReader);
            String temp;
            while ((temp = reader.readLine()) != null) {
                String[] words = temp.split("[\\s\\.\\?\\,\\-\\!\\;\\:\\'\\(\\)\\[\\]]+");
                for (String iter : words) {
                    if (!iter.isEmpty()) {
                        if (result.containsKey(iter)) {
                            result.put(iter, result.get(iter) + 1);
                        } else {
                            result.put(iter, new Integer(1));
                        }
                    }
                }
            }
        } finally {
            closeStream(reader);
            closeStream(fReader);
        }
        return result;
    }

    public static TreeMap<String, Integer> countUniqLines(String fileName, boolean ignoreReg) throws Exception {
        FileReader fReader = null;
        BufferedReader reader = null;
        TreeMap<String, Integer> result = null;
        try {
            if (!ignoreReg) {
                result = new TreeMap<String, Integer>();
            } else {
                result = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
            }
            fReader = new FileReader(fileName);
            reader = new BufferedReader(fReader);
            String temp;
            while ((temp = reader.readLine()) != null) {
                if (result.containsKey(temp)) {
                    result.put(temp, result.get(temp) + 1);
                } else {
                    result.put(temp, new Integer(1));
                }
            }
        } finally {
            closeStream(reader);
            closeStream(fReader);
        }
        return result;
    }
}
