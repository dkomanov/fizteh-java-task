package ru.fizteh.fivt.students.frolovNikolay.wordCounter;

import java.io.BufferedReader;
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
    public static long countWords(String fileName) throws Exception {
        long result = 0;
        FileReader fReader = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fReader);
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            String[] words = temp.split("[^a-zA-z]+");
            result += words.length;
            if (words[0].isEmpty()) {
                --result;
            }
        }
        reader.close();
        fReader.close();
        return result;
    }
    
    public static long countLines(String fileName) throws Exception {
        long result = 0;
        FileReader fReader = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fReader);
        while (reader.readLine() != null) {
            ++result;
        }
        reader.close();
        fReader.close();
        return result;
    }

    public static TreeMap<String, Integer> countUniqWords(String fileName, boolean ignoreReg) throws Exception {
        TreeMap<String, Integer> result = null;
        if (!ignoreReg) {
            result = new TreeMap<String, Integer>();
        } else {
            result = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        }
        FileReader fReader = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fReader);
        String temp;
        while ((temp = reader.readLine()) != null) {
            String[] words = temp.split("[^a-zA-z]+");
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
        reader.close();
        fReader.close();
        return result;
    }

    public static TreeMap<String, Integer> countUniqLines(String fileName, boolean ignoreReg) throws Exception {
        TreeMap<String, Integer> result = null;
        if (!ignoreReg) {
            result = new TreeMap<String, Integer>();
        } else {
            result = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        }
        FileReader fReader = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(fReader);
        String temp;
        while ((temp = reader.readLine()) != null) {
            if (result.containsKey(temp)) {
                result.put(temp, result.get(temp) + 1);
            } else {
                result.put(temp, new Integer(1));
            }
        }
        reader.close();
        fReader.close();
        return result;
    }
}
