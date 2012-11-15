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
        CalendarPrinter calendarPrinter = new CalendarPrinter(
                parseInt(parseResult.getProperty("y")),
                parseInt(parseResult.getProperty("m")),
                parseResult.hasProperty("w"),
                //TimeZone.getTimeZone(parseResult.getProperty("t")));
                parseResult.hasProperty("t") ? TimeZone.getTimeZone(parseResult.getProperty("t")) : null);
        calendarPrinter.print();
    }
}
