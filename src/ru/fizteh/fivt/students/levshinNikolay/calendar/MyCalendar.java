package ru.fizteh.fivt.students.levshinNikolay.calendar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.util.Calendar;

/**
 * Levshin Nikolay
 * MIPT FIVT 196
 */
public class MyCalendar {
    public static DateFormat dateFormat;
    public static TimeZone timeZone;
    public static Integer month;
    public static Integer year;
    public static boolean weeksNeeded;
    public static Calendar calendar;
    public static String[] dayNames = new DateFormatSymbols().getShortWeekdays();
    public static int[] dayIndexes = new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
    public static int lenghtNameDay;

    public static void init() {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        timeZone = null;
        month = null;
        year = null;
        weeksNeeded = false;
    }

    public static void uError() {
        System.out.println("Usage: java MyCalendar [-m MONTH] [-y YEAR] [-w] [t TIMEZONE]");
        System.exit(1);
    }

    public static void parser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                switch (args[i].charAt(1)) {
                    case 't':
                        if (args.length == i + 1 || timeZone != null) {
                            uError();
                        } else {
                            i++;
                            String[] timeZoneNames = TimeZone.getAvailableIDs();
                            String timeZoneName = args[i];
                            for (String timeZoneName1 : timeZoneNames) {
                                if (timeZoneName.equals(timeZoneName1)) {
                                    timeZone = TimeZone.getTimeZone(timeZoneName);
                                    calendar.setTimeZone(timeZone);
                                }
                            }
                            if (timeZone == null) {
                                System.err.println("Incorrect time zone.");
                                System.exit(1);
                            }

                        }
                        break;
                    case 'm':
                        i++;
                        if (args.length <= i || month != null) {
                            uError();
                        } else {
                            try {
                                month =  Integer.parseInt(args[i]) - 1;
                            } catch (NumberFormatException exception) {
                                System.err.println(exception.getMessage());
                                System.exit(1);
                            }
                        }
                        if (calendar.getActualMinimum(Calendar.MONTH) > month || calendar.getActualMaximum(Calendar.MONTH) < month) {
                            uError();
                        }
                        break;
                    case 'y':
                        i++;
                        try {
                        year = Integer.parseInt(args[i]);
                        } catch (NumberFormatException exeption) {
                            System.err.println(exeption.getMessage());
                            System.exit(1);
                        }
                        if (calendar.getActualMinimum(Calendar.YEAR) > year || calendar.getActualMaximum(Calendar.YEAR) < year) {
                            uError();
                        }
                        break;
                    case 'w':
                        weeksNeeded = true;
                        break;
                    default:
                        uError();
                }
            } else {
                uError();
            }
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }

    }

    public static void printC() {
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(year, month, 1);
        for (int i = 0; i < 7; ++i) {
            if (lenghtNameDay < dayNames[i].length()) {
                lenghtNameDay = dayNames[i].length();
            }
        }
        if (lenghtNameDay < 2) {
            lenghtNameDay = 2;
        }
        if (weeksNeeded) {
            System.out.print("   ");
        }
        System.out.println("   " + DateFormatSymbols.getInstance().getMonths()[month] + ' ' + year);
        if (weeksNeeded) {
            System.out.print("   ");
        }
        for (int dayIndex : dayIndexes) {
            System.out.print(dayNames[dayIndex]);
            for (int j = 0; j < lenghtNameDay - dayNames[dayIndex].length() + 1; ++j) {
                System.out.print(' ');
            }
        }
        System.out.println();
        int currentDay = 1;
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        while (month == calendar.get(Calendar.MONTH)) {
            if (weeksNeeded) {
                if (currentWeek < 10) {
                    System.out.print(" " + currentWeek + " ");
                } else {
                    System.out.print(currentWeek + " ");
                }
                currentWeek++;
            }

            for (int indx : dayIndexes) {
                if (indx != calendar.get(Calendar.DAY_OF_WEEK) || month != calendar.get(Calendar.MONTH)) {
                    for (int i = 0; i < lenghtNameDay + 1; ++i) {
                        System.out.print(' ');
                    }
                } else {
                    for (int i = 0; i < lenghtNameDay - 2; ++i) {
                        System.out.print(' ');
                    }
                    if (currentDay < 10) {
                        System.out.print(' ');
                    }
                    System.out.print(currentDay + " ");
                    currentDay++;
                    calendar.set(Calendar.DAY_OF_MONTH, currentDay);
                }
            }
            System.out.println();
        }
    }

    public static void printCurrTime() {
        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
            System.out.println();
            System.out.println("Now: " + dateFormat.format(new Date().getTime()) + ' ' + timeZone.getDisplayName());
        }
    }

    public static void main(String[] args) {
        init();
        parser(args);
        printC();
        printCurrTime();
    }
}


