package ru.fizteh.fivt.students.altimin.calendar;

import ru.fizteh.fivt.students.altimin.ArgumentsParser;

import java.security.KeyException;
import java.util.TimeZone;

/**
 * User: altimin
 * Date: 11/12/12
 * Time: 8:45 PM
 */

public class CalendarRunner {

    private static Integer parseInt(String string) {
        return (string == null) ? null : Integer.parseInt(string);
    }

    public static void main(String[] args) throws KeyException {
        ArgumentsParser argumentsParser = new ArgumentsParser();
        argumentsParser.addKey("w");
        argumentsParser.addKey("y", true);
        argumentsParser.addKey("m", true);
        argumentsParser.addKey("t", true);
        ArgumentsParser.ParseResult parseResult = argumentsParser.parse(args);
        if (parseResult.hasProperty("t")) {
            String[] availableTimeZones = TimeZone.getAvailableIDs();
            String timeZone = parseResult.getProperty("t");
            boolean correctTimeZone = false;
            for (String tz: availableTimeZones) {
                if (tz.equals(availableTimeZones)) {
                    correctTimeZone = true;
                    break;
                }
            }
            if (!correctTimeZone) {
                System.err.println("Incorrect time zone");
                System.exit(1);
            }
        }
        CalendarPrinter calendarPrinter = new CalendarPrinter(
                parseInt(parseResult.getProperty("y")),
                parseInt(parseResult.getProperty("m")),
                parseResult.hasProperty("w"),
                //TimeZone.getTimeZone(parseResult.getProperty("t")));
                parseResult.hasProperty("t") ? TimeZone.getTimeZone(parseResult.getProperty("t")) : null);
        calendarPrinter.print();
    }
}
