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
    boolean words; // key -w
    boolean lines; // key -l
    boolean uniqueSensitive; // key -u
    boolean uniqueInsensitive; // key -U
    boolean aggregate; // key -a
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
                printErrAndExit(e.getMessage());
            }
        }
    }

    void worker(String[] name, int n, Mode mod) {
        if (mod.words || mod.lines || mod.aggregate) {
            if (mod.lines && mod.words) {
                printErrAndExit("Please, chose only one key: '-w' or '-l'. They are not compatible.");
            }
            if (mod.aggregate && !mod.uniqueInsensitive) {
                mod.uniqueSensitive = true;
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
                        if (mod.lines) {
                            ++count;
                            if (mod.uniqueInsensitive || mod.uniqueSensitive) {
                                if (mod.uniqueInsensitive) {
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
                            if (mod.uniqueInsensitive || mod.uniqueSensitive) {
                                for (String s : words) {
                                    if (mod.uniqueInsensitive) {
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
                    printErrAndExit(e.getMessage());
                } finally {
                    closeStream(fReader);
                    closeStream(bReader);
                }
                if (!mod.aggregate) {
                    System.out.println(name[i] + ":");
                    if (!mod.uniqueInsensitive && !mod.uniqueSensitive) {
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
            if (mod.aggregate) {
                if (mod.uniqueInsensitive || mod.uniqueSensitive) {
                    for (String str : hmap.keySet()) {
                        System.out.println(str + " " + hmap.get(str));
                    }
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
            mod.words = true;
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
                            mod.words = true;
                            break;
                        case 'l':
                            mod.lines = true;
                            break;
                        case 'u':
                            mod.uniqueSensitive = true;
                            break;
                        case 'U':
                            mod.uniqueInsensitive = true;
                            break;
                        case 'a':
                            mod.aggregate = true;
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