package ru.fizteh.fivt.students.verytable.calendar;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SimpleCalendar {

    static void printCalendar(Calendar calendar, boolean shouldPrintTimeZone,
                              TimeZone timeZone, boolean shouldPrintWeedsNumber) {
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        String smallIndent = "  ";
        String bigIndent = "    ";

        System.out.print(bigIndent);
        if (shouldPrintWeedsNumber) {
            System.out.print(bigIndent);
        }

        System.out.print(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
                         Locale.getDefault()) + smallIndent);
        System.out.println(calendar.get(Calendar.YEAR));

        String[] shortWeekdays = new DateFormatSymbols().getShortWeekdays();
        if (shouldPrintWeedsNumber) {
            System.out.print(bigIndent);
        }

        for (int i = 1; i < 8; ++i) {
            System.out.print(shortWeekdays[i % 7 + 1] + smallIndent);
        }
        System.out.print("\n");

        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
        if (shouldPrintWeedsNumber) {
            if (weekOfYear < 10) {
                System.out.print(" " + weekOfYear + smallIndent);
            } else {
                System.out.print(weekOfYear + smallIndent);
            }
            ++weekOfYear;
        }

        int curDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) - 1);
        if (curDayOfWeek == 0) {
            curDayOfWeek = 7;
        }
        for (int i = 1; i < curDayOfWeek; ++i) {
            System.out.print(bigIndent);
        }

        int curDay = 1;
        int maxDayNumber = calendar.getActualMaximum(Calendar.DATE);
        while (curDay <= maxDayNumber) {
            while (curDayOfWeek < 8 && curDay <= maxDayNumber) {
                if (curDay < 10) {
                    System.out.print(" " + curDay + smallIndent);
                } else {
                    System.out.print(curDay + smallIndent);
                }
                ++curDay;
                ++curDayOfWeek;
            }
            System.out.print("\n");
            if (shouldPrintWeedsNumber && (curDay - 1 != maxDayNumber)) {
                if (weekOfYear < 10) {
                    System.out.print(" " + weekOfYear + smallIndent);
                } else {
                    System.out.print(weekOfYear + smallIndent);
                }
                ++weekOfYear;
            }
            curDayOfWeek = 1;
        }

        if (shouldPrintTimeZone) {
            System.out.print("\nNow: ");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");
            simpleDateFormat.setTimeZone(timeZone);
            System.out.print(simpleDateFormat.format(calendar.getTimeInMillis()));
            System.out.println(timeZone.getDisplayName());
        }
    }

    public static void main(String[] args) {

        boolean shouldPrintWeeksNumber = false;
        boolean shouldPrintTImeZone = false;
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-m":
                    if (i + 1 < args.length) {
                        int month = Integer.parseInt(args[i + 1]);
                        if (1 <= month && month <= 12) {
                            calendar.set(Calendar.MONTH, month - 1);
                        } else {
                            System.err.println("Month must be in [1, 12]");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("No month declared after -m.");
                        System.exit(1);
                    }
                    ++i;
                    break;
                case "-y":
                    if (i + 1 < args.length) {
                        int year = Integer.parseInt(args[i + 1]);
                        if (1000 <= year && year <= 9999) {
                            calendar.set(Calendar.YEAR, year);
                        } else {
                            System.err.println("Year must be in [1000, 9999]");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("No year declared after -y.");
                        System.exit(1);
                    }
                    ++i;
                    break;
                case "-w":
                    shouldPrintWeeksNumber = true;
                    break;
                case "-t":
                    shouldPrintTImeZone = true;
                    if (i + 1 < args.length) {
                        TimeZone givenTimeZone = TimeZone.getTimeZone(args[i + 1]);
                        if (givenTimeZone.getID().equals("GMT")) {
                            System.err.println("Unavailable time zone: "
                                               + givenTimeZone);
                            System.exit(1);
                        }
                        timeZone = givenTimeZone;
                    } else {
                        System.err.println("No time zone declared after -t.");
                        System.exit(1);
                    }
                    ++i;
                    break;
                default:
                    System.err.println("Unknown argument " + args[i]);
                    System.exit(1);
            }
        }

        printCalendar(calendar, shouldPrintTImeZone, timeZone, shouldPrintWeeksNumber);
    }
}
