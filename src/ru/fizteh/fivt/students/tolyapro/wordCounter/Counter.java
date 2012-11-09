package ru.fizteh.fivt.students.tolyapro.wordCounter;

import java.io.*;

import java.util.*;

enum Mode {
    ERR, NONE, LINES_CASE_NONSENSITIVE, LINES_CASE_SENSITIVE, WORDS_CASE_NONSENSITIVE, WORDS_CASE_SENSITIVE, LINES, WORDS
}

public class Counter {

    public static boolean modeWords(Mode mode) {
        return ((mode == Mode.WORDS) || (mode == Mode.WORDS_CASE_SENSITIVE) || (mode == Mode.WORDS_CASE_NONSENSITIVE));
    }

    public static boolean modeLines(Mode mode) {
        return ((mode == Mode.LINES_CASE_NONSENSITIVE)
                || (mode == Mode.LINES_CASE_SENSITIVE) || (mode == Mode.LINES));
    }

    public static boolean modeUniqueWithReg(Mode mode) {
        return ((mode == Mode.WORDS_CASE_SENSITIVE) || (mode == Mode.LINES_CASE_SENSITIVE));
    }

    public static boolean modeUnique(Mode mode) {
        return ((mode == Mode.WORDS_CASE_NONSENSITIVE) || (mode == Mode.LINES_CASE_NONSENSITIVE));
    }

    public static boolean checkEmpty(String[] args) {
        return ((args.length == 1) && (args[0].isEmpty()));
    }

    public static boolean modeAgregate = false;

    public static void addWord(Map<String, Integer> dict, String word, Mode mode) {
        if (modeUniqueWithReg(mode)) {
            Integer amount;
            amount = dict.get(word);
            if (amount != null) {
                dict.put(word, amount + 1);
            } else {
                dict.put(word, 1);
            }
        } else {
            Integer amount;
            amount = dict.get(word);
            if (dict.get(word) != null) {
                dict.put(word, amount + 1);
            } else {
                dict.put(word, 1);
            }
        }

    }

    public static Mode getParam(char c, Mode mode) {
        if (c == 'l') {
            if (mode == Mode.NONE) {
                mode = Mode.LINES;
            } else {
                return Mode.ERR;
            }

        } else if (c == 'w') {
            if (mode == Mode.NONE) {
                mode = Mode.WORDS;
            } else {
                return Mode.ERR;
            }
        } else if (c == 'u') {
            if (mode == Mode.LINES) {
                mode = Mode.LINES_CASE_NONSENSITIVE;
            } else if (mode == Mode.WORDS) {
                mode = Mode.WORDS_CASE_NONSENSITIVE;
            } else {
                return Mode.ERR;
            }
        } else if (c == 'U') {
            if (mode == Mode.LINES) {
                mode = Mode.LINES_CASE_SENSITIVE;
            } else if (mode == Mode.WORDS) {
                mode = Mode.WORDS_CASE_SENSITIVE;
            } else {
                return Mode.ERR;
            }
        } else if (c == 'a') {
            modeAgregate = true;
        } else {
            mode = Mode.ERR;
        }
        return mode;
    }

    public static void main(String[] args) throws Exception {
        Mode mode = Mode.NONE;
        if (checkEmpty(args)) {
            System.out.println("Usage: java Counter [keys] FILE1 FILE2 ...");
            System.exit(1);
        }
        int paramNumb = 0;
        for (String s : args) {
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
                    paramNumb--;
                }
            } else {
                break;
            }
        }
        if (mode == Mode.ERR) {
            System.out.println("Wrong parametres combination");
            System.exit(1);
        }
        if ((paramNumb == 0) || (mode == Mode.NONE)) {
            mode = Mode.WORDS;
        }
        Map<String, Integer> dict;
        if (modeUnique(mode) && !modeUniqueWithReg(mode)) {
            dict = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        } else {
            dict = new TreeMap<String, Integer>();
        }
        for (int i = paramNumb; i < args.length; ++i) {
            String s = args[i];
            DataInputStream in = null;
            BufferedReader br = null;
            try {
                in = new DataInputStream(new FileInputStream(s));
                br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if (modeWords(mode)) {
                        String[] words = strLine.split("[\\s|\t|:;,.()\\[\\]]");
                        for (String word : words) {
                            addWord(dict, word, mode);
                        }
                    } else {
                        if (modeLines(mode)) {
                            addWord(dict, strLine, mode);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                BufferCloser.close(in);
                BufferCloser.close(br);
                System.exit(1);
            } finally {
                BufferCloser.close(in);
                BufferCloser.close(br);
            }
            if (!modeAgregate) {
                if (modeUnique(mode) || modeUniqueWithReg(mode)) {
                    System.out.println("File " + args[i] + " :");
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
                    System.out.print("File " + args[i] + " ");
                    System.out.println(sum);
                }
                dict.clear();
            }
        }
        if (modeAgregate) {
            if (modeUnique(mode)) {
                System.out.print(dict.size());
                System.exit(0);
            }
            try {
                Iterator<String> iterator = dict.keySet().iterator();
                int sum = 0;
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    String value = dict.get(key).toString();
                    sum += new Integer(value);
                }
                System.out.println(sum);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        System.exit(0);
    }
}