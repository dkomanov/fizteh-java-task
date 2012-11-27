package ru.fizteh.fivt.students.tolyapro.wordCounter;

import java.io.*;

import java.util.*;

enum ModeType {
    ERR, NONE, WORDS, LINES;
}

enum ModeUniqueness {
    NONSENSITIVE, SENSITIVE, NONE;
}

public class Counter {

    public static boolean checkEmpty(String[] args) {
        return ((args.length < 1) || (args[0].isEmpty()));
    }

    public static boolean modeAgregate = false;

    public static void addWord(Map<String, Integer> dict, String word, Mode mode) {
        Integer amount;
        amount = dict.get(word);
        if (amount != null) {
            dict.put(word, amount + 1);
        } else {
            dict.put(word, 1);
        }

    }

    public static Mode getParam(char c, Mode mode) {
        if (c == 'l') {
            if (mode.type == ModeType.NONE) {
                mode.type = ModeType.LINES;
            } else {
                mode.type = ModeType.ERR;
                return mode;
            }
        } else if (c == 'w') {
            if (mode.type == ModeType.NONE) {
                mode.type = ModeType.WORDS;
            } else {
                mode.type = ModeType.ERR;
                return mode;
            }
        } else if (c == 'U') {
            if (mode.uniqueness == ModeUniqueness.NONE) {
                mode.uniqueness = ModeUniqueness.NONSENSITIVE;
            } else {
                mode.type = ModeType.ERR;
                return mode;
            }
        } else if (c == 'u') {
            if (mode.uniqueness == ModeUniqueness.NONE) {
                mode.uniqueness = ModeUniqueness.SENSITIVE;
            } else {
                mode.type = ModeType.ERR;
                return mode;
            }
        } else if (c == 'a') {
            modeAgregate = true;
        } else {
            mode.type = ModeType.ERR;
        }
        return mode;
    }

    public static void main(String[] args) throws Exception {
        Mode mode = new Mode(ModeType.NONE, ModeUniqueness.NONE);
        if (checkEmpty(args)) {
            System.out.println("Usage: java Counter [keys] FILE1 FILE2 ...");
            System.exit(1);
        }
        int paramNumb = 0;
        for (String s : args) {
            if (!s.isEmpty()) {
                char c = s.charAt(0);
                if ((c == '-') && (s.length() > 1)) {
                    c = s.charAt(1);
                    mode = getParam(c, mode);
                    paramNumb++;
                    int i;
                    for (i = 2; i < s.length(); ++i) {
                        c = s.charAt(i);
                        mode = getParam(c, mode);
                        paramNumb++;
                    }
                    if (i > 2) {
                        paramNumb -= i;
                        paramNumb += 2;
                    }
                } else {
                    break;
                }
            }
        }
        if (mode.type == ModeType.ERR) {
            System.out.println("Wrong parametres combination");
            System.exit(1);
        }
        if ((paramNumb == 0) || (mode.type == ModeType.NONE)) {
            mode.type = ModeType.WORDS;
        }
        Map<String, Integer> dict;
        if (mode.uniqueness == ModeUniqueness.NONSENSITIVE) {
            dict = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        } else {
            dict = new TreeMap<String, Integer>();
        }
        if (paramNumb >= args.length) {
            System.out.println("Usage: java Counter [keys] FILE1 FILE2 ...");
            System.exit(1);            
        }
        for (int i = paramNumb; i < args.length; ++i) {
            String s = args[i];
            BufferedReader br = null;
            FileReader file = null;
            try {
                file = new FileReader(s);
                br = new BufferedReader(file);
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if (mode.type == ModeType.WORDS) {
                        String[] words = strLine.split("[\\s|\t|:;,.()\\[\\]]");
                        for (String word : words) {
                            addWord(dict, word, mode);
                        }
                    } else {
                        if (mode.type == ModeType.LINES) {
                            addWord(dict, strLine, mode);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } finally {
                BufferCloser.close(br);
                BufferCloser.close(file);
            }
            if (!modeAgregate) {
                if (mode.uniqueness != ModeUniqueness.NONE) {
                    System.out.println(args[i] + ": ");
                    Iterator<String> iterator = dict.keySet().iterator();
                    while (iterator.hasNext()) {
                        String word = iterator.next().toString();
                        String amount = dict.get(word).toString();
                        System.out.println(word + " " + amount);
                    }
                } else {
                    Iterator<String> iterator = dict.keySet().iterator();
                    int sum = 0;
                    while (iterator.hasNext()) {
                        String word = iterator.next().toString();
                        sum += dict.get(word);
                    }
                    System.out.print(args[i] + ": ");
                    System.out.println(sum);
                }
                dict.clear();
            }
        }
        if (modeAgregate) {
            try {
                if (mode.uniqueness != ModeUniqueness.NONE) {
                    Iterator<String> iterator = dict.keySet().iterator();
                    while (iterator.hasNext()) {
                        String word = iterator.next().toString();
                        String amount = dict.get(word).toString();
                        System.out.println(word + " " + amount);
                    }
                } else {
                    Iterator<String> iterator = dict.keySet().iterator();
                    int sum = 0;
                    while (iterator.hasNext()) {
                        String word = iterator.next().toString();
                        sum += dict.get(word);
                    }
                    System.out.println(sum);
                }
                dict.clear();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        System.exit(0);
    }
}