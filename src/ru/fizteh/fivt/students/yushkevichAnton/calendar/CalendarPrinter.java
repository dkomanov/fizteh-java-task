package ru.fizteh.fivt.students.yushkevichAnton.calendar;

import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarPrinter {
    public static void main(String[] args) {
        new CalendarPrinter().run(args);
    }

    Locale   locale    = Locale.getDefault(); // for testing
    Calendar calendar  = Calendar.getInstance(locale);
    boolean  showWeeks = false;
    boolean  showTime  = false;
    TimeZone timeZone  = calendar.getTimeZone();

    int width = 7 * 2 + 6; // default width of calendar

    TableCharMap map = new TableCharMap(); // special drawing map

    int monday = Calendar.MONDAY; // to force week to begin from monday

    void run(String[] arguments) {
        parseArguments(arguments);

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        printMonth();

        printWeekDays();

        printSheet();

        if (showWeeks) {
            printWeeks();
        }

        printAll();

        if (showTime) {
            printTime();
        }
    }

    void printMonth() {
        String s = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale) +
                   " " +
                   calendar.get(Calendar.YEAR);
        int pos = width / 2 - s.length() / 2;
        for (int i = 0; i < s.length(); i++) {
            map.put(pos + i, -2, s.charAt(i));
        }
    }

    void printWeekDays() {
        Calendar t = (Calendar) calendar.clone();
        for (int i = 0; i < 7; i++) {
            //t.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + i);
            t.set(Calendar.DAY_OF_WEEK, monday + i);
            String s = t.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale);
            s = makeGoodLooking(s);
            for (int j = 0; j < 2; j++) {
                map.put(i * 3 + j, -1, s.charAt(j));
            }
        }
    }

    void printSheet() {
        //int curX = (calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek() + 7) % 7, curY = 0;
        int curX = (calendar.get(Calendar.DAY_OF_WEEK) - monday + 7) % 7, curY = 0;
        Calendar t = (Calendar) calendar.clone();
        int num = 1;
        while (calendar.get(Calendar.MONTH) == t.get(Calendar.MONTH)) {
            String s = Integer.toString(num++);
            s = makeGoodLooking(s);
            for (int j = 0; j < s.length(); j++) {
                map.put(curX * 3 + j, curY, s.charAt(j));
            }

            t.set(Calendar.DAY_OF_MONTH, t.get(Calendar.DAY_OF_MONTH) + 1);
            curX++;
            curY += curX / 7;
            curX %= 7;
        }
    }

    String makeGoodLooking(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2 - s.length(); i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(s);
        return stringBuilder.toString();
    }

    void printWeeks() {
        int curY = 0;
        Calendar t = (Calendar) calendar.clone();
        t.setMinimalDaysInFirstWeek(7);
        t.setFirstDayOfWeek(1);
        while (calendar.get(Calendar.MONTH) == t.get(Calendar.MONTH)) {
            String s = Integer.toString(t.get(Calendar.WEEK_OF_YEAR));
            s = makeGoodLooking(s);
            for (int j = 0; j < s.length(); j++) {
                map.put(j - 3, curY, s.charAt(j));
            }

            t.set(Calendar.DAY_OF_MONTH, t.get(Calendar.DAY_OF_MONTH) + 7);
            curY++;
        }
    }

    void printTime() {
        System.out.print("Now: ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ", locale);
        simpleDateFormat.setTimeZone(timeZone);
        System.out.print(simpleDateFormat.format(Calendar.getInstance(timeZone, locale).getTime()));
        System.out.println(timeZone.getDisplayName(locale));
    }

    void printAll() {
        System.out.println(map);
    }

    void parseArguments(String[] arguments) {
        for (int i = 0; i < arguments.length; i++) {
            String arg = arguments[i];
            if (arg.equals("-m")) {
                try {
                    int month = Integer.parseInt(arguments[++i]);
                    if (month < 1 || month > 12) {
                        throw new IllegalArgumentException();
                    }
                    calendar.set(Calendar.MONTH, month - 1);
                } catch (Exception e) {
                    die("Wrong syntax.");
                }
                continue;
            }
            if (arg.equals("-y")) {
                try {
                    int year = Integer.parseInt(arguments[++i]);
                    if (year < 1000 || year > 9999) {
                        throw new IllegalArgumentException();
                    }
                    calendar.set(Calendar.YEAR, year);
                } catch (Exception e) {
                    die("Wrong syntax.");
                }
                continue;
            }
            if (arg.equals("-w")) {
                showWeeks = true;
                continue;
            }
            if (arg.equals("-t")) {
                try {
                    showTime = true;
                    timeZone = TimeZone.getTimeZone(arguments[++i]);
                    calendar.setTimeZone(timeZone);
                } catch (Exception e) {
                    die("Wrong syntax.");
                }
                continue;
            }
            die("Wrong syntax.");
        }
    }

    void die(String message) {
        System.err.println(message);
        System.exit(1);
    }
}