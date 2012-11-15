package ru.fizteh.fivt.students.altimin.calendar;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: altimin
 * Date: 11/10/12
 * Time: 5:32 PM
 */
public class CalendarPrinter {
    private static final int DAYS_IN_WEEK = 7;
    private boolean printWeekNumber;
    private Calendar calendar;
    private int month;
    private int year;
    private final int offset = 2;
    private final int minimalWeekDayNumber;
    private final int maximalWeekDayNumber;
    private TimeZone timeZone;

    private int convertMonthValue(int value) throws IllegalArgumentException {
        if (value == 1) return Calendar.JANUARY;
        if (value == 2) return Calendar.FEBRUARY;
        if (value == 3) return Calendar.MARCH;
        if (value == 4) return Calendar.APRIL;
        if (value == 5) return Calendar.MAY;
        if (value == 6) return Calendar.JUNE;
        if (value == 7) return Calendar.JULY;
        if (value == 8) return Calendar.AUGUST;
        if (value == 9) return Calendar.SEPTEMBER;
        if (value == 10) return Calendar.OCTOBER;
        if (value == 11) return Calendar.NOVEMBER;
        if (value == 12) return Calendar.DECEMBER;
        throw new IllegalArgumentException(value + " is not valid month number");

    }

    public CalendarPrinter(Integer year, Integer month, boolean printWeekNumber, TimeZone timeZone)
            throws IllegalArgumentException
    {
        this.timeZone = timeZone;
        this.printWeekNumber = printWeekNumber;
        if (timeZone == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = Calendar.getInstance(timeZone);
        }
        this.month = (month != null) ? convertMonthValue(month) : calendar.get(Calendar.MONTH);
        this.year  = (year != null)  ? year  : calendar.get(Calendar.YEAR);
        calendar.set(this.year, this.month, 1);
        minimalWeekDayNumber = calendar.getActualMinimum(Calendar.DAY_OF_WEEK);
        maximalWeekDayNumber = calendar.getActualMaximum(Calendar.DAY_OF_WEEK);
    }

    private String format(Integer value) {
        return value != null ? String.format("%1$" + offset + "d", value) : "  ";
    }

    private void printMonthName() {
        if (printWeekNumber) {
            System.out.print("   ");
        }
        System.out.print("   ");
        System.out.println(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " +
            calendar.get(Calendar.YEAR));
    }

    private void printWeekNames() {
        if (printWeekNumber) {
            System.out.print("   ");
        }
        Map<String, Integer> dn = calendar.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        Map<Integer, String> dayNames = new TreeMap<Integer, String>();
        for (String key: dn.keySet()) {
            dayNames.put(dn.get(key), key);
        }
        for (int i = minimalWeekDayNumber; i <= maximalWeekDayNumber; i ++) {
            String curDayName = dayNames.get(i);
            curDayName = curDayName.substring(0, offset);
            System.out.print(((i == minimalWeekDayNumber) ? "" : " ") + curDayName);
        }
        System.out.println();
    }

    private void printTable() {
        int weeksInCurrentMonth = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        Integer[][] buffer = new Integer[weeksInCurrentMonth][DAYS_IN_WEEK];
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - minimalWeekDayNumber;
        int minDayNumber = calendar.getActualMinimum(Calendar.DATE);
        int maxDayNumber = calendar.getActualMaximum(Calendar.DATE);
        for (int date = minDayNumber; date <= maxDayNumber; date ++) {
            int position = firstDayOfWeek + date - minDayNumber;
            buffer[date / DAYS_IN_WEEK][date % DAYS_IN_WEEK] = date;
        }
        for (int i = 0; i < weeksInCurrentMonth; i ++) {
            if (printWeekNumber) {
                System.out.print(format(calendar.get(Calendar.WEEK_OF_YEAR) + i) + " ");
            }
            for (int j = 0; j < DAYS_IN_WEEK; j ++) {
                System.out.print(((j == 0) ? "" : " ") + format(buffer[i][j]));
            }
            System.out.println();
        }
    }

    void printTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E: YYYY.MM.dd HH:mm:ss z");
        if (timeZone != null) {
            simpleDateFormat.setTimeZone(timeZone);
        }
        System.out.println();
        System.out.println(simpleDateFormat.format(Calendar.getInstance().getTime()));
    }

    public void print() {
        printMonthName();
        printWeekNames();
        printTable();
        if (timeZone != null) {
            printTime();
        }
    }
}
