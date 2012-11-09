
package ru.fizteh.fivt.students.mesherinIlya.wordCounter;

import java.util.*;
import java.io.*;

public class WordCounter {
    
    static boolean linesMode;
    static boolean wordsMode;
    static boolean uniqueCaseSensitive;
    static boolean uniqueCaseNonSensitive;
    static boolean agregate;
        
    static boolean delim(int c) {
        if (linesMode) {
            if (c == '\n' || c == '\r') {
                return true;
            }
            return false;
        }
        if (Character.isWhitespace(c) || ".,;\"()[]{}!?:/\\|<>".indexOf(c) != -1) {
            return true;
        }
        return false;
    
    }

    public static void main(String[] args) throws Exception {
	
        if (args.length == 0) {
            System.err.println("Using: WordCounter [keys] FILE1 FILE2 ...");
            System.exit(1);
        }
        
        int param;
        for (param = 0; param < args.length && args[param].charAt(0) == '-'; param++) {
            if (args[param].length() == 1) {
                System.err.println("Error: what did you mean \'-\'?");
                System.exit(1);
            }
            for (int i = 1; i < args[param].length(); i++) {
                switch (args[param].charAt(i)) {
                case 'u':
                    uniqueCaseSensitive = true;
                    break;
                case 'U':
                    uniqueCaseNonSensitive = true;
                    break;
                case 'w':
                    wordsMode = true;
                    break;
                case 'l':
                    linesMode = true;
                    break;
                case 'a':
                    agregate = true;
                    break;  
                default:
                    System.err.print("Error: unknown parameter \'");
                    System.err.print(args[param].charAt(i));
                    System.err.println('\'');
                    System.exit(1);                
                    break;
                }
            }
        }
        
        if (!wordsMode && !linesMode) {
            wordsMode = true;
        }
        if (uniqueCaseSensitive && uniqueCaseNonSensitive) {
            System.err.println("Error: incompatible parameters u and U");
            System.exit(1);
        }
        if (linesMode && wordsMode) {
            System.err.println("Error: incompatible parameters l and w");
            System.exit(1);
        }
        
        int wordCount = 0;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        //рассмотрим все файлы по очереди
        if (param >= args.length) {
            System.err.println("Error: you should specify the file");
            System.exit(1);
        }
        for (; param < args.length; param++) {
            //откроем очередной файл
            FileReader f = null;
            try {
                f = new FileReader(args[param]);
                
                //если не надо агрегировать по всем файлам, то сбросим счетчик    
                if (!agregate) {
                    wordCount = 0;
                    System.out.println(args[param] + ':');
                    map.clear();
                }
                
                //будем анализировать символы, пока не закончится файл
                int c;
                StringBuilder builder = new StringBuilder();
                String word = new String();
                boolean whiteSpace = true;
                
                while ((c = f.read()) != -1) {
                    if (!delim(c)) {
                        if (whiteSpace) {
                            whiteSpace = false;
                            builder = new StringBuilder();
                        }
                        builder.append((char) c);
                    }
                    else {
                        if (!whiteSpace) {
                            whiteSpace = true;
                            wordCount++;
                            word = builder.toString();
                            if (uniqueCaseNonSensitive) {
                                word = word.toLowerCase();
                            }
                            if (uniqueCaseSensitive || uniqueCaseNonSensitive) {
                                Integer count = map.get(word);
                                map.put(word,
                                        (count == null) ? 1 : ++count);
                            }
                        }
                    }
                
                }
            }
            catch (IOException e) {
                System.err.println("Error: can't open the file \"" + args[param] + '\"');
                System.exit(1);
            }
            finally {
                f.close();
            }
            
            if (!agregate) {
                if (uniqueCaseSensitive || uniqueCaseNonSensitive) {
                    Set<Map.Entry<String, Integer>> set = map.entrySet();
                    for (Map.Entry<String, Integer> entry : set) {
                        System.out.print(entry.getKey() + ' ');
                        System.out.println(entry.getValue());
                    }    
                }
                else {
                    System.out.println(wordCount);
                }
            }
        }
        if (agregate) {
            if (uniqueCaseSensitive || uniqueCaseNonSensitive) {
                Set<Map.Entry<String, Integer>> set = map.entrySet();
                for (Map.Entry<String, Integer> entry : set) {
                    System.out.print(entry.getKey() + ' ');
                    System.out.println(entry.getValue());
                }    
            }
            else {
                System.out.println(wordCount);
            }
        }
    }
}
