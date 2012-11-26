package ru.fizteh.fivt.students.altimin.wordcounter;

import ru.fizteh.fivt.students.altimin.ArgumentsParser;

import java.security.KeyException;

/**
 `* User: altimin
 * Date: 10/30/12
 * Time: 3:15 PM
 */
public class WordCounterRunner {

    private static void exit(String errorMessage) {
        System.err.println(errorMessage);
        System.exit(1);
    }

    public static void main(String[] args) {
        ArgumentsParser argumentsParser = new ArgumentsParser();
        argumentsParser.addKey("l");
        argumentsParser.addKey("w");
        argumentsParser.addKey("u");
        argumentsParser.addKey("U");
        argumentsParser.addKey("a");
        ArgumentsParser.ParseResult parsedArgs;
        try {
            parsedArgs = argumentsParser.parse(args);
        }
        catch (KeyException e) {
            exit("Incorrect parameters");
            return; // java doesn't believe that System.exit is enough. Why?
        }
        boolean aggregate = parsedArgs.hasProperty("a");
        boolean uniqueCountMode = parsedArgs.hasProperty("u") || parsedArgs.hasProperty("U");
        boolean capitalLetterMatters = parsedArgs.hasProperty("u");
        boolean countEmpty;
        WordCounter.Mode mode;
        if (parsedArgs.hasProperty("w") && parsedArgs.hasProperty("l")) {
            exit("Keys -l and -w should not be used together");
        }
        if (parsedArgs.hasProperty("l")) {
            mode = WordCounter.Mode.LINES;
            countEmpty = true;
        } else {
            mode = WordCounter.Mode.WORDS;
            countEmpty = false;
        }
        if (parsedArgs.other.length == 0) {
            exit("No files to explore");
        }
        WordCounter wordCounter = new WordCounter(mode, aggregate, capitalLetterMatters, uniqueCountMode, countEmpty);
        wordCounter.count(parsedArgs.other);
    }
}
