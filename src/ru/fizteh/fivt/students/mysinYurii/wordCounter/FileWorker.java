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
    
    FileWorker(boolean caseSens) {
        wordCount = 0;
        lineCount = 0;
        if (caseSens) {
            wordsMap = new TreeMap<String, Integer>();
        } else {
            wordsMap = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        }
    }
    
    public void printUniqueWords() {
        for (String s : wordsMap.keySet()) {
            System.out.print(s + ' ' + wordsMap.get(s) + '\n');
        }
    }
    
    public int toCountWords(String fileName) {
        BufferedReader reader = null;
        try {
            int resultCount = 0;
            reader = new BufferedReader(new FileReader(fileName));
            String temp = reader.readLine();
            while (temp != null) {
                for (int i = 0; i < temp.length(); ++i) {
                    if (Character.isLetter(temp.charAt(i))) {
                        while ((i < temp.length()) 
                                && (Character.isLetter(temp.charAt(i)))) {
                            ++i;
                        }
                        ++resultCount;
                    }
                }
                temp = reader.readLine();
            }
            wordCount += resultCount;
            reader.close();
            return resultCount;
        }  catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.print(e.getMessage());
                }
        }
    }
    
    public int toCountUniqueWords(String fileName, boolean caseSensivity) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName)); 
            String tempData = null;
            tempData = reader.readLine();
            while (tempData != null) {
                for (int i = 0; i < tempData.length(); ++i) {
                    if (Character.isLetter(tempData.charAt(i))) {
                        int begin = i;
                        while ((i < tempData.length()) 
                                && (Character.isLetter(tempData.charAt(i)))) {
                            ++i;
                        }
                        String newWord = tempData.substring(begin,i);
                        if (wordsMap.containsKey(newWord)) {
                            wordsMap.put(newWord, new Integer(wordsMap.get(newWord).intValue() + 1));
                        } else {
                            wordsMap.put(newWord, new Integer(1));
                        }
                    }
                }
                tempData = reader.readLine();
            }
            reader.close();
            return 0;
        } catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
            return -1;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.print(e.getMessage());
                }
        }
    }
    
    public int toCountLines(String fileName) {
        BufferedReader reader = null;
        try {
            int resultCount = 0;
            reader = new BufferedReader(new FileReader(fileName));
            String tempData = new String();
            tempData = reader.readLine();
            while (tempData != null) {
                ++resultCount;
                tempData = reader.readLine();
            }
            lineCount += resultCount;
            reader.close();
            return resultCount;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return -1;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.print(e.getMessage());
                }
        }
    }
}
