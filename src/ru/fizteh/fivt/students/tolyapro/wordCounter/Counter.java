package ru.fizteh.fivt.students.tolyapro.wordCounter;

import java.io.*;

import java.util.*;

enum Mode {
    Err, None, Lu, LU, Wu, WU, L, W
}

public class Counter {

    public static boolean modeWords(Mode mode) {
        return ((mode == Mode.Wu) || (mode == Mode.WU) || (mode == Mode.W));
    }

    public static boolean modeLines(Mode mode) {
        return ((mode == Mode.Lu) || (mode == Mode.LU) || (mode == Mode.L));
    }

    public static boolean modeUniqueWithReg(Mode mode) {
        return ((mode == Mode.WU) || (mode == Mode.LU));
    }

    public static boolean modeUnique(Mode mode) {
        return ((mode == Mode.Wu) || (mode == Mode.Lu));
    }

    public static boolean checkEmpty(String[] args) {
        return ((args.length == 1) && (args[0].isEmpty()));
    }

    public static boolean modeAgregate = false;

    public static void addWord(Map<String, Integer> dict,
            Map<String, String> lowerCaseWords, String word, Mode mode) {
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
            String lowerCaseWord = word.toLowerCase();
            if (!lowerCaseWords.containsKey(lowerCaseWord))
                lowerCaseWords.put(lowerCaseWord, word);
            amount = dict.get(lowerCaseWord);
            if (dict.get(lowerCaseWord) != null) {
                dict.put(lowerCaseWord, amount + 1);
            } else {
                dict.put(lowerCaseWord, 1);
            }
        }

    }

    public static Mode getParam(char c, Mode mode) {
        if (c == 'l') {
            if (mode == Mode.None) {
                mode = Mode.L;
            } else {
                return Mode.Err;
            }

        } else if (c == 'w') {
            if (mode == Mode.None) {
                mode = Mode.W;
            } else {
                return Mode.Err;
            }
        } else if (c == 'u') {
            if (mode == Mode.L) {
                mode = Mode.Lu;
            } else if (mode == Mode.W) {
                mode = Mode.Wu;
            } else {
                return Mode.Err;
            }
        } else if (c == 'U') {
            if (mode == Mode.L) {
                mode = Mode.LU;
            } else if (mode == Mode.W) {
                mode = Mode.WU;
            } else {
                return Mode.Err;
            }
        } else if (c == 'a') {
            modeAgregate = true;
        } else {
            mode = Mode.Err;
        }
        return mode;
    }

    public static void main(String[] args) throws Exception {
        Mode mode = Mode.None;
        if (checkEmpty(args)) {
            System.out.println("Usage: java Counter [keys] FILE1 FILE2 ...");
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
        if (mode == Mode.Err) {
            System.out.println("Wrong parametres combination");
            System.exit(1);
        }
        if ((paramNumb == 0) || (mode == Mode.None)) {
            mode = Mode.W;
        }
        Map<String, Integer> dict = new HashMap<String, Integer>();
        Map<String, String> lowerCaseWords = new HashMap<String, String>();
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
                            addWord(dict, lowerCaseWords, word, mode);
                        }
                    } else {
                        if (modeLines(mode)) {
                            addWord(dict, lowerCaseWords, strLine, mode);
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
                if (modeUnique(mode)) {
                    System.out.println("File " + args[i] + " :");
                    if (!modeUniqueWithReg(mode)) {
                        Iterator<String> iterator = dict.keySet().iterator();
                        while (iterator.hasNext()) {
                            String word = iterator.next().toString();
                            String lowerCaseWord = lowerCaseWords.get(word);
                            String amount = dict.get(word).toString();
                            System.out.println(lowerCaseWord + " " + amount);
                        }
                    } else {
                        Iterator<String> iterator = dict.keySet().iterator();
                        while (iterator.hasNext()) {
                            String word = iterator.next().toString();
                            String amount = dict.get(word).toString();
                            System.out.println(word + " " + amount);
                        }
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
                lowerCaseWords.clear();
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