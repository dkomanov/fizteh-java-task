package ru.fizteh.fivt.students.kashinYana.calendar;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: yana
 * Date: 20.10.12
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */

public class Calendar {
    static boolean isw = false;
    static boolean ist = false;
    static int numberMonth = -1;
    static String timeZone = "";
    static int numberYear = -1;
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if(args[i].equals("-m")) {
                numberMonth = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-w")) {
                isw = true;
            } else if (args[i].endsWith("-t")) {
                ist = true;
                timeZone = args[i + 1];
            } else if (args[i].equals("-y")) {
                numberYear = Integer.parseInt(args[i+1]);
            }
        }
        if (timeZone.equals("")) {
            timeZone = TimeZone.getDefault().getID();
        }
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
        Date curData = new Date();
        if (numberMonth == -1) {
            SimpleDateFormat ft = new SimpleDateFormat ("M");
            numberMonth = Integer.parseInt(ft.format(curData));
        }
        if (numberYear == -1) {
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy");
            numberYear = Integer.parseInt(ft.format(curData));
        }
        calendar.set(numberYear, numberMonth - 1, 1);
        if(isw) {
            System.out.printf("   ");
        }
        System.out.printf("   %tB %<tY\n", calendar.getTime());
        int idweek =  calendar.get(GregorianCalendar.WEEK_OF_YEAR);
        if(isw) {
            System.out.printf("   ");
        }
        System.out.println("Mo Tu We Th Fr Sa Su");
        int id = 0;
        if(isw) {
            System.out.printf("%2d ", idweek);
        }
        for (id = 0; id < firstDay(calendar); id++) {
            System.out.print("   ");
        }
        int numberDays = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        for (int i =  1; i <= numberDays; i++) {
            System.out.printf("%2d ",i);
            id++;
            if(id == 7) {
                id = 0;
                idweek++;
                System.out.print("\n");
                if(isw && i < numberDays) {
                    System.out.printf("%2d ", idweek);
                }

            }
        }
        System.out.println();
        System.out.println();
        if(isw) {
            System.out.printf("Now: %tY.%<tm.%<td %<tT ", calendar.getTime());
            System.out.println(calendar.getTimeZone().getID() + " Time");
        }
    }

    private static int firstDay(GregorianCalendar calendar) {
        SimpleDateFormat ft = new SimpleDateFormat ("E");
        String weekday = ft.format(calendar.getTime());
        if (weekday.equals("Mon")) {
            return 0;
        } else if(weekday.equals("Tue")) {
            return 1;
        } else if (weekday.equals("Wed")) {
            return 2;
        } else if (weekday.equals("Thu")) {
            return 3;
        }  else if (weekday.equals("Fri")) {
            return 4;
        }  else if (weekday.equals("Sat")) {
            return 5;
        }  else if (weekday.equals("Sun")){
            return 6;
        }
        return 7;
    }
}
