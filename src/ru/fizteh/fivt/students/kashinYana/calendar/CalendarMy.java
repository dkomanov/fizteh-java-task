package ru.fizteh.fivt.students.kashinYana.calendar;

/**
 * Created with IntelliJ IDEA.
 * User: yana
 * Date: 29.10.12
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendarMy {
    static boolean isw = false;
    static boolean ist = false;
    static boolean isy = false;
    static boolean ism = false;
    static int numberMonth = -1;
    static String timeZone = "";
    static int numberYear = -1;
    static String[] dayNames;

    public static void main(String[] args) throws Exception {

        try {
            readKeys(args);
        } catch (Exception e) {
            System.err.println("Error in set keys");
            System.exit(1);
        }

        GregorianCalendar calendar = new GregorianCalendar();
        try {
            setFlags(calendar);
        } catch (Exception e) {
            System.err.println("Incorrect value keys");
            System.exit(1);
        }
        try {
            printCalendar(calendar);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void readKeys(String[] args) throws Exception {
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-m")) {
                numberMonth = Integer.parseInt(args[i + 1]);
                i += 2;
                ism = true;
            } else if (args[i].equals("-w")) {
                isw = true;
                i++;
            } else if (args[i].endsWith("-t")) {
                ist = true;
                timeZone = args[i + 1];
                i += 2;
            } else if (args[i].equals("-y")) {
                numberYear = Integer.parseInt(args[i + 1]);
                i += 2;
                isy = true;
            } else {
                throw new Exception("Unknown key.");
            }
        }
        if (timeZone.equals("")) {
            timeZone = TimeZone.getDefault().getID();
        }
    }

    private static void setFlags(GregorianCalendar calendar) throws Exception {
        String[] setTimeZones = TimeZone.getAvailableIDs();
        boolean variableTimeZone = false;
        for (int i = 0; i < setTimeZones.length; i++) {
            if (setTimeZones[i].equals(timeZone)) {
                variableTimeZone = true;
            }
        }
        if (variableTimeZone) {
            calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        } else {
            throw new Exception("unknown timezone");
        }
        Date curData = new Date();
        if (!ism) {
            SimpleDateFormat ft = new SimpleDateFormat("M");
            numberMonth = Integer.parseInt(ft.format(curData));
        }
        if (!isy) {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy");
            numberYear = Integer.parseInt(ft.format(curData));
        }
        if (numberMonth < 1 || numberMonth > 12 || numberYear > 9999 || numberYear < 1000) {
            throw new Exception("Incorrect value key.");
        }
        calendar.set(numberYear, numberMonth - 1, 1);
        dayNames = new DateFormatSymbols().getShortWeekdays();
    }

    private static void printCalendar(GregorianCalendar calendar) throws Exception {
        //печать заголовка
        if (isw) {
            System.out.printf("   ");
        }
        System.out.printf("   %tB %<tY\n", calendar.getTime());
        if (isw) {
            System.out.printf("   ");
        }
        for (int i = 1; i <= 7; i++) {
            for (int j = 0; j < 2; j++) {
                if (j < dayNames[i % 7 + 1].length()) {
                    System.out.print(dayNames[i % 7 + 1].charAt(j));
                } else {
                    System.out.print(" ");
                }
            }
            System.out.print(" ");
        }
        System.out.println();

        //печать начала первой недели
        int idWeek = calendar.get(GregorianCalendar.WEEK_OF_YEAR);
        if (isw) {
            System.out.printf("%2d ", idWeek);
        }
        int dayWeek = 0;
        for (dayWeek = 0; dayWeek < firstDay(calendar); dayWeek++) {
            System.out.print("   ");
        }

        //печать всего месяца
        int numberDays = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        for (int dayMonth = 1; dayMonth <= numberDays; dayMonth++) {
            System.out.printf("%2d ", dayMonth);
            dayWeek++;
            if (dayWeek == 7) {
                dayWeek = 0;
                idWeek++;
                System.out.print("\n");
                if (isw && dayMonth < numberDays) {
                    System.out.printf("%2d ", idWeek);
                }
            }
        }
        System.out.println();
        //печать текущей даты, если нужно
        if (ist) {
            printCurrentTime();
        }
    }

    private static int firstDay(GregorianCalendar calendar) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("E");
        String weekday = ft.format(calendar.getTime());
        for (int i = 1; i <= 7; i++) {
            if (dayNames[i % 7 + 1].equals(weekday)) {
                return i - 1;
            }
        }
        throw new Exception("Unknown month");
    }

    private static void printCurrentTime() {
        System.out.println();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("Y.M.d HH:mm:ss ");
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        simpleDateFormat.setTimeZone(tz);
        System.out.print("Now: " + simpleDateFormat.format(calendar.getTime()));
        int idSlesh = timeZone.lastIndexOf("/");
        System.out.println(timeZone.substring(idSlesh + 1) + " Time");
    }

}