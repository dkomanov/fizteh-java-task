package ru.fizteh.fivt.students.altimin.wordcounter;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: altimin
 * Date: 10/30/12
 * Time: 2:53 PM
 */
public class WordCounter {
    public enum Mode {
        LINES, WORDS
    }
    private Mode mode;
    private boolean aggregate;
    private boolean capitalLetterMatters;
    private boolean uniqueCountMode;
    private boolean countEmpty;

    private Map<String, Integer> countUniqueElements(InputParser parser) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        while (!parser.isEndOfInput()) {
            String currentString = parser.nextToken();
            if (countEmpty || currentString.length() > 0)
            {
                Integer value = result.get(currentString);
                if (value == null) {
                    result.put(currentString, 1);
                }
                else {
                    result.put(currentString, value + 1);
                }
            }
        }
        return result;
    }

    private int countElements(InputParser parser) {
        int result = 0;
        while (!parser.isEndOfInput()) {
            String value = parser.nextToken();
            if (countEmpty || value.length() > 0) {
                result ++;
            }
        }
        return result;
    }

    InputParser getParser(String fileName) throws FileNotFoundException {
        if (mode == Mode.LINES) {
            return new CharInputParser(fileName, new EolnCharAcceptor());
        } else {
            return new CharInputParser(fileName, new LetterCharAcceptor());
        }
    }

    public void addResult(Map<String,Integer> to, Map<String,Integer> from) {
        for (String string: from.keySet()) {
            if (to.containsKey(string)) {
                to.put(string, to.get(string) + from.get(string));
            } else {
                to.put(string, from.get(string));
            }
        }
    }

    private void printResult(Map<String, Integer> result) {
        for (String value: result.keySet()) {
            if (value.length() > 0 ) {
                System.out.println(value + " " + result.get(value));
            } else {
                System.out.println("empty " + result.get(value));
            }
        }
    }

    private Map<String, Integer> mapToLower(Map<String, Integer> map) {
        Map<String,Integer> result = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        for (String string: map.keySet()) {
            Integer value = result.get(string);
            if (value != null) {
                result.put(string, value + map.get(string));
            } else {
                result.put(string, map.get(string));
            }
        }
        return result;
    }


    public void processFile(String fileName) throws FileNotFoundException {
        System.out.println(fileName + ":");
        if (uniqueCountMode) {
            Map<String, Integer> result = countUniqueElements(getParser(fileName));
            if (!capitalLetterMatters) {
                result = mapToLower(result);
            }
            printResult(result);
        } else {
            System.out.println(countElements(getParser(fileName)));
        }
    }

    public void count(String[] files) {
        if (aggregate) {
            if (uniqueCountMode) {
                Map<String, Integer> result = new TreeMap<String, Integer>();
                for (String fileName: files) {
                    try {
                        addResult(result, countUniqueElements(getParser(fileName)));
                    }
                    catch (FileNotFoundException e) {
                        System.err.println("File " + fileName + " not found");
                        System.exit(1);
                    }
                }
                if (!capitalLetterMatters) {
                    result = mapToLower(result);
                }
                printResult(result);
            } else {
                int result = 0;
                for (String fileName: files) {
                    try {
                        result += countElements(getParser(fileName));
                    }
                    catch (FileNotFoundException e) {
                        System.err.println("File " + fileName + " not found");
                        System.exit(1);
                    }
                }
                System.out.println(result);
            }
        } else {
            for (String fileName: files) {
                try {
                    processFile(fileName);
                }
                catch (FileNotFoundException e) {
                    System.err.println("File " + fileName + " not found");
                    System.exit(1);
                }
            }
        }
    }

    public WordCounter(Mode mode, boolean aggregate, boolean capitalLetterMatters, boolean uniqueCountMode, boolean countEmpty) {
        this.mode = mode;
        this.aggregate = aggregate;
        this.capitalLetterMatters = capitalLetterMatters;
        this.uniqueCountMode = uniqueCountMode;
        this.countEmpty = countEmpty;
    }
}
