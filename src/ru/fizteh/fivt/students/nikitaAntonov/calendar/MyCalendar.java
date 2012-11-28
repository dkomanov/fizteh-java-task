package ru.fizteh.fivt.students.nikitaAntonov.calendar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.fizteh.fivt.students.nikitaAntonov.utils.OptionParser;
import ru.fizteh.fivt.students.nikitaAntonov.utils.OptionParser.IncorrectArgsException;

public class MyCalendar {

    private Options opts;

    public MyCalendar(Options o) {
        opts = o;
    }

    private void print() {

        if (opts.showWeeks) {
            System.out.print("   ");
        }

        StringBuilder title = new StringBuilder();
        title.append(opts.calendar.getDisplayName(Calendar.MONTH,
                Calendar.LONG, Locale.getDefault()));
        title.append(" ");
        title.append(opts.calendar.get(Calendar.YEAR));

        StringBuilder daysOfWeek = new StringBuilder();

        String daysOfWeekNames[] = getDaysOfWeekNames();
        int daysOfWeekLengths[] = new int[daysOfWeekNames.length];
        int maxLength = -1;
        int sumOfLengths = 0;
        for (int i = 0, e = daysOfWeekNames.length; i < e; ++i) {
            if (daysOfWeekNames[i].length() == 1) {
                daysOfWeekNames[i] = " " + daysOfWeekNames[i];
            }
            daysOfWeekLengths[i] = daysOfWeekNames[i].length();
            if (daysOfWeekLengths[i] > maxLength) {
                maxLength = daysOfWeekLengths[i];
            }
            sumOfLengths += daysOfWeekLengths[i];
        }

        sumOfLengths += 6;

        if (title.length() < sumOfLengths) {
            for (int i = 1, e = (sumOfLengths - title.length()) / 2; i < e; ++i) {
                System.out.print(" ");
            }
        }
        System.out.println(title);

        if (opts.showWeeks) {
            System.out.print("   ");
        }

        for (int i = 0, e = daysOfWeekNames.length; i < e; ++i) {
            printStringWithAlignment(daysOfWeekNames[i], maxLength);

            System.out.print(" ");
        }

        System.out.println();

        opts.calendar.set(Calendar.DAY_OF_MONTH, 1);
        int currentDay = 1;
        int currentWeek = opts.calendar.get(Calendar.WEEK_OF_YEAR);

        if (opts.showWeeks) {
            printNumWithAlignment(currentWeek, 2);

            System.out.print(" ");
        }

        int emptyDays = opts.calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (emptyDays == 0) {
            emptyDays = 7;
        }
        --emptyDays;
        
        for (; emptyDays > 0; --emptyDays) {
            printStringWithAlignment("", maxLength);
            System.out.print(" ");
        }

        while (opts.month == opts.calendar.get(Calendar.MONTH)) {

            while (currentWeek == opts.calendar.get(Calendar.WEEK_OF_YEAR) && opts.month == opts.calendar.get(Calendar.MONTH)) {
                printNumWithAlignment(currentDay, maxLength);
                System.out.print(" ");

                ++currentDay;
                opts.calendar.set(Calendar.DAY_OF_MONTH, currentDay);
            }

            currentWeek = opts.calendar.get(Calendar.WEEK_OF_YEAR);

            System.out.println();

            if (opts.month == opts.calendar.get(Calendar.MONTH)) {

                if (opts.showWeeks) {
                    printNumWithAlignment(currentWeek, 2);
                    System.out.print(" ");
                }

            }
        }
        
        if (opts.timezoneWasSet) {
            DateFormat dateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm:ss");
            dateFormat.setTimeZone(opts.timeZone);
            System.out.println("\nNow: " +  dateFormat.format(new Date()) + " " + opts.timeZone.getID() + " time");
        }

    }

    private static String[] getDaysOfWeekNames() {
        String tmp[] = new DateFormatSymbols().getShortWeekdays();
        String result[] = new String[7];

        result[6] = tmp[1];
        for (int i = 0; i < 6; ++i) {
            result[i] = tmp[i + 2];
        }

        return result;
    }

    private static void printNumWithAlignment(int number, int places) {
        printStringWithAlignment(String.valueOf(number), places);
    }

    private static void printStringWithAlignment(String str, int places) {
        printSpaces(places - str.length());

        System.out.print(str);
    }

    private static void printSpaces(int number) {
        for (int i = 0; i < number; ++i) {
            System.out.print(" ");
        }
    }

    public static void main(String[] args) {
        MyCalendar cal = null;

        try {
            cal = new MyCalendar(new Options(args));
        } catch (IncorrectArgsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        cal.print();
    }

    static class Options {
        public boolean showWeeks;
        public TimeZone timeZone;
        public Calendar calendar;
        public int month;
        public int year;
        public boolean timezoneWasSet = false;

        public Options(String args[]) throws IncorrectArgsException {

            OptionParser parser = new OptionParser("m:y:wt:");
            parser.parse(args);

            calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);

            showWeeks = parser.has('w');

            if (parser.has('t')) {
                timezoneWasSet = true;
                String userTimeZone = parser.valueOf('t');
                timeZone = TimeZone.getTimeZone(userTimeZone);
                if (!timeZone.getID().equals(userTimeZone)) {
                    throw new OptionParser.IncorrectArgsException(
                            "Incorrect timezone");
                }
            } else {
                timeZone = TimeZone.getDefault();
            }

            calendar.setTimeZone(timeZone);

            if (parser.has('m')) {
                try {
                    month = Integer.parseInt(parser.valueOf('m'));
                } catch (NumberFormatException e) {
                    throw new OptionParser.IncorrectArgsException(
                            "Incorrect month");
                }

                --month;

                if (month < calendar.getActualMinimum(Calendar.MONTH)
                        || month > calendar.getActualMaximum(Calendar.MONTH)) {
                    throw new OptionParser.IncorrectArgsException(month
                            + " month. Are you crazy?");
                }

                calendar.set(Calendar.MONTH, month);
            } else {
                month = calendar.get(Calendar.MONTH);
            }

            if (parser.has('y')) {
                try {
                    year = Integer.parseInt(parser.valueOf('y'));
                } catch (NumberFormatException e) {
                    throw new OptionParser.IncorrectArgsException(
                            "Incorrect year");
                }

                if (year < calendar.getActualMinimum(Calendar.YEAR)
                        || year > calendar.getActualMaximum(Calendar.YEAR)) {
                    throw new OptionParser.IncorrectArgsException(
                            "Year out of range");
                }

                calendar.set(Calendar.YEAR, year);
            } else {
                year = calendar.get(Calendar.YEAR);
            }
        }
    }
}
