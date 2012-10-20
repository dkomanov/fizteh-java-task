package ru.fizteh.fivt.students.fedyuninV.calendarExample;


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
public class CalendarExample {
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


    private static void usageError() {
        System.out.println("Usage: java Calendar [-m MONTH] [-y YEAR] [-w] [t TIMEZONE]");
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
                            tz = TimeZone.getTimeZone(args[i]);
                            calendar.setTimeZone(tz);
                        }
                        break;
                    case 'm':
                        if (args.length == i + 1  ||  month != null) {
                            usageError();
                        } else {
                            i++;
                            month = Integer.parseInt(args[i]);
                            month--;
                            if(calendar.getActualMinimum(Calendar.MONTH) > month
                                    ||  calendar.getActualMaximum(Calendar.MONTH) < month) {
                                usageError();
                            }
                        }
                        break;
                    case 'y':
                        if (args.length == i + 1  ||  year != null) {
                            usageError();
                        } else {
                            i++;
                            year = Integer.parseInt(args[i]);
                            if(calendar.getActualMinimum(Calendar.YEAR) > year
                                    ||  calendar.getActualMaximum(Calendar.YEAR) < year) {
                                usageError();
                            }
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
            System.out.print("   ");
        }
        System.out.println("   " + DateFormatSymbols.getInstance().getMonths()[month] + " " + year);
        if (weeksNeeded) {
            System.out.print("   ");
        }
        String[] dayNames = new DateFormatSymbols().getShortWeekdays();
        for (int i = 1; i <= 7; i++) {
            System.out.print(dayNames[i % 7 + 1] + " ");
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
            System.out.println();
            System.out.println("Now: " + df.format(date) + " " + tz.getID() + " time");
        }
    }


    public static void main(String[] args) {
        init();
        parseOptions(args);
        printCalendar();
        printCurrentTime();
    }
}
