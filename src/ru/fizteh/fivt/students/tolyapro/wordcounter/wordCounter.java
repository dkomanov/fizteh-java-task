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
            word = word.toLowerCase();
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
        for (int i = paramNumb; i < args.length; ++i) {
            String s = args[i];
            FileInputStream fstream = null;
            DataInputStream in = null;
            BufferedReader br = null;
            try {
                fstream = new FileInputStream(s);
                in = new DataInputStream(fstream);
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
                if (in != null) {
                    in.close();
                }
                if (fstream != null) {
                    fstream.close();
                }
                if (br != null) {
                    br.close();
                }
                System.exit(1);
            } finally {
                if (in != null) {
                    in.close();
                }
                if (fstream != null) {
                    fstream.close();
                }
                if (br != null) {
                    br.close();
                }
            }
            if (!modeAgregate) {
                if (modeUnique(mode)) {
                    System.out.print("File " + args[i] + " ");
                    System.out.println(dict.size());
                    dict.clear();
                } else {
                    Iterator iterator = dict.keySet().iterator();
                    int sum = 0;
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        String value = dict.get(key).toString();
                        sum += new Integer(value);
                    }
                    System.out.print("File " + args[i] + " ");
                    System.out.println(sum);
                    dict.clear();
                }
            }
        }
        if (modeAgregate) {
            if (modeUnique(mode)) {
                System.out.print(dict.size());
                System.exit(0);
            }
            try {
                Iterator iterator = dict.keySet().iterator();
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