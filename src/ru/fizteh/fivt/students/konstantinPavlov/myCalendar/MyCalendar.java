package ru.fizteh.fivt.students.konstantinPavlov.myCalendar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyCalendar {

    static boolean flagW = false;
    static boolean flagT = false;
    static TimeZone timeZone = TimeZone.getDefault();
    static Calendar calendar = Calendar.getInstance();

    public static void main(String[] args) {
        setFlags(args);
        printMyCalendar();
    }

    public static void setFlags(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].charAt(0) == '-') {
                if (args[i].charAt(1) != 'w' && args.length == i + 1) {
                    System.err
                            .println("Error: incorrect input"
                                    + System.lineSeparator()
                                    + "Usage: java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                    System.exit(1);
                }

                switch (args[i]) {
                case "-m":
                    int month = Integer.parseInt(args[++i]) - 1;
                    if (calendar.getActualMinimum(Calendar.MONTH) > month
                            || calendar.getActualMaximum(Calendar.MONTH) < month) {
                        System.err
                                .println("Error: incorrect input"
                                        + System.lineSeparator()
                                        + "Usage: java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                        System.exit(1);
                    }
                    calendar.set(Calendar.MONTH, month);
                    break;

                case "-y":
                    i++;
                    int year = Integer.parseInt(args[i]);
                    if (calendar.getActualMinimum(Calendar.YEAR) > year
                            || calendar.getActualMaximum(Calendar.YEAR) < year) {
                        System.err
                                .println("Error: incorrect input"
                                        + System.lineSeparator()
                                        + "Usage: java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                        System.exit(1);
                    }
                    calendar.set(Calendar.YEAR, year);
                    break;

                case "-w":
                    flagW = true;
                    break;

                case "-t":
                    timeZone = TimeZone.getTimeZone(args[++i]);
                    if (timeZone.getID().equals("GMT")) {
                        System.err
                                .println("Error: incorrect input"
                                        + System.lineSeparator()
                                        + "Usage: java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                        System.exit(1);
                    }
                    calendar.setTimeZone(timeZone);
                    flagT = true;
                    break;

                default:
                    System.err
                            .println("Error: incorrect input"
                                    + System.lineSeparator()
                                    + "Usage: java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                    System.exit(1);
                }
            } else {
                System.err
                        .println("Error: incorrect input"
                                + System.lineSeparator()
                                + "Usage: java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                System.exit(1);
            }
        }
    }

    public static void printMyCalendar() {
        // printing header
        if (flagW) {
            System.out.print("\t\t\t");
        } else {
            System.out.print("\t\t");
        }
        System.out.print(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.getDefault()));
        System.out.print(" ");
        System.out.println(calendar.get(Calendar.YEAR));

        // printing days of week
        String[] daysOfWeek = new DateFormatSymbols().getShortWeekdays();
        if (flagW) {
            System.out.print("\t");
        }
        for (int i = 2; i < 8; ++i) {
            System.out.print(daysOfWeek[i]);
            System.out.print("\t");
        }
        System.out.println(daysOfWeek[1]);

        // printing main part
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        int firstDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (firstDay == 0) {
            firstDay = 7;
        }
        if (flagW) {
            System.out.print(calendar.get(Calendar.WEEK_OF_YEAR));
            System.out.print("\t");
        }
        for (int i = 0; i < firstDay - 1; ++i) {
            System.out.print("\t");
        }
        int ourMonth = calendar.get(Calendar.MONTH);
        int currentDay = 1;
        while (calendar.get(Calendar.MONTH) == ourMonth) {
            for (int i = firstDay; i <= 7
                    && ourMonth == calendar.get(Calendar.MONTH); ++i) {
                if (flagW && i == 1 && Calendar.WEEK_OF_MONTH != 1) {
                    System.out
                            .print(calendar.get(Calendar.WEEK_OF_YEAR) + "\t");
                }
                System.out.print(currentDay);
                if (i != 7) {
                    System.out.print("\t");
                }
                ++currentDay;
                calendar.set(Calendar.DAY_OF_MONTH, currentDay);
            }
            System.out.println();
            firstDay = 1;
        }

        // printing current time in our timezone
        if (flagT) {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            dateFormat.setTimeZone(timeZone);
            System.out.println();
            System.out.print("Now: ");
            System.out.print(dateFormat.format(date));
            System.out.print(" ");
            System.out.print(timeZone.getID());
        }
    }
}