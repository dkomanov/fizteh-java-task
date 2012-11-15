package ru.fizteh.fivt.students.alexanderKuzmin.calendar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;

/**
 * @author Alexander Kuzmin group 196 Class NewCalendar
 * 
 */

public class NewCalendar {

    private void executeCalendar(Calendar calendar, boolean printWeek,
            TimeZone tZone, boolean printZone) {
        StringBuilder output = new StringBuilder();
        output.append("\t\t")
                .append(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
                        Locale.getDefault())).append(" ")
                .append(calendar.get(Calendar.YEAR)).append("\n");
        if (printWeek) {
            output.append("\t");
        }
        String[] dayShortNames = new DateFormatSymbols().getShortWeekdays();
        for (int i = 1; i < 8; ++i) {
            output.append(dayShortNames[i % 7 + 1]).append("\t");
        }
        output.append("\n");
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        int firstDayOfMonth = (calendar.get(Calendar.DAY_OF_WEEK)
                - firstDayOfWeek + 7) % 7;
        int numberOfWeek = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
        if (printWeek) {
            output.append(++numberOfWeek).append("\t");
        }
        for (int i = 0; i < firstDayOfMonth; ++i) {
            output.append("\t");
        }
        int maxDayOfMonth = calendar.getActualMaximum(Calendar.DATE);
        for (int day = 1; day <= maxDayOfMonth; ++day) {
            output.append(day).append("\t");
            if ((day + firstDayOfMonth) % 7 == 0 && day != maxDayOfMonth) {
                output.append("\n");
                if (printWeek) {
                    output.append(++numberOfWeek).append("\t");
                }
            }
        }
        if (printZone) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            dateFormat.setTimeZone(tZone);
            Date date = new Date();
            output.append("\nNow: ").append(dateFormat.format(date))
                    .append(" ").append(tZone.getDisplayName());
        }
        System.out.println(output);
    }

    /**
     * @param args
     * [-m MONTH]  - display the calendar of this month (default is current). Numbers from 1 to 12.
     * [-y YEAR] - year (default is current). 4 digits.
     * [-w] - if specified, displays the number of the week.
     * [-t TIMEZONE] - if specified, under the calendar displays the current time in a specified time zone.
     */

    public static void main(String[] args) {
        NewCalendar myCalendar = new NewCalendar();
        TimeZone tZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance();
        boolean printWeek = false;
        boolean printZone = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-w")) {
                printWeek = true;
            } else if (args[i].equals("-m") && args.length >= i + 2) {
                try {
                    int month = Integer.parseInt(args[++i]);
                    if (month <= calendar.getActualMaximum(Calendar.MONTH) + 1
                            && month >= calendar
                                    .getActualMinimum(Calendar.MONTH) + 1) {
                        calendar.set(Calendar.MONTH, --month);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    Closers.printErrAndExit("Key '-m': Invalid argument. Use: [-m MONTH] , MONTH = {1, ..., 12}");
                }
            } else if (args[i].equals("-y") && args.length >= i + 2) {
                try {
                    int year = Integer.parseInt(args[++i]);
                    if (year >= 999 && year <= 10000) {
                        calendar.set(Calendar.YEAR, year);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    Closers.printErrAndExit("Key '-y': Invalid argument. Use: [-y YEAR] , YEAR = 4 digits.");
                }
            } else if (args[i].equals("-t") && args.length >= i + 2) {
                tZone = TimeZone.getTimeZone(args[++i]);
                if (tZone.getID().equals("GMT")) {
                    Closers.printErrAndExit("Invalid argument. Use: 'java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]'.");
                }
                printZone = true;
            } else {
                Closers.printErrAndExit("Invalid argument. Use: 'java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]'.");
            }
        }
        myCalendar.executeCalendar(calendar, printWeek, tZone, printZone);
    }
}