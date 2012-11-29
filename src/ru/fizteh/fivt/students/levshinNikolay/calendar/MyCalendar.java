package ru.fizteh.fivt.students.levshinNikolay.calendar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.util.Calendar;

/**
 * Levshin Nikolay
 * MIPT FIVT 196
 */
public class MyCalendar {
    public static DateFormat date_format;
    public static TimeZone time_zone;
    public static Integer month;
    public static Integer year;
    public static boolean weeks_needed;
    public static Calendar calendar;
    public static String[] day_names = new DateFormatSymbols().getShortWeekdays();
    public static int[] day_indexes = new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
    public static int day_name_length;

    public static void init() {
        calendar = Calendar.getInstance();
        date_format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        time_zone = null;
        month = null;
        year = null;
        weeks_needed = false;
    }

    public static void UError() {
        System.out.println("Usage: java MyCalendar [-m MONTH] [-y YEAR] [-w] [t TIMEZONE]");
        System.exit(1);
    }

    public static void parser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                switch (args[i].charAt(1)) {
                    case 't':
                        if (args.length == i + 1 || time_zone != null) {
                            UError();
                        } else {
                            i++;
                            String[] tzNames = TimeZone.getAvailableIDs();
                            String TZName = args[i];
                            for (int j = 0; j < tzNames.length; ++j) {
                                if (TZName.equals(tzNames[j])) {
                                    time_zone = TimeZone.getTimeZone(TZName);
                                    calendar.setTimeZone(time_zone);
                                }
                            }
                            if (time_zone == null) {
                                System.err.println("Incorrect time zone.");
                                System.exit(1);
                            }

                        }
                        break;
                    case 'm':
                        i++;
                        month = Integer.parseInt(args[i]) - 1;
                        if (calendar.getActualMinimum(Calendar.MONTH) > month || calendar.getActualMaximum(Calendar.MONTH) < month) {
                            UError();
                        }
                        break;
                    case 'y':
                        i++;
                        year = Integer.parseInt(args[i]) - 1;
                        if (calendar.getActualMinimum(Calendar.YEAR) > year || calendar.getActualMaximum(Calendar.YEAR) < year) {
                            UError();
                        }
                        break;
                    case 'w':
                        weeks_needed = true;
                        break;
                    default:
                        UError();
                }
            } else {
                UError();
            }
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }

    }

    public static void printC() {
        calendar.set(year, month, 1);
        for (int i = 0; i < 7; ++i) {
            if (day_name_length < day_names[i].length()) {
                day_name_length = day_names[i].length();
            }
        }
        if (weeks_needed) {
            System.out.print("   ");
        }
        System.out.println("   " + DateFormatSymbols.getInstance().getMonths()[month] + ' ' + year);
        if (weeks_needed) {
            System.out.print("   ");
        }
        for (int dayIndex : day_indexes) {
            System.out.print(day_names[dayIndex]);
            for (int j = 0; j < day_name_length - day_names[dayIndex].length() + 1; ++j) {
                System.out.print(' ');
            }
        }
        if (day_name_length < 2) {
            day_name_length = 2;
        }
        System.out.println();
        int current_day = 1;
        int current_week = calendar.get(Calendar.WEEK_OF_YEAR);
        while (month == calendar.get(Calendar.MONTH)) {
            if (weeks_needed) {
                if (current_week < 10) {
                    System.out.print(" " + current_week + " ");
                } else {
                    System.out.print(current_week + " ");
                }
                current_week++;
            }

            for (int indx : day_indexes) {
                if (indx != calendar.get(Calendar.DAY_OF_WEEK) || month != calendar.get(Calendar.MONTH)) {
                    for (int i = 0; i < day_name_length + 1; ++i) {
                        System.out.print(' ');
                    }
                } else {
                    for (int i = 0; i < day_name_length - 2; ++i) {
                        System.out.print(' ');
                    }
                    if (current_day < 10) {
                        System.out.print(' ');
                    }
                    System.out.print(current_day + " ");
                    current_day++;
                    calendar.set(Calendar.DAY_OF_MONTH, current_day);
                }
            }
            System.out.println();
        }
    }

    public static void printCurrTime() {
        if (time_zone != null) {
            date_format.setTimeZone(time_zone);
            System.out.println();
            System.out.println("Now: " + date_format.format(new Date().getTime()) + ' ' + time_zone.getDisplayName());
        }
    }

    public static void main(String[] args) {
        init();
        parser(args);
        printC();
        printCurrTime();
    }
}


