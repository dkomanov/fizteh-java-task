package ru.fizteh.fivt.students.dmitriyBelyakov.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        for(int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if(arg.equals("-w")) {
                nOfWeek = true;
            } else if(arg.equals("-t")) {
                time = true;
                timeZone = TimeZone.getTimeZone(args[++i]);
            } else if(arg.equals("-m")) {
                try {
                    month = Integer.parseInt(args[++i]);
                    if(month < 1 || month > 12) {
                        throw new RuntimeException();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("not a month number");
                }
            } else if(arg.equals("-y")) {
                if(args[i + 1].length() != 4) {
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
        if(month == -1) {
            month = Calendar.getInstance().get(Calendar.MONTH);
            year = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            --month;
        }
        printCalendar(month, year, timeZone, nOfWeek, time);
        } catch (Throwable t) {
            if(t.getLocalizedMessage() != null) {
                System.err.println(t.getLocalizedMessage());
            } else {
                System.err.println("Error: unknown.");
            }
        }
    }

    private static void printCalendar(int month, int year, TimeZone timeZone, boolean nOfWeek, boolean time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        System.out.println("\t\t" + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " "
                + calendar.get(Calendar.YEAR));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        if (nOfWeek) {
            System.out.print("\t");
        }
        for (int i = calendar.getFirstDayOfWeek(); i < calendar.getFirstDayOfWeek() + 7; ++i) {
            System.out.print(getDayOfWeekName(i) + '\t');
        }
        System.out.println();
        int firstNOfWeek = calendar.get(Calendar.WEEK_OF_YEAR) - calendar.get(Calendar.WEEK_OF_MONTH);
        if (nOfWeek) {
            System.out.print(firstNOfWeek + "\t");
            ++firstNOfWeek;
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDay = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < firstDay - calendar.getFirstDayOfWeek(); ++i) {
            System.out.print("\t");
        }
        for (int i = 1; i <= 31; ++i) {
            System.out.print(i + "\t");
            if ((i + firstDay - calendar.getFirstDayOfWeek()) % 7 == 0) {
                System.out.println();
                if (nOfWeek && i != 31) {
                    System.out.print(firstNOfWeek + "\t");
                    ++firstNOfWeek;
                }
            }
        }
        System.out.println();
        if (time) {
            System.out.println();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            simpleDateFormat.setTimeZone(timeZone);
            System.out.println("Now: " + simpleDateFormat.format(Calendar.getInstance().getTime())
                    + " " + timeZone.getDisplayName(Locale.getDefault()));
        }
    }

    public static String getDayOfWeekName(int dayNum) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayNum);
        String res = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        if(res.length() > 2) {
            return res.substring(0, 2);
        } else {
            return res;
        }
    }
}