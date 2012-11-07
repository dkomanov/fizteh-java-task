package ru.fizteh.fivt.students.mysinYurii.wordCounter;

/*
 * Author: Mysin Yurii
 * 
 * Group: 196
 * 
 */

import java.util.*;
import java.io.*;

public class FileWorker {
    int wordCount;
    
    int lineCount;
    
    Map<String, Integer> wordsMap;
    
    FileWorker() {
        wordCount = 0;
        lineCount = 0;
        wordsMap = new TreeMap<String, Integer>();
    }
    
    public void printUniqueWords() {
        for (String s : wordsMap.keySet()) {
            System.out.print(s + ' ' + wordsMap.get(s) + '\n');
        }
    }
    
    public int toCountWords(String fileName) {
        int resultCount = 0;
        FileInputStream fileStream;
        try {
             fileStream = new FileInputStream(fileName);
        } catch(FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            return -1;
        }
        byte[] tempData = null;
        try {
            tempData = new byte[fileStream.available()];
        } catch (IOException e1) {
            System.out.println(e1.getMessage());
            try {
                fileStream.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return -1;
        }
        try {
            fileStream.read(tempData);
        } catch (IOException e1) {
            System.out.println(e1.getMessage());
        }
        for (int i = 0; i < tempData.length; ++i) {
            if (Character.isLetter(tempData[i])) {
                while ((i < tempData.length) 
                        && (Character.isLetter(tempData[i]))) {
                    ++i;
                }
                ++resultCount;
            }
        }
        
        try {
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        wordCount += resultCount;
        return resultCount;
    }
    
    public int toCountUniqueWords(String fileName, boolean caseSensivity) {
        try {
            FileInputStream fileStream = new FileInputStream(fileName); 
            byte[] tempData = new byte[fileStream.available()];
            fileStream.read(tempData);
            String inputData = new String(tempData);
            for (int i = 0; i < tempData.length; ++i) {
                if (Character.isLetter(tempData[i])) {
                    int begin = i;
                    while ((i < tempData.length) 
                            && (Character.isLetter(tempData[i]))) {
                        ++i;
                    }
                    String newWord = inputData.substring(begin,i);
                    if (!caseSensivity) newWord = newWord.toLowerCase();
                    if (wordsMap.containsKey(newWord)) {
                        wordsMap.put(newWord, new Integer(wordsMap.get(newWord).intValue() + 1));
                    } else {
                        wordsMap.put(newWord, new Integer(1));
                    }
                }
            }
            fileStream.close();
            return 0;
        } catch(FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            return -1;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
    
    public int toCountLines(String fileName) {
        try{
            int resultCount = 0;
            FileInputStream fileStream = new FileInputStream(fileName);
            byte[] tempData = new byte[fileStream.available()];
            fileStream.read(tempData);
            for (int i = 0; i < tempData.length; ++i) {
                if (tempData[i] == '\n')++resultCount;
            }
            fileStream.close();
            if ((resultCount == 0) && (tempData.length > 0) ) {
                ++lineCount;
                return 1;
            } else {
                lineCount += resultCount;
                return resultCount;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            return -1;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
}
