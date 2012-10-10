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
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(new File(fileName));
            br = new BufferedReader (fr);
            int count = 0;
            while (br.readLine() != null) {
                ++count;
            }
            return count;
        } catch (Exception e) {
            throw e;
        } finally {
            fr.close();
            br.close();
        }
        
    }

    public static int countWords(String fileName) throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(new File(fileName));
            br = new BufferedReader (fr);
            int count = 0;
            String str;
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, " \t!?';:,.)(@#<>");
                while (st.hasMoreTokens()) {
                    str = st.nextToken();
                    ++count;
                }
            }
            return count;
        } catch (Exception e) {
            throw e;
        } finally {
            fr.close();
            br.close();
        }
    }

    public static TreeMap countUniqWordsWithRegistr(String fileName) throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(new File(fileName));
            br = new BufferedReader (fr);
            Map<String, Integer> m = new TreeMap<String, Integer>();
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
            return (TreeMap) m;
        } catch (Exception e) {
            throw e;
        } finally {
            fr.close();
            br.close();
        }
    }

    public static TreeMap countUniqWordsWithoutRegistr(String fileName)
            throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(new File(fileName));
            br = new BufferedReader (fr);
            Map<String, Integer> m = new TreeMap<String, Integer>();
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
            return (TreeMap) m;
        } catch (Exception e) {
            throw e;
        } finally {
            fr.close();
            br.close();
        }
    }
}
