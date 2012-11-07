package ru.fizteh.fivt.students.alexanderKuzmin.wordCounter;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Kuzmin A. group 196 Class WordCounter for counting of the number of
 *         words and others.
 * 
 */

class Mode {
    boolean w; // key -w
    boolean l; // key -l
    boolean u; // key -u
    boolean U; // key -U
    boolean a; // key -a
};

public class WordCounter {

    private static void printErrAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

    private static <T extends Closeable> void closeStream(T stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                printErrAndExit(e.getClass().getName().toString());
            }
        }
    }

    void worker(String[] name, int n, Mode mod) {
        if (mod.w || mod.l || mod.a) {
            if (mod.l && mod.w) {
                printErrAndExit("Please, chose only one key: '-w' or '-l'. They are not compatible.");
            }
            if (mod.a && !mod.U) {
                mod.u = true;
            }
            Integer count = 0;
            HashMap<String, Integer> hmap = new HashMap<String, Integer>();
            for (int i = n; i < name.length; ++i) {
                FileReader fReader = null;
                BufferedReader bReader = null;
                try {
                    fReader = new FileReader(name[i]);
                    bReader = new BufferedReader(fReader);
                    String curline;
                    while ((curline = bReader.readLine()) != null) {
                        if (mod.l) {
                            ++count;
                            if (mod.U || mod.u) {
                                if (mod.U) {
                                    curline = curline.toLowerCase();
                                }
                                if (hmap.containsKey(curline)) {
                                    hmap.put(curline, hmap.get(curline) + 1);
                                } else {
                                    hmap.put(curline, 1);
                                }
                            }
                        } else {
                            String[] words = curline
                                    .split("[\\s\\.,:;\\\"\\\'\\(\\)\\\\!]+");
                            count += words.length;
                            if (mod.U || mod.u) {
                                for (String s : words) {
                                    if (mod.U) {
                                        s = s.toLowerCase();
                                    }
                                    if (hmap.containsKey(s)) {
                                        hmap.put(s, hmap.get(s) + 1);
                                    } else {
                                        hmap.put(s, 1);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    printErrAndExit(e.getClass().getName().toString());
                } finally {
                    closeStream(fReader);
                    closeStream(bReader);
                }
                if (!mod.a) {
                    System.out.println(name[i] + ":");
                    if (!mod.U && !mod.u) {
                        System.out.println(count);
                        count = 0;
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
                if (mod.U || mod.u) {
                    StringBuilder sb = new StringBuilder();
                    for (String str : hmap.keySet()) {
                        sb.append(str).append(" ").append(hmap.get(str))
                                .append("\n");
                    }
                    System.out.println(sb);
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
            printErrAndExit("No FILE, please use: 'java WordCounter [keys] FILE1 FILE2 ...'");
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
                            printErrAndExit("Error with keys. Use only 'w', 'l', 'u', 'U', 'a'");
                        }
                    }
                }
            }
            wc.worker(args, i, mod);
        } else {
            printErrAndExit("Please, enter the correct input (java WordCounter [keys] FILE1 FILE2 ...)");
        }
    }
}