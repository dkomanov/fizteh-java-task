package ru.fizteh.fivt.students.dmitriyBelyakov.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyCalendar {
    public static void main(String[] args) {
        boolean nOfWeek = false;
        boolean time = false;
        TimeZone timeZone = TimeZone.getDefault();
        int month = -1;
        int year = -1;
        try {
            for (int i = 0; i < args.length; ++i) {
                String arg = args[i];
                if (arg.equals("-w")) {
                    nOfWeek = true;
                } else if (arg.equals("-t")) {
                    time = true;
                    timeZone = TimeZone.getTimeZone(args[++i]);
                    if (!args[i].equals(timeZone.getID())) {
                        throw new RuntimeException("incorrect time zone.");
                    }
                } else if (arg.equals("-m")) {
                    try {
                        month = Integer.parseInt(args[++i]);
                        if (month < 1 || month > 12) {
                            throw new RuntimeException();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("not a month number");
                    }
                } else if (arg.equals("-y")) {
                    if (args[i + 1].length() != 4) {
                        throw new RuntimeException("not a year number");
                    }
                    try {
                        year = Integer.parseInt(args[++i]);
                    } catch (Exception e) {
                        throw new RuntimeException("not a year number");
                    }
                } else {
                    System.err.println("Use: java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                    System.exit(1);
                }
            }
            Calendar calendar = Calendar.getInstance();
            if (month != -1) {
                calendar.set(Calendar.MONTH, --month);
            }
            if (year != -1) {
                calendar.set(Calendar.YEAR, year);
            }
            calendar.setTimeZone(timeZone);
            printCalendar(calendar, timeZone, nOfWeek, time);
        } catch (Throwable t) {
            if (t.getLocalizedMessage() != null) {
                System.err.println("Error: " + t.getLocalizedMessage());
            } else {
                System.err.println("Error: unknown.");
            }
        }
    }

    private static void printCalendar(Calendar calendar, TimeZone timeZone, boolean nOfWeek, boolean time) {
        System.out.println("\t\t" + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " "
                + calendar.get(Calendar.YEAR));
        if (nOfWeek) {
            System.out.print("\t");
        }
        for (int i = calendar.getFirstDayOfWeek(); i < calendar.getFirstDayOfWeek() + 7; ++i) {
            System.out.print(getDayOfWeekName(i) + '\t');
        }
        System.out.println();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstNOfWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int maxWeekNum = calendar.getActualMaximum(Calendar.WEEK_OF_YEAR);
        if (nOfWeek) {
            System.out.print(firstNOfWeek + "\t");
            ++firstNOfWeek;
        }
        int firstDay = calendar.get(Calendar.DAY_OF_WEEK);
        if (firstDay - calendar.getFirstDayOfWeek() < 0) {
            firstDay += 7;
        }
        for (int i = 0; i < firstDay - calendar.getFirstDayOfWeek(); ++i) {
            System.out.print("\t");
        }
        for (int currDayInMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH),
                    lastDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); currDayInMonth <= lastDayInMonth;
                            ++currDayInMonth) {
            if ((currDayInMonth + firstDay - calendar.getFirstDayOfWeek() - 1) % 7 == 0) {
                System.out.println();
                if (nOfWeek) {
                    if (firstNOfWeek > maxWeekNum) {
                        firstNOfWeek = 1;
                    }
                    System.out.print(firstNOfWeek + "\t");
                    ++firstNOfWeek;
                }
            }
            System.out.print(currDayInMonth + "\t");
        }
        System.out.println();
        if (time) {
            System.out.println();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            simpleDateFormat.setTimeZone(timeZone);
            System.out.println("Now: " + simpleDateFormat.format(new Date().getTime())
                    + " " + timeZone.getDisplayName());
        }
    }

    public static String getDayOfWeekName(int dayNum) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayNum);
        String res = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        if (res.length() > 2) {
            return res.substring(0, 2);
        } else {
            return res;
        }
    }
}