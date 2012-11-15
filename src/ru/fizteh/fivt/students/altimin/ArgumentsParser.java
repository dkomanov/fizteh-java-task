package ru.fizteh.fivt.students.altimin;

import java.security.KeyException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * User: altimin
 * Date: 10/24/12
 * Time: 2:05 PM
 */

/*
    Two types of keys:
        short (only one letter) -l, -C
        long (some name): --config, --name
*/

public class ArgumentsParser {
    public class ParseResult {
        private Set<String> properties;
        public String[] other;
        ParseResult() {
            properties = new HashSet<String>();
        }
        public boolean hasProperty(String property) {
            return properties.contains(property);
        }
    }

    private Set<Character> shortKeys;
    private Set<String> longKeys;

    public ArgumentsParser() {
        longKeys = new HashSet<String>();
        shortKeys = new HashSet<Character>();
    }

    public void addKey(String value) {
        if (value.length() > 0) {
            if (value.length() == 1) {
                shortKeys.add(value.charAt(0));
            } else {
                longKeys.add(value);
            }
        }
    }

    private boolean isLongKey(String value) {
        return (value.length() >= 3 && value.charAt(0) == '-' && value.charAt(1) == '-');
    }

    private boolean isShortKey(String value) {
        return (value.length() >= 2 && value.charAt(0) == '-');
    }

    private void processShortKey(String value, ParseResult parseResult) throws KeyException {
        for (int i = 1; i < value.length(); i ++) {
            if (!shortKeys.contains(value.charAt(i))) {
                throw new KeyException(value + "is not a valid key: unknown symbol " + value.charAt(i));
            }
            parseResult.properties.add(String.valueOf(value.charAt(i)));
        }
    }

    private void processLongKey(String value, ParseResult parseResult) throws KeyException {
        String keyValue = value.substring(2);
        if (!longKeys.contains(keyValue)) {
            throw new KeyException(value + " is not known");
        }
        parseResult.properties.add(keyValue);
    }

    public ParseResult parse(String[] strings) throws KeyException {
        ParseResult parseResult = new ParseResult();
        Vector<String> otherStrings = new Vector<String>();
        for (String string: strings) {
            if (isLongKey(string)) {
                processLongKey(string, parseResult);
            } else if (isShortKey(string)) {
                processShortKey(string, parseResult);
            } else {
                otherStrings.add(string);
            }
        }
        parseResult.other = new String[otherStrings.size()];
        parseResult.other = otherStrings.toArray(parseResult.other);
        return parseResult;
    }
}
