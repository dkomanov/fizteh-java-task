package ru.fizteh.fivt.students.tolyapro.wordCounter;

import java.io.*;

import java.util.*;

public class Counter {

    public static boolean checkParam(int param) {
        return ((!((param < 100) || (param > 221) || ((param / 10) % 10 > 2))) || (param == 0));
    }

    public static boolean modeWords(int param) {
        return (param / 100 == 2);
    }

    public static boolean modeLines(int param) {
        return (param / 100 == 1);
    }

    public static boolean modeUniqueWithReg(int param) {
        return ((param % 100) / 10 == 2);
    }

    public static boolean modeUnique(int param) {
        return ((param % 100) / 10 != 0);
    }

    public static boolean checkEmpty(String[] args) {
        return ((args.length == 1) && (args[0].isEmpty()));
    }

    public static boolean modeAgregate(int param) {
        return (param % 10 == 1);
    }

    public static void addWord(Map<String, Integer> dict, String word, int param) {
        if (modeUniqueWithReg(param)) {
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

    public static int getParam(char c) {
        int param = 0;
        if (c == 'l') {
            param += 100;
        } else if (c == 'w') {
            param += 200;
        } else if (c == 'u') {
            param += 10;
        } else if (c == 'U') {
            param += 20;
        } else if (c == 'a') {
            param += 1;
        } else {
            param += 555;
        }
        return param;
    }

    public static void main(String[] args) throws Exception {
        if (checkEmpty(args)) {
            System.out.println("Usage: java Counter [keys] FILE1 FILE2 ...");
        }
        int param = 0;
        int paramNumb = 0;
        for (String s : args) {
            char c = s.charAt(0);
            if ((c == '-') && (s.length() > 1)) {
                c = s.charAt(1);
                param += getParam(c);
                paramNumb++;
                int i;
                for (i = 2; i < s.length(); ++i) {
                    c = s.charAt(i);
                    param += getParam(c);
                    paramNumb++;
                }
                if (i > 2) {
                    paramNumb--;
                }
            } else {
                break;
            }
        }
        if (!checkParam(param)) {
            System.out.println("Wrong parametres combination");
            System.exit(1);
        }
        if (paramNumb == 0) {
            param = 200;
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
                    if (modeWords(param)) {
                        String[] words = strLine.split(" ");
                        for (String word : words) {
                            addWord(dict, word, param);
                        }
                    } else {
                        if (modeLines(param)) {
                            addWord(dict, strLine, param);
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
            }
            if (!modeAgregate(param)) {
                if (modeUnique(param)) {
                    System.out.print(args[i] + " ");
                    if (in != null) {
                        in.close();
                    }
                    if (fstream != null) {
                        fstream.close();
                    }
                    if (br != null) {
                        br.close();
                    }
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
            try {
                in.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
            try {
                br.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
            try {
                fstream.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        if (modeAgregate(param)) {
            if (modeUnique(param)) {
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