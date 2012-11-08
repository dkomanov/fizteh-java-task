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
                    if (isSuitable(temp.charAt(i))) {
                        while ((i < temp.length()) 
                                && isSuitable(temp.charAt(i))) {
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
            System.err.println(e.getMessage());
            try {
                reader.close();
            } catch (IOException e1) {
                System.err.println(e1.getMessage());
            }
            return -1;
        }
    }
    
    private boolean isSuitable(char charAt) {
        return ((Character.isAlphabetic(charAt)) || (Character.isDigit(charAt)));
    }

    public int toCountUniqueWords(String fileName, boolean caseSensivity) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName)); 
            String tempData = null;
            tempData = reader.readLine();
            while (tempData != null) {
                for (int i = 0; i < tempData.length(); ++i) {
                    if (isSuitable(tempData.charAt(i))) {
                        int begin = i;
                        while ((i < tempData.length()) 
                                && isSuitable(tempData.charAt(i))) {
                            ++i;
                        }
                        String newWord = tempData.substring(begin,i);
                        if (wordsMap.containsKey(newWord)) {
                            wordsMap.put(newWord, wordsMap.get(newWord) + 1);
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
            System.err.println(e.getMessage());
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                System.err.println(e1.getMessage());
            }
            return -1;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                System.err.println(e1.getMessage());
            }
            return -1;
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
            System.err.println(e.getMessage());
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                System.err.println(e1.getMessage());
            }
            return -1;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                System.err.println(e1.getMessage());
            }
            return -1;
        }
    }
}
