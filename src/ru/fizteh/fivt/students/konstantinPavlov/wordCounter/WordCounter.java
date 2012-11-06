package ru.fizteh.fivt.students.konstantinPavlov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class WordCounter {

    static boolean settingL = false;
    static boolean settingW = false;
    static boolean settingNoRegister = false;
    static boolean settingRegister = false;
    static boolean settingA = false;

    static int globalLinesAmount = 0;
    static int globalWordsAmount = 0;

    static Map<String, Integer> globalMap = new HashMap<String, Integer>();
    static Map<String, String> globalLowerCaseMap = new HashMap<String, String>();

    private static BufferedReader bufferedReader;

    static void analyzeFile(String path) throws IOException {
        FileInputStream fis = null;
        InputStreamReader streamReader = null;

        try {
            File file = new File(path);
            if (!file.canRead()) {
                System.err.println("Error: file \"" + file.getPath()
                        + "\" can't be opened or readed");
                System.exit(1);
            }
            fis = new FileInputStream(file);
            streamReader = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(streamReader);

            String str;
            int localLinesAmount = 0;
            int localWordsAmount = 0;
            Map<String, Integer> localMap = new HashMap<String, Integer>();
            Map<String, String> localLowerCaseMap = new HashMap<String, String>();

            // start analyzing
            String searchString;
            while ((str = bufferedReader.readLine()) != null) {

                if (settingW) {
                    // count words

                    // parsing the string to words
                    String[] arrayOfWords = str
                            .split("[\\s\\.:;,\\\"\\\'\\(\\)!]+");

                    for (int i = 0; i < arrayOfWords.length; ++i) {
                        // add word to our map
                        if (settingNoRegister) {
                            searchString = arrayOfWords[i].toLowerCase();
                        } else {
                            searchString = arrayOfWords[i];
                        }

                        if (!settingA) {
                            if (localMap.containsKey(searchString)
                                    && !searchString.equals(" ")
                                    && !searchString.equals("\t")) {
                                ++localWordsAmount;
                                localMap.put(searchString,
                                        localMap.get(searchString) + 1);
                            } else {
                                if (!searchString.equals(" ")
                                        && !searchString.equals("\t")) {
                                    ++localWordsAmount;
                                    localMap.put(searchString, 1);
                                    localLowerCaseMap.put(searchString,
                                            arrayOfWords[i]);
                                }
                            }
                        } else {
                            if (globalMap.containsKey(searchString)
                                    && !searchString.equals(" ")
                                    && !searchString.equals("\t")) {
                                ++globalWordsAmount;
                                globalMap.put(searchString,
                                        globalMap.get(searchString) + 1);
                            } else {
                                if (!searchString.equals(" ")
                                        && !searchString.equals("\t")) {
                                    ++globalWordsAmount;
                                    globalMap.put(searchString, 1);
                                    globalLowerCaseMap.put(searchString,
                                            arrayOfWords[i]);
                                }
                            }
                        }
                    }
                } else {
                    // count lines
                    if (settingNoRegister) {
                        searchString = str.toLowerCase();
                    } else {
                        searchString = str;
                    }

                    if (!settingNoRegister && !settingRegister) {
                        if (!settingA) {
                            ++localLinesAmount;
                        } else {
                            ++globalLinesAmount;
                        }
                    } else {
                        // count lines without register
                        if (!settingA) {
                            if (localMap.containsKey(searchString)
                                    && !searchString.isEmpty()) {
                                localMap.put(searchString,
                                        localMap.get(searchString) + 1);
                            } else {
                                if (!searchString.isEmpty()) {
                                    localMap.put(searchString, 1);
                                    localLowerCaseMap.put(searchString, str);
                                }
                            }
                        } else {
                            if (globalMap.containsKey(searchString)
                                    && !searchString.isEmpty()) {
                                globalMap.put(searchString,
                                        globalMap.get(searchString) + 1);
                            } else {
                                if (!searchString.isEmpty()) {
                                    globalMap.put(searchString, 1);
                                    localLowerCaseMap.put(searchString, str);
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
                        System.out
                                .println(localLowerCaseMap.get(entry.getKey())
                                        + " " + entry.getValue());
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
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception expt) {
                }
            }

            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (Exception expt) {
                }
            }
        }

    }

    static void showAnalysisA() {
        if (settingA) {
            if (settingW || settingL) {
                for (Map.Entry<String, Integer> entry : globalMap.entrySet()) {
                    System.out.println(globalLowerCaseMap.get(entry.getKey())
                            + " " + entry.getValue());
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
        if (!settingL && !settingW)
            settingW = true;
    }

    public static void main(String[] args) throws IOException {
        setSettings(args);

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