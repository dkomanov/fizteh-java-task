package ru.fizteh.fivt.students.konstantinPavlov;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class WordCounter {

    static boolean settingL = false;
    static boolean settingW = false;
    static boolean settingNoRegister = false;
    static boolean settingRegister = false;
    static boolean settingA = false;

    static int globalLinesAmount = 0;
    static int globalWordsAmount = 0;

    static Map<String, Integer> globalMap;

    // private static BufferedReader bufferedReader;

    static void analyzeFile(String path) throws IOException {
        FileReader fis = null;
        BufferedReader bufferedReader = null;

        try {
            File file = new File(path);
            if (!file.canRead()) {
                System.err.println("Error: file \"" + file.getPath()
                        + "\" can't be opened or readed");
                System.exit(1);
            }
            fis = new FileReader(file);
            bufferedReader = new BufferedReader(fis);

            String str;
            int localLinesAmount = 0;
            int localWordsAmount = 0;
            Map<String, Integer> localMap;
            if (settingNoRegister) {
                localMap = new TreeMap<String, Integer>(
                        String.CASE_INSENSITIVE_ORDER);
            } else {
                localMap = new TreeMap<String, Integer>();
            }

            // start analyzing
            while ((str = bufferedReader.readLine()) != null) {

                if (settingW) {
                    // count words

                    // parsing the string to words
                    String[] arrayOfWords = str
                            .split("[\\s\\.:;,\\\"\\\'\\(\\)!]+");

                    for (int i = 0; i < arrayOfWords.length; ++i) {
                        // add word to our map

                        if (!settingA) {
                            if (localMap.containsKey(arrayOfWords[i])) {
                                ++localWordsAmount;
                                localMap.put(arrayOfWords[i],
                                        localMap.get(arrayOfWords[i]) + 1);
                            } else {
                                ++localWordsAmount;
                                localMap.put(arrayOfWords[i], 1);
                            }
                        } else {
                            if (globalMap.containsKey(arrayOfWords[i])) {
                                ++globalWordsAmount;
                                globalMap.put(arrayOfWords[i],
                                        globalMap.get(arrayOfWords[i]) + 1);
                            } else {
                                ++globalWordsAmount;
                                globalMap.put(arrayOfWords[i], 1);
                            }
                        }
                    }
                } else {
                    // count lines

                    if (!settingNoRegister && !settingRegister) {
                        if (!settingA) {
                            ++localLinesAmount;
                        } else {
                            ++globalLinesAmount;
                        }
                    } else {
                        // count lines without register
                        if (!settingA) {
                            if (localMap.containsKey(str) && !str.isEmpty()) {
                                localMap.put(str, localMap.get(str) + 1);
                            } else {
                                if (!str.isEmpty()) {
                                    localMap.put(str, 1);
                                }
                            }
                        } else {
                            if (globalMap.containsKey(str) && !str.isEmpty()) {
                                globalMap.put(str, globalMap.get(str) + 1);
                            } else {
                                if (!str.isEmpty()) {
                                    globalMap.put(str, 1);
                                }
                            }
                        }
                    }

                }

            }
            // finish analyzing

            // show information about analysis
            if (!settingA) {
                System.out.println(file.getPath() + ":");
                if ((settingW || settingL)
                        && (settingRegister || settingNoRegister)) {
                    for (Map.Entry<String, Integer> entry : localMap.entrySet()) {
                        System.out.println(entry.getKey() + " "
                                + entry.getValue());
                    }
                } else {
                    if (settingW) {
                        System.out.println(localWordsAmount);
                    } else {
                        System.out.println(localLinesAmount);
                    }
                }
            }
        } catch (Exception expt) {
            System.err.println("Error: " + expt.getMessage());
        } finally {
            closer(fis);
            closer(bufferedReader);
        }
    }

    static void closer(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Exception expt) {
            }
        }
    }

    static void showAnalysisA() {
        if (settingA) {
            if (settingW || settingL) {
                for (Map.Entry<String, Integer> entry : globalMap.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
            }
        }
    }

    public static void setSettings(String[] args) {
        for (String str : args) {
            if (str.isEmpty() || str.charAt(0) != '-') {
                continue;
            } else {
                for (int i = 1; i < str.length(); ++i) {
                    switch (str.charAt(i)) {
                    case 'l':
                        if (settingW) {
                            System.err.println("Error: flag w has already set");
                            System.exit(1);
                        }
                        settingL = true;
                        break;

                    case 'w':
                        if (settingL) {
                            System.err.println("Error: flag l has already set");
                            System.exit(1);
                        }
                        settingW = true;
                        break;

                    case 'u':
                        if (settingNoRegister) {
                            System.err.println("Error: flag U has alredy set");
                            System.exit(1);
                        }
                        settingRegister = true;
                        break;

                    case 'U':
                        if (settingRegister) {
                            System.err.println("Error: flag u has alredy set");
                            System.exit(1);
                        }
                        settingNoRegister = true;
                        break;

                    case 'a':
                        settingA = true;
                        break;

                    default:
                        System.err.println("Error: wrong flag: "
                                + str.charAt(i));
                        System.exit(1);
                        break;
                    }
                }
            }
        }
        if (!settingL && !settingW) {
            settingW = true;
        }
    }

    public static void main(String[] args) throws IOException {
        setSettings(args);

        if (settingNoRegister) {
            globalMap = new TreeMap<String, Integer>(
                    String.CASE_INSENSITIVE_ORDER);
        } else {
            globalMap = new TreeMap<String, Integer>();
        }

        for (int i = 0; i < args.length; ++i) {
            String path = args[i];
            if (path.isEmpty() || path.charAt(0) == '-') {
                continue;
            }
            analyzeFile(path);
        }
        showAnalysisA();
    }
}