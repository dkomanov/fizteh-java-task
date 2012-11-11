package ru.fizteh.fivt.students.altimin.calendar;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * User: altimin
 * Date: 11/10/12
 * Time: 5:32 PM
 */
public class CalendarPrinter {
    private static final int OFFSET = 3;

    public static void printCalendar(int month, int year, boolean printWeekNumber, TimeZone timeZone) {
        Calendar calendar;
        if (timeZone == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = Calendar.getInstance(timeZone);
        }
        calendar.set(month, year, 1);
        //int firstDayOfWeek = calendar.get
    }
}
