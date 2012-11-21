
package ru.fizteh.fivt.students.mesherinIlya.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

public class MyCalendar {

    public static void main(String[] args) {
        
        boolean printTime = false;
        boolean printWeeks = false;
        Integer specifiedMonth = 0;
        Integer specifiedYear = 0;
        
        TimeZone timeZone = TimeZone.getDefault();
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-w")) {
                printWeeks = true;
            } else if (args[i].equals("-m") && i + 1 < args.length) {
                try {
                    specifiedMonth = Integer.parseInt(args[++i], 10);
                    if (specifiedMonth < 1 || specifiedMonth > 12) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.err.println("Invalid number of month. If must be integer from 1 to 12.");
                    System.exit(1);
                }
            
            } else if (args[i].equals("-y") && i + 1 < args.length) {
                try {
                    specifiedYear = Integer.parseInt(args[++i], 10);
                    if (specifiedYear < 1000 || specifiedYear > 9999) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.err.println("Invalid number of year. It must be integer from 1000 to 9999.");
                    System.exit(1);
                }
            
            
            
            } else if (args[i].equals("-t") && i + 1 < args.length) {
                try {
                    i++;
                    printTime = true;
                    String[] availableIDs = TimeZone.getAvailableIDs();
                    boolean good = false;
                    for (String s : availableIDs) {
                        if (s.equals(args[i])) {
                            good = true;
                            break;
                        }
                    }
                    if (!good) {
                        throw new Exception();
                    }
                    
                    timeZone = TimeZone.getTimeZone(args[i]);
                
                } catch (Exception e) {
                    System.err.println("Unknown time zone: \"" + args[i] + "\"");
                    System.exit(1);
                }
            
            } else {
                System.err.println("Using: MyCalendar [-w] [-m month] [-y year] [-t timezone]");
                System.exit(1);
            }  
        
        }


        
        Calendar current = Calendar.getInstance();        
              
        if (specifiedYear == 0) {
            specifiedYear = current.get(Calendar.YEAR);
        }
        
        if (specifiedMonth == 0) {
            specifiedMonth = current.get(Calendar.MONTH);
        } else {
            specifiedMonth--;
        }
        
        GregorianCalendar calendar = new GregorianCalendar(specifiedYear, specifiedMonth,
                current.get(Calendar.DAY_OF_MONTH));
        
        long offset = timeZone.getOffset(current.getTimeInMillis()) - 
                TimeZone.getDefault().getOffset(current.getTimeInMillis());
        
        calendar.add(Calendar.MILLISECOND, (int) offset);
        
                
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentMonth = calendar.get(Calendar.MONTH);
        
        if (printWeeks) {
            System.out.print("   ");
        }
        System.out.print("   ");
        System.out.print(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        System.out.print(" ");
        System.out.println(calendar.get(Calendar.YEAR));
        
        
        if (printWeeks) {
            System.out.print("   ");
        }
        
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        for (int i = 0; i < 7; i++) {
            System.out.print(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            System.out.print(" "); 
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
        
        calendar.set(Calendar.DAY_OF_WEEK, currentDayOfWeek);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        
        while (calendar.get(Calendar.DAY_OF_MONTH) != calendar.getActualMinimum(Calendar.DAY_OF_MONTH)) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        while (calendar.get(Calendar.DAY_OF_WEEK) != calendar.getFirstDayOfWeek()) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
            
        while (calendar.get(Calendar.MONTH) != currentMonth ||
                calendar.get(Calendar.DAY_OF_MONTH) != calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            
            if (calendar.get(Calendar.DAY_OF_WEEK) == calendar.getFirstDayOfWeek()) {
                System.out.print('\n');
                        
                if (printWeeks) {
                    if (calendar.get(Calendar.WEEK_OF_YEAR) < 10) {
                        System.out.print(" ");
                    }
                    System.out.print(calendar.get(Calendar.WEEK_OF_YEAR));
                    System.out.print(" ");
                }
            
            }
                
            if (calendar.get(Calendar.MONTH) != currentMonth) {
                System.out.print("   ");
            } else {
                if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
                    System.out.print(" ");
                }
                System.out.print(calendar.get(Calendar.DAY_OF_MONTH));
                System.out.print(" ");
            }
            
        
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        
        }       
        
        
        System.out.print('\n');
 
        if (printTime) {
            System.out.print("\nNow: ");
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");
            simpleDateFormat.setTimeZone(timeZone);
             
            System.out.print(simpleDateFormat.format(current.getTime()));
            System.out.println(timeZone.getDisplayName());
            
            
        }
        
    }

}
