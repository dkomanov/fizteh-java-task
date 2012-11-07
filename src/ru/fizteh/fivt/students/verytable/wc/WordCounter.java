package ru.fizteh.fivt.students.verytable.wc;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 29.09.12
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */

public class WordCounter {

    static BufferedReader openFile(String fileName) throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
        } catch (Exception frEx) {
            System.err.println(fileName + " failed to open");
            throw frEx;
        }
        try {
             br = new BufferedReader(fr);
        } catch (Exception ex1) {
            System.err.println(fileName + " failed to open.");
            try {
                closeFile(fileName, fr);
            } catch (Exception ex2) {
                System.out.println(fileName + " failed to close.");
                throw ex2;
            }
        }
        return br;
    }

    static void closeFile(String fileName,
                          Closeable closeable) throws IOException {
        try {
            if(closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {
            System.err.println(fileName + " failed to close.");
            throw ex;
        }
    }

    static void stringWordsCounter(String s, HashMap<String, Integer> elements,
                                   boolean isUpperCaseUniqueKey) {
        if (isUpperCaseUniqueKey) {
            s = s.toLowerCase();
        }
        StringTokenizer tokenizer = new StringTokenizer(s, "[ \t\n.!?,:;]+");
        String curToken;
        while (tokenizer.hasMoreTokens()) {
            curToken = tokenizer.nextToken();
            Integer curTokenPos = elements.get(curToken);
            if (curTokenPos != null) {
                elements.put(curToken, curTokenPos + 1);
            } else {
                elements.put(curToken, 1);
            }
        }
    }

    static void textWordsCounter(HashMap<String, Integer> elements,
                                 BufferedReader br,
                                 boolean isUpperCaseUniqueKey) {
        String curStr;
        try {
            while ((curStr = br.readLine()) != null) {
                stringWordsCounter(curStr, elements,
                                   isUpperCaseUniqueKey);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void textLinesCounter(HashMap<String, Integer> elements,
                                 BufferedReader br,
                                 boolean isUpperCaseUniqueKey) {
        String curStr;
        if (isUpperCaseUniqueKey) {
            try {
                while ((curStr = br.readLine()) != null) {
                    curStr = curStr.toLowerCase();
                    Integer curStrPos = elements.get(curStr);
                    if (curStrPos != null) {
                        elements.put(curStr, elements.get(curStr) + 1);
                    } else {
                        elements.put(curStr, 1);
                    }
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            try {
                while ((curStr = br.readLine()) != null) {
                    Integer curStrPos = elements.get(curStr);
                    if (curStrPos != null) {
                        elements.put(curStr, curStrPos + 1);
                    } else {
                        elements.put(curStr, 1);
                    }
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    static void countForFile(HashMap<String, Integer> elements, String fileName,
                             Keys keys) throws Exception {
        BufferedReader br = null;
        try {
            br = openFile(fileName);
            if (keys.isLinesCountKey) {
                textLinesCounter(elements, br, keys.isUpperCaseUniqueKey);
            } else {
                textWordsCounter(elements, br, keys.isUpperCaseUniqueKey);
            }
        }
        finally {
            closeFile(fileName, br);
        }
    }

    static boolean count(String data[], int lastKeyPosition, Keys keys) {
        boolean wasFilesError = false;
        HashMap<String, Integer> elements = new HashMap<String, Integer>();
        if (keys.isAggregateKey) {
            if (keys.isLowerCaseUniqueKey || keys.isUpperCaseUniqueKey) {
                for (int i = lastKeyPosition + 1; i < data.length; ++i) {
                    try {
                        countForFile(elements, data[i], keys);
                    } catch (Exception ex) {
                        wasFilesError = true;
                    }
                }
                Iterator it = elements.entrySet().iterator();
                Map.Entry me;
                while (it.hasNext()) {
                    me = (Map.Entry) it.next();
                    System.out.println(me.getKey() + " "
                                       + me.getValue());
                }
            } else {
                for (int i = lastKeyPosition + 1; i < data.length; ++i) {
                    try {
                        countForFile(elements, data[i], keys);
                    } catch (Exception ex) {
                        wasFilesError = true;
                    }
                }
                int sum = 0;
                Iterator it = elements.entrySet().iterator();
                Map.Entry me;
                while (it.hasNext()) {
                    me = (Map.Entry) it.next();
                    sum += (Integer) me.getValue();
                }
                System.out.println(sum);
            }
        } else {
            boolean wasCurFileError = false;
            for (int i = lastKeyPosition + 1; i < data.length; ++i) {
                wasCurFileError = false;
                elements.clear();
                try {
                    countForFile(elements, data[i], keys);
                } catch (Exception ex) {
                    wasFilesError = true;
                    wasCurFileError = true;
                }
                if (!wasCurFileError) {
                    if (!keys.isLowerCaseUniqueKey
                        && !keys.isUpperCaseUniqueKey) {
                        int sum = 0;
                        Iterator it = elements.entrySet().iterator();
                        Map.Entry me;
                        while (it.hasNext()) {
                            me = (Map.Entry) it.next();
                            sum += (Integer) me.getValue();
                        }
                        System.out.println(sum);
                    } else {
                        Iterator it = elements.entrySet().iterator();
                        Map.Entry me;
                        while (it.hasNext()) {
                            me = (Map.Entry) it.next();
                            System.out.println(me.getKey() + " "
                                               + me.getValue());
                        }
                    }
                }
            }
        }
        return !wasFilesError;
    }

    static boolean isKey(char c) {
        return (c == 'l' || c == 'w' || c == 'u' || c == 'U' || c == 'a');
    }

    static void parseKeys(String keysString, Keys keys) throws Exception {
        boolean wasKey;

        for (int i = 0; i < keysString.length(); ++i) {
            if (!Character.isSpaceChar(keysString.charAt(i))) {
                if (keysString.charAt(i) == '-') {
                    ++i;
                    wasKey = false;
                    while (i < keysString.length()
                           && (isKey(keysString.charAt(i))
                           || Character.isSpaceChar(keysString.charAt(i)))) {
                        switch (keysString.charAt(i)) {
                            case 'l':
                                keys.isLinesCountKey = true;
                                wasKey = true;
                                break;
                            case 'w':
                                keys.isWordsCountKey = true;
                                wasKey = true;
                                break;
                            case 'u':
                                keys.isLowerCaseUniqueKey = true;
                                wasKey = true;
                                break;
                            case 'U':
                                keys.isUpperCaseUniqueKey = true;
                                wasKey = true;
                                break;
                            case 'a':
                                keys.isAggregateKey = true;
                                wasKey = true;
                                break;
                        }
                        ++i;
                    }
                    if (i != keysString.length()
                        && (keysString.charAt(i) != '-'
                        || keysString.charAt(i) == '-' && !wasKey)) {
                        throw new Exception("Invalid symbol in keys. "
                                            + "In position " + i + ".");
                    }
                    if (i == keysString.length() && wasKey) {
                        break;
                    } else if (i == keysString.length() && !wasKey) {
                        throw new Exception("No keys after '-'. "
                                            + "In position " + i + ".");
                    }
                    --i;
                } else {
                    throw new Exception("Invalid symbol in keys. "
                                        + "In position " + i + ".");
                }
            }
        }
    }

    static boolean isCorrectKeySet(Keys keys) {
        return !(keys.isLinesCountKey && keys.isWordsCountKey
                 || keys.isLowerCaseUniqueKey && keys.isUpperCaseUniqueKey);
    }

    public static void main(String args[]) {

        if (args == null || args.length == 0) {
            System.out.println("Usage: [keySequence1] [keySequence2]... "
                               + "(-l, -w, -u, -U, -a) "
                               + "[file1] [file2]...\n"
                               + "where each keySequence starts with '-' "
                               + "and should contain at least one key "
                               + "between each two hyphens and "
                               + "between the last hyphen and the end "
                               + "of keySequence.\n"
                               + "Keys shouldn't be contradicting.\n"
                               + "Between each two symbols could be "
                               + "many space characters.");
            System.exit(1);
        }

        int lastKeyPosition = -1;
        while (lastKeyPosition + 1 < args.length
               && args[lastKeyPosition + 1].startsWith("-")) {
            ++lastKeyPosition;
        }
        if (lastKeyPosition == args.length - 1) {
            System.err.println("No files declared!");
            System.exit(1);
        }

        Keys keys = new Keys();
        for (int i = 0; i <= lastKeyPosition; ++i) {
            try {
                parseKeys(args[i], keys);
            } catch (Exception ex) {
                System.err.println("In argument " + i + ": "
                                   + ex.getMessage());
                System.exit(1);
            }
        }
        if (!keys.isLinesCountKey) {
            keys.isWordsCountKey = true;
        }
        if (!isCorrectKeySet(keys)) {
            System.err.println("Unacceptable key sequence: "
                               + "(u, U), (l, w) "
                               + "are not allowed.");
            System.exit(1);
        }

        if (!count(args, lastKeyPosition, keys)) {
            System.exit(1);
        }
    }
}

class Keys {
    boolean isLinesCountKey = false;
    boolean isWordsCountKey = false;
    boolean isLowerCaseUniqueKey = false;
    boolean isUpperCaseUniqueKey = false;
    boolean isAggregateKey = false;
}

