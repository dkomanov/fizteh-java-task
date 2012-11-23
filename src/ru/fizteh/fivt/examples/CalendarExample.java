package ru.fizteh.fivt.examples;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public class CalendarExample {

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();

        System.out.println(calendar);
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(calendar.get(Calendar.SECOND));

        System.out.println(calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        System.out.println(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.MONTH, 2);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("Y.M.d");

        TimeZone tz = TimeZone.getTimeZone("Asia/Omsk");
        simpleDateFormat.setTimeZone(tz);
        System.out.println(simpleDateFormat.format(calendar.getTime()));

        System.out.println(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));

        System.out.println(Arrays.toString(TimeZone.getAvailableIDs()));

        System.out.println(tz.getDisplayName());

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
    }
}
