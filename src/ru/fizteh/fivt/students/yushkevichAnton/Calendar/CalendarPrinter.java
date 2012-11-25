package ru.fizteh.fivt.students.yushkevichAnton.calendar;

import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarPrinter {
    public static void main(String[] args) {
        new CalendarPrinter().run(args);
    }

    Locale   locale    = Locale.getDefault(); 
    Calendar calendar  = Calendar.getInstance(locale);
    boolean  showWeeks = false;
    boolean  showTime  = false;
    TimeZone timeZone  = calendar.getTimeZone();

    int width = 7 * 2 + 6; 

    final int MAP_SIZE = 1000;
    char[][] map = new char[MAP_SIZE][MAP_SIZE]; 

    int startX = MAP_SIZE / 2, startY = MAP_SIZE / 2; 

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
            map[startX + pos + i][startY - 2] = s.charAt(i);
        }
    }

    void printWeekDays() {
        Calendar t = (Calendar) calendar.clone();
        for (int i = 0; i < 7; i++) {
            t.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + i);
            String s = t.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale);
            while (s.length() < 2) {
                s = " " + s;
            }
            for (int j = 0; j < 2; j++) {
                map[startX + i * 3 + j][startY - 1] = s.charAt(j);
            }
        }
    }

    void printSheet() {
        int curX = (calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek() + 7) % 7, curY = 0;
        Calendar t = (Calendar) calendar.clone();
        int num = 1;
        while (calendar.get(Calendar.MONTH) == t.get(Calendar.MONTH)) {
            String s = Integer.toString(num++);
            while (s.length() < 2) {
                s = " " + s;
            }
            for (int j = 0; j < s.length(); j++) {
                map[startX + curX * 3 + j][startY + curY] = s.charAt(j);
            }

            t.set(Calendar.DAY_OF_MONTH, t.get(Calendar.DAY_OF_MONTH) + 1);
            curX++;
            curY += curX / 7;
            curX %= 7;
        }
    }

    void printWeeks() {
        int curY = 0;
        Calendar t = (Calendar) calendar.clone();
        while (calendar.get(Calendar.MONTH) == t.get(Calendar.MONTH)) {
            String s = Integer.toString(t.get(Calendar.WEEK_OF_YEAR));
            while (s.length() < 2) {
                s = " " + s;
            }
            for (int j = 0; j < s.length(); j++) {
                map[startX - 3 + j][startY + curY] = s.charAt(j);
            }

            t.set(Calendar.DAY_OF_MONTH, t.get(Calendar.DAY_OF_MONTH) + 7);
            curY++;
        }
    }

    void printTime() {
        System.out.println();
        System.out.print("Now: ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ", locale);
        simpleDateFormat.setTimeZone(timeZone);
        System.out.print(simpleDateFormat.format(Calendar.getInstance(timeZone, locale).getTime()));
        System.out.println(timeZone.getDisplayName(locale));
    }

    void printAll() {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                if (map[x][y] != 0) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (map[x][y] == 0) {
                    System.out.print(" ");
                } else {
                    System.out.print(map[x][y]);
                }
            }
            System.out.println();
        }
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

