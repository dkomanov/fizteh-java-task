package ru.fizteh.fivt.students.almazNasibullin.wordcounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    public static int countLines(String fileName) throws Exception {
        BufferedReader br;
        try {
            br = new BufferedReader (new FileReader(new File(fileName)));
        } catch (Exception e) {
            throw e;
        }
        int count = 0;
        while (br.readLine() != null) {
            ++count;
        }
        br.close();
        return count;
    }

    public static int countWords(String fileName) throws Exception {
        BufferedReader br;
        try {
            br = new BufferedReader (new FileReader(new File(fileName)));
        } catch (Exception e) {
            throw e;
        }
        int count = 0;
        String str;
        while ((str = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(str, " \t");
            while (st.hasMoreTokens()) {
                str = st.nextToken();
                ++count;
            }
        }
        br.close();
        return count;
    }

    public static TreeMap countUniqWordsWithRegistr(String fileName) throws Exception {
        BufferedReader br;
        try {
            br = new BufferedReader (new FileReader(new File(fileName)));
        } catch (Exception e) {
            throw e;
        }
        Map m = new TreeMap();
        String str;
        while ((str = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(str, " \t");
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
        br.close();
        return (TreeMap) m;
    }

    public static TreeMap countUniqWordsWithoutRegistr(String fileName)
            throws Exception {
        BufferedReader br;
        try {
            br = new BufferedReader (new FileReader(new File(fileName)));
        } catch (Exception e) {
            throw e;
        }
        Map m = new TreeMap();
        String str;
        while ((str = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(str, " \t");
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
        br.close();
        return (TreeMap) m;
    }
}
