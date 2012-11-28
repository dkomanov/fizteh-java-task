package ru.fizteh.fivt.students.tolyapro.formattedCalendar;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FormattedCalendar {

    static TimeZone timeZone;
    static int year;
    static int month;
    static boolean showWeekNumber;

    public static void parseArgs(String[] args) throws Exception {
        timeZone = null;
        month = 0;
        year = 0;
        showWeekNumber = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-y")) {
                try {
                    year = Integer.parseInt(args[++i]);
                    if (year <= 0) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    throw new Exception("Bad year");
                }
            } else if (args[i].equals("-m")) {
                try {
                    month = Integer.parseInt(args[++i]);
                    if (month < 1 || month > 12) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    throw new Exception("Bad month");
                }
            } else if (args[i].equals("-w")) {
                showWeekNumber = true;
            } else if (args[i].equals("-t")) {
                if (args.length == i + 1) {
                    throw new Exception("Bad timezone");
                }
                timeZone = TimeZone.getTimeZone(args[++i]);
                if (!args[i].equals(timeZone.getID())) {
                    throw new Exception("Bad timezone");
                }
            } else {
                throw new Exception(
                        "Usage: [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
            }
        }
    }

    public static void print(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // header :
        if (showWeekNumber) {
            System.out.print("\t");
        }
        System.out.println("\t\t"
                + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
                        Locale.getDefault()) + "\t"
                + calendar.get(Calendar.YEAR));
        // day of the week :
        String[] daysNames = new DateFormatSymbols().getShortWeekdays();
        if (showWeekNumber) {
            System.out.print("\t");
        }
        for (int i = 2; i < 8; ++i) {
            System.out.print(daysNames[i] + "\t");
        }
        System.out.println(daysNames[1]);
        // calendar
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (showWeekNumber) {
            System.out.print(currWeek++ + "\t");
        }
        // first spaces
        int spaces = calendar.get(Calendar.DAY_OF_WEEK)
                - calendar.getFirstDayOfWeek();
        if (spaces < 0) {
            spaces += 7;
        }
        for (int i = 0; i < spaces; ++i) {
            System.out.print("\t");
        }
        for (int currDay = 1; currDay <= maxDay; ++currDay) {
            if ((currDay + spaces - 1) % 7 == 0) {
                System.out.println("");
                if (showWeekNumber) {
                    int realCurrWeek = (currWeek++)
                            % (calendar.getActualMaximum(Calendar.WEEK_OF_YEAR) + 1);
                    if (realCurrWeek == 0) {
                        realCurrWeek++;
                        currWeek++;
                    }
                    System.out.print(realCurrWeek + "\t");
                }
            }
            System.out.print(currDay + "\t");
        }

        if (timeZone != null) {
            System.out.println("");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy.MM.dd HH:mm:ss ");
            simpleDateFormat.setTimeZone(timeZone);
            System.out.println("Now: "
                    + simpleDateFormat.format(new Date().getTime()) + " "
                    + timeZone.getDisplayName());
        }
    }

    public static void main(String[] args) {
        try {
            parseArgs(args);
        } catch (Exception e) {
            System.err.println("Incorrect input: " + e.getMessage());
            System.exit(1);
        }
        Calendar calendar = Calendar.getInstance();
        if (month > 0) {
            calendar.set(Calendar.MONTH, month - 1);
        }
        if (year != 0) {
            calendar.set(Calendar.YEAR, year);
        }
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        if (timeZone != null) {
            calendar.setTimeZone(timeZone);
        } else {
            calendar.setTimeZone(TimeZone.getDefault());
        }
        print(calendar);
    }

}
