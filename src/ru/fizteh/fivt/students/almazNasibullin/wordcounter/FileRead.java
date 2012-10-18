package ru.fizteh.fivt.students.almazNasibullin.wordcounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * @author almaz
 */

public class FileRead {
    /*
     * в данном классе реализованы функции считывания и подсчета информации
     * из файлов
     */
    public static void closeOrExit(FileReader fr, BufferedReader br) {
        try {
            if (fr != null) {
                fr.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static int countLines(String fileName) {
        BufferedReader br = null;
        FileReader fr = null;
        int count = 0;
        try {
            fr = new FileReader(new File(fileName));
            br = new BufferedReader(fr);
            while (br.readLine() != null) {
                ++count;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            closeOrExit(fr, br);
        }
        return count;
    }

    public static int countWords(String fileName) {
        BufferedReader br = null;
        FileReader fr = null;
        int count = 0;
        try {
            fr = new FileReader(new File(fileName));
            br = new BufferedReader(fr);
            String str;
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, " \t!?';:,.)(@#<>");
                while (st.hasMoreTokens()) {
                    str = st.nextToken();
                    ++count;
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            closeOrExit(fr, br);
        }
        return count;
    }

    public static TreeMap<String, Integer> countUniqWordsWithRegistr(String fileName) {
        BufferedReader br = null;
        FileReader fr = null;
        Map<String, Integer> m = new TreeMap<String, Integer>();
        try {
            fr = new FileReader(new File(fileName));
            br = new BufferedReader(fr);
            String str;
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, " \t!?';:,.)(@#<>");
                while (st.hasMoreTokens()) {
                    String cur = st.nextToken();
                    if (m.containsKey(cur)) {
                        int count = (Integer)m.get(cur);
                        m.put(cur, count + 1);
                    } else {
                        m.put(cur, 1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            closeOrExit(fr, br);
        }
        return (TreeMap)m;
    }

    public static TreeMap<String, Integer> countUniqWordsWithoutRegistr(String fileName) {
        BufferedReader br = null;
        FileReader fr = null;
        Map<String, Integer> m = new TreeMap<String, Integer>();
        try {
            fr = new FileReader(new File(fileName));
            br = new BufferedReader(fr);
            String str;
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, " \t!?';:,.)(@#<>");
                while (st.hasMoreTokens()) {
                    String cur = st.nextToken();
                    cur = cur.toLowerCase();
                    if (m.containsKey(cur)) {
                        int count = (Integer)m.get(cur);
                        m.put(cur, count + 1);
                    } else {
                        m.put(cur, 1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            closeOrExit(fr, br);
        }
        return (TreeMap)m;
    }
}
