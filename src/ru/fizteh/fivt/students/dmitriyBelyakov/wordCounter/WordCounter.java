package ru.fizteh.fivt.students.dmitriyBelyakov.wordCounter;

import java.math.BigInteger;
import java.util.*;
import java.io.*;

enum Mode {NONE, MINUS_L, MINUS_W}

enum ModeUnique {NONE, MINUS_U, MINUS_BIG_U}

public class WordCounter {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Use: java WordCounter [keys] FILE1 FILE2 ...");
            System.exit(1);
        }
        Mode mode = Mode.NONE;
        ModeUnique unique = ModeUnique.NONE;
        boolean minusA = false;
        ArrayList<String> fileNames = new ArrayList<String>();
        for (String s : args) {
            if (s.charAt(0) == '-') {
                for (int i = 1; i < s.length(); ++i) {
                    char c = s.charAt(i);
                    if (c == 'w') {
                        if (mode != Mode.NONE) {
                            System.err.println("Error: conflict of flags.");
                            System.exit(1);
                        }
                        mode = Mode.MINUS_W;
                    } else if (c == 'l') {
                        if (mode != Mode.NONE) {
                            System.err.println("Error: conflict of flags.");
                            System.exit(1);
                        }
                        mode = Mode.MINUS_L;
                    } else if (c == 'u') {
                        unique = ModeUnique.MINUS_U;
                    } else if (c == 'U') {
                        unique = ModeUnique.MINUS_BIG_U;
                    } else if (c == 'a') {
                        minusA = true;
                    }
                }
            } else {
                fileNames.add(s);
            }
        }
        if (mode == Mode.NONE) {
            mode = Mode.MINUS_W;
        }
        ArrayList<String> errorList = new ArrayList<String>();
        try {
            String out = makeCalculations(fileNames, mode, unique, minusA, errorList);
            for (String error : errorList) {
                System.err.println("Error: " + error);
            }
            System.out.print(out);
            if (!errorList.isEmpty()) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    static String makeOutInformation(String fileName, BigInteger i) {
        String newLine = System.getProperty("line.separator");
        StringBuilder builder = new StringBuilder();
        builder.append(fileName);
        builder.append(":");
        builder.append(newLine);
        builder.append(i);
        builder.append(newLine);
        return builder.toString();
    }

    static String makeOutInformation(String fileName, HashMap<String, BigInteger> map) {
        String newLine = System.getProperty("line.separator");
        StringBuilder builder = new StringBuilder();
        builder.append(fileName);
        builder.append(":");
        builder.append(newLine);
        for (String key : map.keySet()) {
            builder.append(key);
            builder.append(" ");
            builder.append(map.get(key));
            builder.append(newLine);
        }
        return builder.toString();
    }

    static String makeOutInformation(BigInteger i) {
        String newLine = System.getProperty("line.separator");
        return i.toString() + newLine;
    }

    static String makeOutInformation(HashMap<String, BigInteger> map) {
        String newLine = System.getProperty("line.separator");
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            builder.append(key);
            builder.append(" ");
            builder.append(map.get(key));
            builder.append(newLine);
        }
        return builder.toString();
    }

    static String makeCalculations(ArrayList<String> fileNames, Mode mode, ModeUnique unique, boolean minusA, ArrayList<String> errorList) throws Exception {
        BigInteger count = BigInteger.ZERO;
        HashMap<String, BigInteger> map = new HashMap<String, BigInteger>();
        StringBuilder builder = new StringBuilder();
        for (String fileName : fileNames) {
            FileReader reader = null;
            BufferedReader bufReader = null;
            try {
                reader = new FileReader(fileName);
                bufReader = new BufferedReader(reader);
                String str;
                while ((str = bufReader.readLine()) != null) {
                    if (mode == Mode.MINUS_W) {
                        String[] words = str.split("\\s+");
                        if (unique == ModeUnique.NONE) {
                            BigInteger tmp = new BigInteger(Integer.toString(words.length));
                            count = count.add(tmp);
                        } else if (unique == ModeUnique.MINUS_U) {
                            for (String word : words) {
                                if (map.containsKey(word)) {
                                    map.put(word, map.get(word).add(BigInteger.ONE));
                                } else {
                                    map.put(word, BigInteger.ONE);
                                }
                            }
                        } else {
                            for (String word : words) {
                                String lWord = word.toLowerCase();
                                if (map.containsKey(lWord)) {
                                    map.put(lWord, map.get(lWord).add(BigInteger.ONE));
                                } else {
                                    map.put(lWord, BigInteger.ONE);
                                }
                            }
                        }
                    } else {
                        if (unique == ModeUnique.NONE) {
                            count = count.add(BigInteger.ONE);
                        } else if (unique == ModeUnique.MINUS_U) {
                            if (map.containsKey(str)) {
                                map.put(str, map.get(str).add(BigInteger.ONE));
                            } else {
                                map.put(str, BigInteger.ONE);
                            }
                        } else {
                            String lStr = str.toLowerCase();
                            if (map.containsKey(lStr)) {
                                map.put(lStr, map.get(lStr).add(BigInteger.ONE));
                            } else {
                                map.put(lStr, BigInteger.ONE);
                            }
                        }
                    }
                }
                if (!minusA) {
                    if (unique == ModeUnique.NONE) {
                        builder.append(makeOutInformation(fileName, count));
                    } else {
                        builder.append(makeOutInformation(fileName, map));
                    }
                    map.clear();
                    count = BigInteger.ZERO;
                }
            } catch (Exception e) {
                if (minusA) {
                    throw e;
                }
                errorList.add(e.getMessage());
            } finally {
                if (reader != null) {
                    reader.close();
                }
                if(bufReader != null) {
                    bufReader.close();
                }
            }
        }
        if (minusA) {
            if (unique == ModeUnique.NONE) {
                builder.append(makeOutInformation(count));
            } else {
                builder.append(makeOutInformation(map));
            }
        }
        return builder.toString();
    }
}
