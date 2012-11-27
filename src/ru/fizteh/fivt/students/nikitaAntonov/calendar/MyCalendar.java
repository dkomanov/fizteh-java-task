package ru.fizteh.fivt.students.nikitaAntonov.calendar;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import ru.fizteh.fivt.students.nikitaAntonov.utils.OptionParser;
import ru.fizteh.fivt.students.nikitaAntonov.utils.OptionParser.IncorrectArgsException;

public class MyCalendar {

    private Options opts;

    public MyCalendar(Options o) {
        opts = o;
    }

    public void print() {
        print_header();
    }

    private void print_header() {

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
            for (int j = maxLength - daysOfWeekLengths[i]; j > 0; --j) {
                System.out.print(" ");
            }

            System.out.print(daysOfWeekNames[i]);

            System.out.print(" ");
        }
        
        System.out.println();

        opts.calendar.set(Calendar.DAY_OF_MONTH, 1);
        int currentDay = 1;
        int currentWeek = opts.calendar.get(Calendar.WEEK_OF_YEAR);

        if (opts.showWeeks) {
            String numOfWeek = String.valueOf(currentWeek);
            if (numOfWeek.length() == 1) {
                numOfWeek = " " + numOfWeek;
            }

            System.out.print(numOfWeek);
            System.out.print(" ");
        }

        int EmptyDays = opts.calendar.get(Calendar.DAY_OF_WEEK) - 2;
        for (; EmptyDays > 0; --EmptyDays) {
            for (int j = 0; j < maxLength; ++j) {
                System.out.print(" ");
            }
            System.out.print(" ");
        }

        while (opts.month == opts.calendar.get(Calendar.MONTH)) {

            while (currentWeek == opts.calendar.get(Calendar.WEEK_OF_YEAR)) {
                String number = String.valueOf(currentDay);
                
                for (int i = 0, e = maxLength - number.length(); i < e; ++i) {
                    System.out.print(" ");
                }
                System.out.print(number);
                System.out.print(" ");
            
                ++currentDay;
                opts.calendar.set(Calendar.DAY_OF_MONTH, currentDay);
            }
            
            currentWeek = opts.calendar.get(Calendar.WEEK_OF_YEAR);
            
            System.out.println();
            
            if (opts.month == opts.calendar.get(Calendar.MONTH)) {

                if (opts.showWeeks) {
                    String numOfWeek = String.valueOf(currentWeek);
                    if (numOfWeek.length() == 1) {
                        numOfWeek = " " + numOfWeek;
                    }

                    System.out.print(numOfWeek);
                    System.out.print(" ");
                }

            }
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

    public static void main(String[] args) throws IncorrectArgsException {

        MyCalendar cal = new MyCalendar(new Options(args));
        cal.print();
    }

    static class Options {
        public boolean showWeeks;
        public TimeZone timeZone;
        public Calendar calendar;
        public int month;
        public int year;

        public Options(String args[]) throws IncorrectArgsException {

            OptionParser parser = new OptionParser("m:y:wt:");
            parser.parse(args);

            calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);

            showWeeks = parser.has('w');

            if (parser.has('t')) {
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
