/**
 * 
 */
package ru.fizteh.fivt.students.nikitaAntonov.utils;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Дешёвая въетнамская подделка под getopt (ну надо же как-то выживать в этом
 * жестоком мире без getopt`а)
 * 
 * @author Антонов Никита
 */
public class OptionParser {
    private TreeMap<Character, Option> options;
    protected boolean canWorkWithoutParams = false;
    public ArrayList<String> FreedomOpts;

    public OptionParser(String optstring) {

        if (optstring == null) {
            throw new NullPointerException();
        }

        options = new TreeMap<>();

        int toSkip = 0;

        for (int i = 0, e = optstring.length(); i < e; ++i) {
            char c = optstring.charAt(i);

            if (toSkip > 0) {
                --toSkip;
                continue;
            }

            if (!Character.isLetterOrDigit(c)) {
                throw new OptstringException("Incorrect symbol '" + c
                        + "' in optstring\n(position: " + i + ")");
            }

            if (options.containsKey(c)) {
                throw new OptstringException("the symbol '" + c
                        + "' appeared twice");
            }

            char next = (i + 1 < e) ? optstring.charAt(i + 1) : 0;
            char next2 = (i + 2 < e) ? optstring.charAt(i + 2) : 0;

            Option.NeedValue q = Option.NeedValue.NO;

            if (next == ':') {
                if (next2 == ':') {
                    toSkip = 2;
                    q = Option.NeedValue.OPTIONAL;
                } else {
                    toSkip = 1;
                    q = Option.NeedValue.NECESSARY;
                }
            }

            options.put(c, new Option(q));
        }
    }

    public void parse(String args[]) throws IncorrectArgsException {
        boolean skipNext = false;

        for (int i = 0, end = args.length; i < end; ++i) {

            if (skipNext) {
                skipNext = false;
                continue;
            }

            String opt = args[i];
            String nextOpt = ((i + 1 < end) ? args[i + 1] : null);

            if (isOptions(args[i])) {
                skipNext = parseOption(opt.substring(1), nextOpt);
            } else {
                FreedomOpts.add(opt);
            }
        }
    }

    private boolean parseOption(String opt, String next)
            throws IncorrectArgsException {
        boolean nextOptWasUsed = false;

        for (int i = 0, e = opt.length(); i < e; ++i) {
            char c = opt.charAt(i);

            Option o = options.get(c);

            if (o == null) {
                throw new IncorrectArgsException("Incorrect argument " + c);
            }

            if (o.qualifier == Option.NeedValue.NO
                    || (o.qualifier == Option.NeedValue.OPTIONAL && (i + 1 != e
                            || next == null || isOptions(next)))) {
                o.value = null;
                o.isSet = true;
            } else if (o.qualifier != Option.NeedValue.NO && i + 1 == e
                    && next != null && !isOptions(next)) {
                o.value = next;
                o.isSet = true;
                nextOptWasUsed = true;
            } else {
                throw new IncorrectArgsException(
                        "Incorrect usage of parameter " + c);
            }

        }

        return nextOptWasUsed;
    }

    public boolean has(char c) {
        Option o = options.get(c);
        if (o == null) {
            return false;
        }
        return o.isSet;
    }

    public boolean hasArgument(char c) {
        Option o = options.get(c);
        if (o == null) {
            return false;
        }

        return o.value != null;
    }

    public String valueOf(char c) {
        Option o = options.get(c);
        if (o == null) {
            return null;
        }

        return o.value;
    }

    static boolean isOptions(String opt) {
        return opt.matches("^-\\w+$");
    }

    static class Option {
        public String value = null;
        public boolean isSet = false;
        public NeedValue qualifier;

        enum NeedValue {
            NO, OPTIONAL, NECESSARY
        };

        public Option(NeedValue q) {
            qualifier = q;
        }
    }

    public static class OptstringException extends RuntimeException {

        private static final long serialVersionUID = -8414609923100637823L;

        public OptstringException(String message) {
            super(message);
        }
    }

    public static class IncorrectArgsException extends Exception {

        private static final long serialVersionUID = 5392124941354868981L;

        public IncorrectArgsException(String message) {
            super(message);
        }
    }
}
