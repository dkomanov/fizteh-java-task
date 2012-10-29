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
    
    public static void printWhiteSpaces(int length) {
         for (int i = 0; i < length; i++) {
             System.out.print(" ");
         }
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
                        boolean find = false;
                        for (int j = 0; j < TimeZone.getAvailableIDs().length; ++j) {
                            if (args[i].equals(TimeZone.getAvailableIDs()[j])) {
                                find = true;
                                break;
                            }
                        }
                        
                        if (!find) {
                            System.err.println("Error: Unknown time zone");
                            System.exit(1);
                        }
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
        int length = days[1].length();
        
        for (int i = 2; i < 8; i++) {
            printWhiteSpaces(2 - length);
            System.out.print(days[i] + "  ");
        }
        printWhiteSpaces(2 - length);
        System.out.println(days[1]);

        int currentDay = 1;
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        boolean printCurrentWeek = false;
        
        if (printWeek) {
            if (currentWeek < 10) {
                System.out.print(" ");
            } 
            System.out.print(currentWeek + "  ");
            currentWeek++;
        }
        
        int k = 1;
        int emptyDays = calendar.get(Calendar.DAY_OF_WEEK);
        emptyDays--;
        if (emptyDays == 0) {
            emptyDays = 7;
        }
        
        k = emptyDays;
        while (emptyDays > 1) {
            printWhiteSpaces((length > 2 ? length : 2) + 2);
            emptyDays--;
        }
        
        while (month == calendar.get(Calendar.MONTH)) {
            if (printWeek && printCurrentWeek) {
                if (currentWeek < 10) {
                    System.out.print(" ");
                } 
                System.out.print(currentWeek + "  ");
                currentWeek++;
            } else {
                printCurrentWeek = true;
            }
            
            for (int j = k; j <= 7  &&  month == calendar.get(Calendar.MONTH); j++) {
                if (currentDay < 10) {
                    System.out.print(" ");
                }
                printWhiteSpaces(length - 2);
                System.out.print(currentDay + "  ");
                currentDay++;
                calendar.set(Calendar.DAY_OF_MONTH, currentDay);
            }
            k = 1;
            System.out.println();
        }
        
    }

    public static void main(String[] args) {
        try {
            readKeys(args);
            print();
            if (timeZone != null) {
                DateFormat dateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm:ss");
                dateFormat.setTimeZone(timeZone);
                System.out.println("\nNow: " +  dateFormat.format(date) + " " + timeZone.getID() + " time");
            }
        } catch (Exception expt) {
            System.err.println("Error: " + expt.getMessage());
            System.exit(1);
        }
    }

}
