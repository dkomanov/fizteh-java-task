/*
 * Counter.java
 * Oct 6, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.counter;

import java.util.*;
import java.io.*;

/*
 * Utility for counting lines or words in a file
 */
public class Counter {
    public final static String SEP = " \t\n.,:;!?";
    /* File to count in */
    private File file;
    /* What to count, how to count? */
    private Settings settings;

    /* Console program counting files listed by user */
    public static void main(String[] args) {
        Settings settings = new Settings();
        List<File> files = null;
        try {
            files = settings.processArgs(args);
        } catch (CounterUsageException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        CounterResult sum = new CounterResult();
        for (File file : files) {
            CounterResult result = new CounterResult();
            try {
                result = new Counter(file, settings).count();
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
                System.exit(1);
            }
            sum.add(result);
            if (!settings.aggregate) {
                System.out.println(file + ":\n" + result.str(settings));
            }
        }
        if (settings.aggregate) {
            System.out.println("total:\n" + sum.str(settings));
        }
    }

    /* Initializes fields */
    public Counter(File file, Settings settings) {
        this.file = file;
        this.settings = settings;
    }

    /* Returns the needed answer */
    public CounterResult count() throws IOException {
        CounterResult result = new CounterResult();
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(file));

            if (!settings.uniqueSensitive && !settings.uniqueNonSensitive) {
                while (input.ready()) {
                    String line = input.readLine();
                    ++result.lines;
                    if (settings.words) {
                        StringTokenizer tok = new StringTokenizer(line, SEP);
                        result.words += tok.countTokens();
                    }
                }
            } else {
                Map<String, Integer> lineItems = new HashMap<String, Integer>();
                Map<String, Integer> wordItems = new HashMap<String, Integer>();
                while (input.ready()) {
                    String line = input.readLine();
                    if (settings.uniqueNonSensitive) {
                        line = line.toLowerCase();
                    }
                    if (settings.lines) {
                        if (lineItems.containsKey(line)) {
                            lineItems.put(line, lineItems.get(line) + 1);
                        } else {
                            lineItems.put(line, 1);
                        }
                    }
                    if (settings.words) {
                        StringTokenizer tok = new StringTokenizer(line, SEP);
                        while (tok.hasMoreTokens()) {
                            String word = tok.nextToken();
                            if (wordItems.containsKey(word)) {
                                wordItems.put(word, wordItems.get(word) + 1);
                            } else {
                                wordItems.put(word, 1);
                            }
                        }
                    }
                }
                result.wordItems = wordItems;
                result.lineItems = lineItems;
            }
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return result;
    }
}

/*
 * Utility class for storing settings
 * and parsing them from console arguments
 */
class Settings {
    /* Settings */
    public static final boolean ALLOW_WL = false;
    public static final boolean ALLOW_UU = false;
    public boolean words = false;
    public boolean lines = false;
    public boolean uniqueSensitive = false;
    public boolean uniqueNonSensitive = false;
    public boolean aggregate = false;

    /* Processes arguments and changes fields
     * if neccessary. Returns a list of files
     */
    public List<File> processArgs(String[] args)
            throws CounterUsageException {
        List<File> result = new ArrayList<File>();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                String longopt = arg.substring(1);
                for (int index = 0; index < longopt.length(); ++index) {
                    char opt = longopt.charAt(index);
                    if (opt == 'a') {
                        aggregate = true;
                    } else if (opt == 'w') {
                        words = true;
                    } else if (opt == 'l') {
                        lines = true;
                    } else if (opt == 'u') {
                        uniqueSensitive = true;
                    } else if (opt == 'U') {
                        uniqueNonSensitive = true;
                    }
                    else {
                        throw new CounterUsageException("Unknown option " + opt);
                    }
                }
            } else {
                File file = new File(arg);
                if (!file.exists()) {
                    throw new CounterUsageException("File " + file + " not found");
                }
                result.add(file);
            }
        }
        if (result.isEmpty()) {
            throw new CounterUsageException("usage: java ru.fizteh.fivt.students.harius.counter.Counter [arguments] [files]");
        }
        if (!words && !lines) {
            words = true;
        }
        if (!Settings.ALLOW_WL && words && lines) {
            throw new CounterUsageException("Error: -w and -l cannot be used together");
        }
        if (!Settings.ALLOW_UU && uniqueNonSensitive && uniqueSensitive) {
            throw new CounterUsageException("Error: -u and -U cannot be used together");
        }
        return result;
    }
}

/*
 * Represents output of call to Counter
 */
class CounterResult {
    /* Numbers of words and lines */
    public int words = 0;
    public int lines = 0;
    public Map<String, Integer> wordItems = new HashMap<String, Integer>();
    public Map<String, Integer> lineItems = new HashMap<String, Integer>();

    /*
     * Accumulate result
     */
    public void add(CounterResult another) {
        words += another.words;
        lines += another.lines;
        for (String word : another.wordItems.keySet()) {
            if (wordItems.containsKey(word)) {
                wordItems.put(word, wordItems.get(word) + another.wordItems.get(word));
            } else {
                wordItems.put(word, another.wordItems.get(word));
            }
        }
        for (String line : another.lineItems.keySet()) {
            if (lineItems.containsKey(line)) {
                lineItems.put(line, lineItems.get(line) + another.lineItems.get(line));
            } else {
                lineItems.put(line, another.lineItems.get(line));
            }
        }
    }

    /*
     * Convert to string properly
     */
    public String str(Settings settings) {
        String result = "";
        if (settings.uniqueSensitive || settings.uniqueNonSensitive) {
            if (settings.words) {
                for(String word : wordItems.keySet()) {
                    result += word + " " + wordItems.get(word) + "\n";
                }
            }
            if (settings.lines) {
                for(String line : lineItems.keySet()) {
                    result += line + " " + lineItems.get(line) + "\n";
                }
            }
        } else {
            if (settings.words) {
                result += words + " ";
            }
            if (settings.lines) {
                result += lines;
            }
        }
        return result;
    }
}

/*
 * Exception to be used by Counter
 */
class CounterUsageException extends Exception {
    /* constructor */
    public CounterUsageException(String why) {
        super(why);
    }
}