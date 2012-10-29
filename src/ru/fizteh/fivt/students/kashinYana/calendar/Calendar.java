package ru.fizteh.fivt.students.kashinYana.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * User: Yana Kashinskaya, 195 group.
 */

public class Calendar {

    static boolean isw = false;
    static boolean ist = false;
    static boolean isy = false;
    static boolean ism = false;
    static int numberMonth = -1;
    static String timeZone = "";
    static int numberYear = -1;

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
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
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
        System.out.println("Mo Tu We Th Fr Sa Su");

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
        if (isw) {
            System.out.println();
            System.out.printf("Now: %tY.%<tm.%<td %<tT ", calendar.getTime());
            int idSlesh = timeZone.lastIndexOf("/");
            System.out.println(timeZone.substring(idSlesh + 1) + " Time");
        }
    }

    private static int firstDay(GregorianCalendar calendar) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("E");
        String weekday = ft.format(calendar.getTime());
        if (weekday.equals("Mon")) {
            return 0;
        } else if (weekday.equals("Tue")) {
            return 1;
        } else if (weekday.equals("Wed")) {
            return 2;
        } else if (weekday.equals("Thu")) {
            return 3;
        } else if (weekday.equals("Fri")) {
            return 4;
        } else if (weekday.equals("Sat")) {
            return 5;
        } else if (weekday.equals("Sun")) {
            return 6;
        } else {
            throw new Exception("Unknown month");
        }
    }
}
