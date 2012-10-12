package ru.fizteh.fivt.students.myhinMihail;

import java.util.*;
import java.io.*;

public class WordsCounter {
    
    public static boolean forEach = true;
    public static boolean countUnique = false;
    public static boolean countUniqueCase = true;
    public static boolean countWords = false;
    public static boolean countLines = false;
    public static boolean defaultParam = true;
    
    public static void putToArray(Vector<Map<String, Integer>> array, String str, int indx, int[] wordsCount) {
        if (!forEach) {
            indx = 0;
        }
        
        str = str.replaceAll("\\s+", "");
        if (!countUniqueCase) {
            str = str.toLowerCase();
        }
        
        if (!str.isEmpty()) {
            if (countUnique) {
                if (array.elementAt(indx).get(str) == null) {
                    array.elementAt(indx).put(str, 1);
                } else {
                    array.elementAt(indx).put(str, array.elementAt(indx).get(str) + 1);
                }
            }
            
            if (countWords) {
            	wordsCount[indx]++;
            }
        }
    }
    
    public static void printUsageAndExit(){
        System.err.println("Error: No arguments.\nUsage: [keys] FILE1" +
                " FILE2 \nKeys:\n-l\t- count lines\n-w\t- count words\n" +
                "-u\t- count unique elements\n-U\t- count unique elements" +
                " (not case sensitive)\n-a\t- aggregate information on all files");
        System.exit(1);
    }
    
    public static int readKeys(String[] args) {
    	int params = 0;
        for (String str : args) {
            if (str.isEmpty()) {
                params++;
                continue;
            }
            if (str.charAt(0) == '-') {
                params++;
                for (int i = 1; i < str.length(); ++i) {
                    switch (str.charAt(i)) {
                        case 'l': 
                            if (countWords) {
                                System.err.println("Error: do not use flags -l and -w at the same time");
                                System.exit(1);
                            }
                            countLines = true;
                            defaultParam = false;
                            break;
                        
                        case 'w':
                            if (countLines) {
                                System.err.println("Error: do not use flags -l and -w at the same time");
                                System.exit(1);
                            }
                            countWords = true;
                            defaultParam = false;
                            break;
                        
                        case 'u':
                            countUnique = true;
                            defaultParam = false;
                            if (!countUniqueCase) {
                                System.err.println("Error: do not use flags -U and -u at the same time");
                                System.exit(1);
                            }
                            break;
                        
                        case 'U':
                            if (countUnique) {
                                System.err.println("Error: do not use flags -U and -u at the same time");
                                System.exit(1);
                            }
                            countUnique = true;
                            countUniqueCase = false;
                            defaultParam = false;
                            break;
                        
                        case 'a':
                            forEach = false;
                            break;
                        
                        default:
                            System.out.println("Unknown parametr: \'" + str.charAt(i) + "\'");
                            break;
                    }
                }
            }
        }
        
        if (params == args.length) {
            printUsageAndExit();
        }
        
        if (defaultParam) {
            countWords = true;
        }
        
        return params;
    	
    }

    public static void main(String[] args) throws Exception {
        try {
            if (args.length == 0) {  
                printUsageAndExit();
            }
            
            int keysCount = readKeys(args);
            int realArgs = args.length - keysCount;
            
            int lineNumber[] = null;
            int wordsNumber[] = null;
            
            if (countLines) {
            	lineNumber = new int[realArgs];
            }
            
            if (countWords) {
            	wordsNumber = new int[realArgs];
            }
            
            Vector<Map<String, Integer>> array = new Vector<Map<String, Integer>>();
            for (int i = 0; i < realArgs; ++i) {
                if (countUnique) {
                    Map<String, Integer> temp = new LinkedHashMap<String, Integer>();
                    array.add(temp);
                }
                
                if (countWords) {
                	wordsNumber[i] = 0;
                }
                
                if (countLines) {
                	lineNumber[i] = 0;
                }
                
                if (!forEach) {
                    break;
                }
            }
            
            int indx = 0;
            int currentParam = 0;
            
            for (int i = 0; i < args.length; ++i) {
                if (args[i].isEmpty() || args[i].charAt(0) == '-') {
                    continue;
                }
                String path = args[i];
                
                if (forEach) {
                    indx = currentParam;
                }
                
                File file = new File(path);
                FileReader fr = null;
                BufferedReader reader = null;
                
                try {
                    fr = new FileReader(file);
                    reader = new BufferedReader(fr);
                    String line;
                
                    while ((line = reader.readLine()) != null) {
                        if (countLines) { 
                            lineNumber[indx]++; 
                        }
                        String words[] = line.split("[\\s\\.:;,\\\"\\\'\\(\\)!]+");
                        for (String s : words) {
                            putToArray(array, s, indx, wordsNumber);
                        }
                    } 
                } finally {
                    try {
                        reader.close();
                        fr.close();
                    } catch (Exception expt) { 
                    }
                }
                currentParam++;
            }
            
            int badName = 0 ;
            if (!forEach) {
                for (int i = 0; i < args.length; ++i) {
                    if (args[i].isEmpty() || args[i].charAt(0) == '-') {
                        badName++;
                        continue;
                    }
                    if ((i != 0) && (badName != i)) {
                        System.out.print(", ");
                        badName--;
                    }
                    System.out.print(args[i]);
                }
                System.out.println(":");
            }
            
            currentParam = 0;
            for (int i = 0; i < args.length; ++i) {
                if (args[i].isEmpty() || args[i].charAt(0) == '-') {
                    continue;
                }
                
                if (forEach) {
                    System.out.println(args[i] + ":");
                    indx = currentParam;
                }
                
                if (countUnique) {
                    for (Map.Entry<String, Integer> entry : array.elementAt(indx).entrySet()) {
                        System.out.println(entry.getKey() + " " + entry.getValue());
                    }
                }
                
                if (countLines) {
                    System.out.println(lineNumber[indx]);
                }
                if (countWords) {
                    System.out.println(wordsNumber[indx]);
                }
                System.out.println();
                
                if (!forEach) {
                    break;
                }
                currentParam++;
            }
            
        } catch (Exception excpt) {
            System.err.println("Error: " + excpt.getMessage());
            System.exit(1);
        }
    }
    
}
