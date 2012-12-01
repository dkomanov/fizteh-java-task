package ru.fizteh.fivt.students.khusaenovTimur.calendar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Timur
 * Date: 29.11.12
 * Time: 15:19
 */

public class MyCalendar {
    private static Locale myLocale = Locale.getDefault();
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private static TimeZone timeZone = null;
    private static Integer month = null;
    private static Integer year = null;
    private static boolean weeksNeeded = false;
    private static Calendar calendar = Calendar.getInstance(myLocale);
    private static String[] dayNames = new DateFormatSymbols(myLocale).getShortWeekdays();
    private static int dayNameLength;
    private static int[] dayIndexes = new int[]{
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
    };


    private static void getMonth(String[] args, int index) {
        if (args.length <= index || month != null) {
            usageError("Incorrect arguments. Usage: [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
        } else {
            try {
                month =  Integer.parseInt(args[index]) - 1;
            } catch (NumberFormatException exception) {
                System.err.println(exception.getMessage());
                System.exit(1);
            }
        }
        if (calendar.getActualMinimum(Calendar.MONTH) > month || calendar.getActualMaximum(Calendar.MONTH) < month) {
            usageError("Incorrect arguments. Invalid month.");
        }
    }

    private static void getYear(String[] args, int index) {
        if (args.length <= index || year != null) {
            usageError("Incorrect arguments. Usage: [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
        } else {
            try {
                year =  Integer.parseInt(args[index]);
            } catch (NumberFormatException exception) {
                System.err.println(exception.getMessage());
                System.exit(1);
            }
        }
        if (calendar.getActualMinimum(Calendar.YEAR) > year || calendar.getActualMaximum(Calendar.YEAR) < year) {
            usageError("Incorrect arguments. Invalid year.");
        }
    }

    private static void getTimeZone(String[] args, int index) {
        String[] timeZoneNames = TimeZone.getAvailableIDs();
        String givenTimeZoneName = args[index];
        for (String timeZoneName : timeZoneNames) {
            if (givenTimeZoneName.equals(timeZoneName)) {
                timeZone = TimeZone.getTimeZone(givenTimeZoneName);
                calendar.setTimeZone(timeZone);
            }
        }
        if (timeZone == null) {
            usageError("Irrcorect TimeZone name.");
        }
    }

    private static void usageError(String string) {
        System.out.println(string);
        System.exit(1);
    }

    private static void parseOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                switch (args[i].charAt(1)) {
                    case 't':
                        if (args.length == i + 1 || timeZone != null) {
                            usageError("Incorrect arguments. Invalid TimeZone.");
                        } else {
                            i++;
                            getTimeZone(args, i);
                        }
                        break;
                    case 'm':
                        i++;
                        getMonth(args, i);
                        break;
                    case 'y':
                        i++;
                        getYear(args, i);
                        break;
                    case 'w':
                        weeksNeeded = true;
                        break;
                    default:
                        usageError("Incorrect arguments. Usage: [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                }
            } else {
                usageError("Incorrect arguments. Usage: [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
            }
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
    }


    private static void printCalendar() {
        for (int i = 0; i < 7; i++) {
            if (dayNameLength < dayNames[i].length()) {
                dayNameLength = dayNames[i].length();
            }
        }
        if (dayNameLength < 2) {
            dayNameLength = 2;
        }
        if (weeksNeeded) {
            System.out.print("   ");
        }
        System.out.println("   " + DateFormatSymbols.getInstance(myLocale).getMonths()[month] + " " + year);
        if (weeksNeeded) {
            System.out.print("   ");
        }
        for (int dayIndex : dayIndexes) {
            System.out.print(dayNames[dayIndex]);
            for (int j = 0; j < dayNameLength - dayNames[dayIndex].length() + 1; j++) {
                System.out.print(" ");
            }
        }
        System.out.println();
        int currDay = 1;
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(year, month, currDay);
        int currWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        while (month == calendar.get(Calendar.MONTH)) {
            if (weeksNeeded) {
                if (currWeek < 10) {
                    System.out.print(" ");
                }
                System.out.print(currWeek + " ");
                currWeek++;
            }
            for (int j : dayIndexes) {
                if (j != calendar.get(Calendar.DAY_OF_WEEK) || month != calendar.get(Calendar.MONTH)) {
                    for (int i = 0; i < dayNameLength + 1; i++) {
                        System.out.print(" ");
                    }
                } else {
                    for (int i = 0; i < dayNameLength - 2; i++) {
                        System.out.print(" ");
                    }
                    if (currDay < 10) {
                        System.out.print(" ");
                    }
                    System.out.print(currDay + " ");
                    currDay++;
                    calendar.set(Calendar.DAY_OF_MONTH, currDay);
                }
            }
            System.out.println();
        }
    }


    private static void printCurrentTime() {
        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
            System.out.println();
            System.out.println("Now: " + dateFormat.format(new Date().getTime()) + " " + timeZone.getDisplayName());
        }
    }


    public static void main(String[] args) {
        parseOptions(args);
        printCalendar();
        printCurrentTime();
    }
}
