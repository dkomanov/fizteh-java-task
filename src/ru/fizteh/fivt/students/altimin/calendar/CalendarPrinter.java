package ru.fizteh.fivt.students.altimin.calendar;

import java.text.DateFormatSymbols;
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
    private int[] days = {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
    };

    private int getCorrectDayNumber(int day) {
        for (int i = 0; i < days.length; i ++) {
            if (days[i] == day) {
                return i;
            }
        }
        return -1;
    }

    private int convertMonthValue(int value) throws IllegalArgumentException {
        switch (value) {
            case 1:   return Calendar.JANUARY;
            case 2:   return Calendar.FEBRUARY;
            case 3:   return Calendar.MARCH;
            case 4:   return Calendar.APRIL;
            case 5:   return Calendar.MAY;
            case 6:   return Calendar.JUNE;
            case 7:   return Calendar.JULY;
            case 8:   return Calendar.AUGUST;
            case 9:   return Calendar.SEPTEMBER;
            case 10:  return Calendar.OCTOBER;
            case 11:  return Calendar.NOVEMBER;
            case 12:  return Calendar.DECEMBER;
            default: throw new IllegalArgumentException(value + " is not valid month number");
        }
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
        if (this.year < calendar.getMinimum(Calendar.YEAR)) {
            throw new IllegalArgumentException("Year should not exceed " + calendar.getMinimum(Calendar.YEAR));
        }
        if (this.year > calendar.getMaximum(Calendar.YEAR)) {
            throw new IllegalArgumentException("Year should not be greater than " + calendar.getMaximum(Calendar.YEAR));
        }
        calendar.set(this.year, this.month, 1);
        minimalWeekDayNumber = calendar.getActualMinimum(Calendar.DAY_OF_WEEK);
        maximalWeekDayNumber = calendar.getActualMaximum(Calendar.DAY_OF_WEEK);
    }

    private String format(Integer value) {
        return value != null ? String.format("%1$" + offset + "d", value) : "  ";
    }

    private String format(String value) {
        return value != null ? String.format("%1$" + offset + "s", value) : "  ";
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
        String[] dayNames = new DateFormatSymbols().getShortWeekdays();
        for (int i = 0; i < DAYS_IN_WEEK; i ++) {
            String curDayName = dayNames[days[i]];
            curDayName = curDayName.length() <= offset ? curDayName : curDayName.substring(0, offset);
            System.out.print(((i == 0) ? "" : " ") + format(curDayName));
        }
        System.out.println();
    }

    private void printTable() {
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int weeksInCurrentMonth = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
                - calendar.get(Calendar.WEEK_OF_MONTH) + 1;
        Integer[][] buffer = new Integer[weeksInCurrentMonth][DAYS_IN_WEEK];
        int firstDayOfWeek = getCorrectDayNumber(calendar.get(Calendar.DAY_OF_WEEK));
        int minDayNumber = calendar.getActualMinimum(Calendar.DATE);
        int maxDayNumber = calendar.getActualMaximum(Calendar.DATE);
        for (int date = minDayNumber; date <= maxDayNumber; date ++) {
            int position = firstDayOfWeek + date - minDayNumber;
            buffer[position / DAYS_IN_WEEK][position % DAYS_IN_WEEK] = date;
        }
        for (int i = 0; i < weeksInCurrentMonth; i ++) {
            if (printWeekNumber) {
                System.out.print(format(calendar.get(Calendar.WEEK_OF_YEAR)
                        - calendar.getActualMinimum(Calendar.WEEK_OF_YEAR) + 1 + i) + " ");
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
