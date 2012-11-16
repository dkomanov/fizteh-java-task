package ru.fizteh.fivt.students.altimin;

import java.security.KeyException;
import java.util.*;

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
        private Map<String, String> values;
        public String[] other;
        ParseResult() {
            properties = new HashSet<String>();
            values = new HashMap<String, String>();
        }
        public boolean hasProperty(String property) {
            return properties.contains(property);
        }

        public String getProperty(String property) {
            return values.get(property);
        }
    }

    private Set<Character> shortKeys;
    private Set<String> longKeys;
    private Set<String> keysWithProperties;

    public ArgumentsParser() {
        longKeys = new HashSet<String>();
        shortKeys = new HashSet<Character>();
        keysWithProperties = new HashSet<String>();
    }

    public void addKey(String value, boolean hasProperty) {
        if (value.length() > 0) {
            if (value.length() == 1) {
                shortKeys.add(value.charAt(0));
            } else {
                longKeys.add(value);
            }
        }
        if (hasProperty) {
            keysWithProperties.add(value);
        }
    }

    public void addKey(String value) {
        addKey(value, false);
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
            if (value.length() > 2 && keysWithProperties.contains(value.substring(i, i + 1))) {
                throw new KeyException("-" + value.charAt(i) + " key should have a parameter");
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

    private boolean hasProperty(String value) {
        return keysWithProperties.contains(getKeyValue(value));
    }

    private String getKeyValue(String value) {
        if (isLongKey(value)) {
            return value.substring(2);
        } else if (isShortKey(value)) {
            return value.substring(1);
        } else {
            return value;
        }
    }

    public ParseResult parse(String[] strings) throws KeyException {
        ParseResult parseResult = new ParseResult();
        Vector<String> otherStrings = new Vector<String>();
        for (int i = 0; i < strings.length; i ++) {
            if (isLongKey(strings[i]) || isShortKey(strings[i])) {
                if (isLongKey(strings[i])) {
                    processLongKey(strings[i], parseResult);
                } else {
                    processShortKey(strings[i], parseResult);
                }
                if (hasProperty(strings[i])) {
                    if (i + 1 == strings.length) {
                        throw new KeyException("No parameter for key " + strings[i]);
                    }
                    parseResult.values.put(getKeyValue(strings[i]), strings[i + 1]);
                    i ++;
                }
            } else {
                otherStrings.add(strings[i]);
            }
        }
        parseResult.other = new String[otherStrings.size()];
        parseResult.other = otherStrings.toArray(parseResult.other);
        return parseResult;
    }
}
