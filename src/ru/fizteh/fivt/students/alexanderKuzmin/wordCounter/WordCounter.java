package ru.fizteh.fivt.students.alexanderKuzmin.wordCounter

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

/**
 * @author Kuzmin A. group 196 Class WordCounter for counting of the number of
 *         words and others.
 * 
 */

class Mode {
    boolean w, l, u, U, a;

    Mode() {
        w = false;
        l = false;
        u = false;
        U = false;
        a = false;
    }
};

public class WordCounter {

    void worker(String[] name, int n, Mode mod) {
        if (mod.w || mod.l) {
            if (mod.l && mod.w) {
                System.out
                        .println("Please, chose only one key: '-w' or '-l'. They are not compatible.");
                System.exit(1);
            }
            BigInteger count = BigInteger.ZERO;
            HashMap<String, BigInteger> hmap = new HashMap<String, BigInteger>();
            for (int i = n; i < name.length; ++i) {
                FileReader freader = null;
                BufferedReader breader = null;
                try {
                    freader = new FileReader(name[i]);
                    breader = new BufferedReader(freader);
                    String curline;
                    while ((curline = breader.readLine()) != null) {
                        if (mod.l) {
                            count = count.add(BigInteger.ONE);
                            if (mod.U || mod.u) {
                                if (mod.U) {
                                    curline = curline.toLowerCase();
                                }
                                if (hmap.containsKey(curline)) {
                                    hmap.put(
                                            curline,
                                            hmap.get(curline).add(
                                                    BigInteger.ONE));
                                } else {
                                    hmap.put(curline, BigInteger.ONE);
                                }
                            }
                        } else {
                            String[] words = curline
                                    .split("[\\s\\.,:;\\\"\\\'\\(\\)\\\\!]+");
                            count = count.add(BigInteger.valueOf(words.length));
                            if (mod.U || mod.u) {
                                for (String s : words) {
                                    if (mod.U) {
                                        s = s.toLowerCase();
                                    }
                                    if (hmap.containsKey(s)) {
                                        hmap.put(s,
                                                hmap.get(s).add(BigInteger.ONE));
                                    } else {
                                        hmap.put(s, BigInteger.ONE);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getClass().getName());
                    System.exit(1);
                } finally {
                    if (freader != null)
                        try {
                            freader.close();
                        } catch (IOException e) {
                            System.out.println(e.getClass().getName());
                            System.exit(1);
                        }
                    if (breader != null)
                        try {
                            breader.close();
                        } catch (IOException e) {
                            System.out.println(e.getClass().getName());
                            System.exit(1);
                        }
                }
                if (!mod.a) {
                    System.out.println(name[i] + ":");
                    if (!mod.U && !mod.u) {
                        System.out.println(count);
                        count = BigInteger.ZERO;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (String str : hmap.keySet()) {
                            sb.append(str).append(" ").append(hmap.get(str))
                                    .append("\n");
                        }
                        System.out.println(sb);
                        hmap.clear();
                    }
                }
            }
            if (mod.a) {
                if (!mod.U && !mod.u) {
                    if (mod.w) {
                        System.out.println("Count of words: " + count);
                    } else {
                        System.out.println("Count of lines: " + count);
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (String str : hmap.keySet()) {
                        sb.append(str).append(" ").append(hmap.get(str))
                                .append("\n");
                    }
                    System.out.println("Answer: \n" + sb);
                    hmap.clear();
                }
            }
        } else {
            System.out
                    .println("Error with keys. Please, enter key '-l' or '-w'.");
        }
    }

    /**
     * @param [keys] FILE1 FILE2 ...
     */
    public static void main(String[] args) throws Exception {

        WordCounter wc = new WordCounter();
        Mode mod = new Mode();
        if (args.length < 1) {
            System.out
                    .println("No FILE, please use: 'java WordCounter [keys] FILE1 FILE2 ...'");
        } else if (args.length > 0 && args[0].charAt(0) != '-') {
            mod.w = true;
            wc.worker(args, 0, mod);
        } else if (args.length > 1) {
            int i = 0;
            for (; i < args.length; ++i) {
                if (args[i].charAt(0) != '-') {
                    break;
                } else {
                    for (int j = 1; j < args[i].length(); ++j) {
                        switch (args[i].charAt(j)) {
                        case 'w':
                            mod.w = true;
                            break;
                        case 'l':
                            mod.l = true;
                            break;
                        case 'u':
                            mod.u = true;
                            break;
                        case 'U':
                            mod.U = true;
                            break;
                        case 'a':
                            mod.a = true;
                            break;
                        default:
                            System.out
                                    .println("Error with keys. Use only 'w', 'l', 'u', 'U', 'a'");
                            System.exit(1);
                        }
                    }
                }
            }
            wc.worker(args, i, mod);
        } else {
            System.out
                    .println("Please, enter the correct input (java WordCounter [keys] FILE1 FILE2 ...)");
        }
    }
}