package ru.fizteh.fivt.students.yuliaNikonova.calendar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyCalendar {

    public static Calendar mCalendar = Calendar.getInstance();
    public static Date date = new Date();
    public static TimeZone timeZone = null;
    public static int month = -1;
    public static int year = -1;
    public static boolean printWeek = false;
    public static DateFormatSymbols dfs;
    public static int currentDay;
    public static StringBuilder week;

    public static void main(String[] args) {
        mCalendar = Calendar.getInstance();
        dfs = new DateFormatSymbols(Locale.getDefault());
        month = mCalendar.get(Calendar.MONTH);
        year = mCalendar.get(Calendar.YEAR);
        week = new StringBuilder();
        parseArgs(args);
        printCalendar();
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-m")) {
                if (i == args.length - 1) {
                    exitError("wring use of -m");
                }
                month = Integer.parseInt(args[++i]) - 1;
                if (month > mCalendar.getActualMaximum(Calendar.MONTH) || month < mCalendar.getActualMinimum(Calendar.MONTH)) {
                    exitError("wrong number of month");
                }
                mCalendar.set(Calendar.MONTH, month);
            } else if (args[i].equals("-w")) {
                printWeek = true;
            } else if (args[i].equals("-y")) {
                if (i == args.length - 1) {
                    exitError("wring use of -y");
                }
                year = Integer.parseInt(args[++i]);
                if (year > mCalendar.getActualMaximum(Calendar.YEAR) || year < mCalendar.getActualMinimum(Calendar.YEAR)) {
                    exitError("wrong number of year");
                }
                mCalendar.set(Calendar.YEAR, year);

            } else if (args[i].equals("-t")) {
                if (i == args.length - 1) {
                    exitError("wring use of -t");
                }
                String timeZ = args[++i];
                timeZone = TimeZone.getTimeZone(timeZ);
                boolean exist = false;
                for (String tz : TimeZone.getAvailableIDs()) {
                    if (tz.equals(timeZ)) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    exitError("wrong timezone");
                }
                mCalendar.setTimeZone(timeZone);
            } else {
                System.err.println("unkonw key " + args[i]);
                help();
            }
        }
    }

    private static void help() {
        System.out.println("Usage: " + "java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
        System.exit(1);

    }

    private static void exitError(String message) {
        System.err.println(message);
        System.exit(1);

    }

    public static void printCalendar() {
        if (printWeek) {
            System.out.print("  ");
        }
        System.out.println("    " + dfs.getMonths()[month] + " " + year);
        String[] weekDays = dfs.getShortWeekdays();
        if (printWeek) {
            week.append("   ");
        }
        for (int i = 2; i < weekDays.length; i++) {
            week.append(weekDays[i]);
            week.append(" ");
        }
        week.append(weekDays[1]);
        System.out.println(week);
        int length = weekDays[1].length();
        int firstDay = mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int lastDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        mCalendar.set(Calendar.DAY_OF_MONTH, firstDay);
        int emptyDays = mCalendar.get(Calendar.DAY_OF_WEEK);
        if (emptyDays > 1) {
            emptyDays -= 2;
        } else {
            emptyDays = 6;
        }
        currentDay = firstDay;
        week.delete(0, Integer.MAX_VALUE);
        if (printWeek) {
            appendWeek();
        }
        week.append(emptyString((length + 1) * emptyDays));

        for (int i = emptyDays + 1; i < 8; i++) {
            appendDay(length);
        }

        System.out.println(week);

        while (currentDay <= lastDay) {
            mCalendar.set(Calendar.DAY_OF_MONTH, currentDay);
            week.delete(0, Integer.MAX_VALUE);
            if (printWeek) {
                appendWeek();
            }
            for (int i = 0; i < 7; i++) {
                if (currentDay > lastDay) {
                    break;
                }
                appendDay(length);
            }

            System.out.println(week);
        }

        if (timeZone != null) {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat();
            dateFormat.setTimeZone(timeZone);
            System.out.print("Now: " + dateFormat.format(date) + " " + timeZone.getDisplayName(Locale.getDefault()));
        }

    }

    private static void appendWeek() {
        week.append(mCalendar.get(Calendar.WEEK_OF_YEAR));
        if (String.valueOf(mCalendar.get(Calendar.WEEK_OF_YEAR)).length() < 2) {
            week.append(" ");
        }
        week.append(" ");
    }

    private static void appendDay(int length) {
        if (String.valueOf(currentDay).length() < length) {
            week.append(emptyString(length - String.valueOf(currentDay).length()));
        }
        week.append(currentDay);
        week.append(" ");
        currentDay++;
    }

    public static String emptyString(int length) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            str.append(" ");
        }
        return str.toString();
    }
}
