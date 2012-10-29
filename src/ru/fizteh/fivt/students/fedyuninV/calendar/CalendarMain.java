package ru.fizteh.fivt.students.fedyuninV.calendar;


import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class CalendarMain {
    private static DateFormat df;
    private static TimeZone tz;
    private static Integer month;
    private static Integer year;
    private static boolean weeksNeeded;
    private static Calendar calendar;
    private static String[] dayNames = new DateFormatSymbols().getShortWeekdays();
    private static int[] dayIndexes = new int[] {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
    private static int dayNameLength;

    private static void init() {
        calendar = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
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
                            String potentialTZName = args[i];
                            for (int j = 0; j < timeZoneNames.length; j++) {
                                if (potentialTZName.equals(timeZoneNames[j])) {
                                    tz = TimeZone.getTimeZone(potentialTZName);
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
        for (int i = 0; i < 7; i++) {
            if (dayNameLength < dayNames[i].length()) {
                dayNameLength = dayNames[i].length();
            }
        }
        if (weeksNeeded) {
            System.out.print("   ");
        }
        System.out.println("   " + DateFormatSymbols.getInstance().getMonths()[month] + " " + year);
        if (weeksNeeded) {
            System.out.print("   ");
        }
        String langName = Locale.getDefault().getLanguage();
        if (langName == "ja"  ||  langName == "ko") {
            System.out.print(' ');
        }
        for (int dayIndex: dayIndexes) {
            System.out.print(dayNames[dayIndex]);
            for (int j = 0; j < dayNameLength - dayNames[dayIndex].length() + 1; j++) {
                System.out.print(' ');
            }
        }
        if (dayNameLength < 2) {
            dayNameLength = 2;
        }
        System.out.println();
        int currDay = 1;
        int currWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        while(month == calendar.get(Calendar.MONTH)) {
            if (weeksNeeded) {
                if (currWeek < 10) {
                    System.out.print(" " + currWeek + " ");
                } else {
                    System.out.print(currWeek + " ");
                }
                currWeek++;
            }
            for (int j: dayIndexes) {
                if (j != calendar.get(Calendar.DAY_OF_WEEK)  ||  month != calendar.get(Calendar.MONTH)) {
                    for (int i = 0; i < dayNameLength + 1; i++) {
                        System.out.print(" ");
                    }
                } else {
                    for (int i = 0; i < dayNameLength - 2; i++) {
                        System.out.print(' ');
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
        if (tz != null) {
            calendar.setTimeInMillis(new Date().getTime());
            df.setTimeZone(tz);
            System.out.println();
            int slashIndex = tz.getID().lastIndexOf('/');
            System.out.println("Now: " + df.format(calendar.getTime()) + " "
                    + tz.getID().substring(slashIndex + 1) + " time");
        }
    }


    public static void main(String[] args) {
        init();
        parseOptions(args);
        printCalendar();
        printCurrentTime();
    }
}