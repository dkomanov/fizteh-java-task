package ru.fizteh.fivt.students.myhinMihail;

import java.text.*;
import java.util.*;

public class MonthCalendar {
    public static Calendar calendar = Calendar.getInstance();
    public static Date date = new Date();
    public static TimeZone timeZone = null;
    public static int month = -1;
    public static int year = -1;
    public static boolean printWeek = false;
    
    public static void printUsageAndExit() {
        System.err.println("Error: Incorrect input");
        System.err.println("Usage: [-m MONTH] [-y YEAR] [-w] [t TIMEZONE]");
        System.exit(1);
    }
    
    public static void readKeys(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].charAt(0) == '-') {
                
                if (args[i].length() == 1) {
                    System.err.println("Error: empty key");
                    continue;
                }
                
                if (args[i].charAt(1) != 'w' && args.length == i + 1) {
                    printUsageAndExit();
                }
                
                switch (args[i].charAt(1)) {
                    case 'm':
                        i++;
                        month = Integer.parseInt(args[i]) - 1;
                        if(calendar.getActualMinimum(Calendar.MONTH) > month
                            || calendar.getActualMaximum(Calendar.MONTH) < month) {
                            printUsageAndExit();
                        }
                        break;
                    
                    case 'y':
                        i++;
                        year = Integer.parseInt(args[i]);
                        if(calendar.getActualMinimum(Calendar.YEAR) > year
                            || calendar.getActualMaximum(Calendar.YEAR) < year) {
                            printUsageAndExit();
                        }
                        break;
                        
                    case 'w':
                        printWeek = true;
                        break;
                        
                    case 't':
                        i++;
                        timeZone = TimeZone.getTimeZone(args[i]);
                        calendar.setTimeZone(timeZone);
                        break;
                    
                    default:
                        printUsageAndExit();
                }
            } else {
                printUsageAndExit();
            }
        }
        
        if (month == -1) {
            month = calendar.get(Calendar.MONTH);
        }
    
        if (year == -1) {
            year = calendar.get(Calendar.YEAR);
        }
        calendar.set(year, month, 1);
        
    }
    
    public static void print() {
        if (printWeek) {
            System.out.print("    ");
        }
        System.out.println("       " + DateFormatSymbols.getInstance().getMonths()[month] + " " + year);
        if (printWeek) {
            System.out.print("    ");
        }
        
        String[] days = new DateFormatSymbols().getShortWeekdays();
        for (int i = 2; i < 8; i++) {
            System.out.print(days[i] + "  ");
        }
        System.out.println(days[1] + "  ");
        
        int currentDay = 1;
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        
        int k = 1;
        int emptyDays = calendar.get(Calendar.DAY_OF_WEEK);
        emptyDays--;
        if (emptyDays == 0) {
            emptyDays = 7;
        }
        
        k = emptyDays;
        while (emptyDays > 1) {
            System.out.print("    ");
            emptyDays--;
        }
        
        while (month == calendar.get(Calendar.MONTH)) {
            if (printWeek) {
                if (currentWeek < 10) {
                    System.out.print(" ");
                } 
                System.out.print(currentWeek + "  ");
                currentWeek++;
            }
            
            for (int j = k; j <= 7  &&  month == calendar.get(Calendar.MONTH); j++) {
                if (currentDay < 10) {
                    System.out.print(" ");
                }
                System.out.print(currentDay + "  ");
                currentDay++;
                calendar.set(Calendar.DAY_OF_MONTH, currentDay);
            }
            k = 1;
            System.out.println();
        }
        
    }

    public static void main(String[] args) {
        readKeys(args);
        print();
        if (timeZone != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            dateFormat.setTimeZone(timeZone);
            System.out.println("\nNow: " +  dateFormat.format(date) + " " + timeZone.getID() + " time");
        }
    }

}
