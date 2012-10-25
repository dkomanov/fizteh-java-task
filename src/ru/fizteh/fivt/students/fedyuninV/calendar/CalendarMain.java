package ru.fizteh.fivt.students.fedyuninV.calendar;


import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class CalendarMain {
    private static Date date;
    private static DateFormat df;
    private static TimeZone tz;
    private static Integer month;
    private static Integer year;
    private static boolean weeksNeeded;
    private static Calendar calendar;

    private static void init() {
        calendar = Calendar.getInstance();
        date = new Date();
        df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        tz = null;
        month = null;
        year = null;
        weeksNeeded = false;
    }


    private static Integer parseOrExit(String[] args, int index, Integer data) {
        if(args.length <= index  ||  data != null) {
            usageError();
        } else {
            try {
                return Integer.parseInt(args[index]);
            } catch (NumberFormatException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
        return null;
    }


    private static void usageError() {
        System.out.println("Usage: java CalendarMain [-m MONTH] [-y YEAR] [-w] [t TIMEZONE]");
        System.exit(1);
    }

    private static void parseOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                switch (args[i].charAt(1)) {
                    case 't':
                        if (args.length == i + 1  ||  tz != null) {
                            usageError();
                        } else {
                            i++;
                            String[] timeZoneNames = TimeZone.getAvailableIDs();
                            for (int j = 0; j < timeZoneNames.length; j++) {
                                if (args[i].equals(timeZoneNames[j])) {
                                    tz = TimeZone.getTimeZone(args[i]);
                                    calendar.setTimeZone(tz);
                                }
                            }
                            if (tz == null) {
                                System.err.println("Incorrect TimeZone name.");
                                System.exit(1);
                            }
                        }
                        break;
                    case 'm':
                        i++;
                        month = parseOrExit(args, i, month);
                        month--;
                        if(calendar.getActualMinimum(Calendar.MONTH) > month
                                ||  calendar.getActualMaximum(Calendar.MONTH) < month) {
                            usageError();
                        }
                        break;
                    case 'y':
                        i++;
                        year = parseOrExit(args, i, year);
                        if(calendar.getActualMinimum(Calendar.YEAR) > year
                                ||  calendar.getActualMaximum(Calendar.YEAR) < year) {
                            usageError();
                        }
                        break;
                    case 'w':
                        weeksNeeded = true;
                        break;
                    default:
                        usageError();
                }
            } else {
                usageError();
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
        calendar.set(year, month, 1);
        if (weeksNeeded) {
            System.out.print("    ");
        }
        System.out.println("   " + DateFormatSymbols.getInstance().getMonths()[month] + " " + year);
        if (weeksNeeded) {
            System.out.print("    ");
        }
        String[] dayNames = new DateFormatSymbols().getShortWeekdays();
        for (int i = 1; i <= 7; i++) {
            System.out.print(dayNames[i % 7 + 1]);
            for (int j = 0; j < 4 - dayNames[i % 7 + 1].length(); j++) {
                System.out.print(' ');
            }
        }
        System.out.println();
        int currDay = 1;
        int currWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        while(month == calendar.get(Calendar.MONTH)) {
            if (weeksNeeded) {
                if (currWeek < 10) {
                    System.out.print(" " + currWeek + "  ");
                } else {
                    System.out.print(currWeek + "  ");
                }
                currWeek++;
            }
            for (int j = 2; j <= 8  &&  month == calendar.get(Calendar.MONTH); j++) {
                if (j == 8) {
                    j = 1;
                }
                if (j < calendar.get(Calendar.DAY_OF_WEEK)) {
                    System.out.print("    ");
                } else {
                    if (currDay < 10) {
                        System.out.print(" ");
                    }
                    System.out.print(currDay + "  ");
                    currDay++;
                    calendar.set(Calendar.DAY_OF_MONTH, currDay);
                }
                if (j == 1) {
                    j = 8;
                }
            }
            System.out.println();
        }
    }


    private static void printCurrentTime() {
        if (tz != null) {
            calendar.setTimeZone(tz);
            calendar.setTimeInMillis(new Date().getTime());
            Calendar temp = Calendar.getInstance();
            temp.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            temp.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            temp.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
            temp.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            temp.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            temp.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
            temp.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND));
            System.out.println();
            System.out.println("Now: " + df.format(temp.getTime()) + " " + tz.getID() + " time");
        }
    }


    public static void main(String[] args) {
        init();
        parseOptions(args);
        printCalendar();
        printCurrentTime();
    }
}