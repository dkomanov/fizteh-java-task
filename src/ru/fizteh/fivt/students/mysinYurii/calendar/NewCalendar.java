package ru.fizteh.fivt.students.mysinYurii.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class NewCalendar {
    static int monthNum;
    static int yearNum;
    static boolean toPrintWeek;
    static TimeZone timeZone;
    static boolean toPrintTime;
    
    static String format(int k) {
        String toFormat = Integer.toString(k);
        while (toFormat.length() < 3) {
            toFormat = toFormat + ' ';
        }
        return toFormat;
    }
    
    static String format(String s) {
        while (s.length() < 3) {
            s += ' ';
        }
        return s;
    }
    
    static void printData() {
        Calendar date = Calendar.getInstance(timeZone);
        date.set(Calendar.MONTH, monthNum);
        date.set(Calendar.YEAR, yearNum);
        date.set(Calendar.DAY_OF_MONTH, 1);
        System.out.print("   ");
        if (toPrintWeek) {
            System.out.print("   ");
        }
        System.out.println(date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + ' ' + date.get(Calendar.YEAR));
        if (toPrintWeek) {
            System.out.print("   ");
        }
        for (int i = date.getActualMinimum(Calendar.DAY_OF_WEEK); i <= date.getActualMaximum(Calendar.DAY_OF_WEEK); ++i) {
           date.set(Calendar.DAY_OF_WEEK, i + 1);
           System.out.print(format(date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())));
        }
        date.set(Calendar.MONTH, monthNum);
        date.set(Calendar.YEAR, yearNum);
        date.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println();
        int i1 = 2;
        int currDay = 1;
        int currWeek = date.get(Calendar.WEEK_OF_YEAR);
        if (toPrintWeek) {
            System.out.print(format(currWeek));
        }
        while (i1 < date.get(Calendar.DAY_OF_WEEK)) {
            ++i1;
            System.out.print("   ");
        }
        --i1;
        for (; i1 <= date.getActualMaximum(Calendar.DAY_OF_WEEK); ++i1) {
            System.out.print(format(currDay));
            ++currDay;
        }
        for (int i = date.getActualMinimum(Calendar.WEEK_OF_MONTH) + 1;  i < date.getActualMaximum(Calendar.WEEK_OF_MONTH); ++i) {
            System.out.println();
            ++currWeek;
            if (toPrintWeek) {
                System.out.print(format(currWeek));
            }
            for (int j = date.getActualMinimum(Calendar.DAY_OF_WEEK); j <= date.getActualMaximum(Calendar.DAY_OF_WEEK); ++j) {
                if (currDay <= date.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    System.out.print(format(currDay));
                }
                ++currDay;
            }
        }
        if (toPrintTime) {
            SimpleDateFormat outputDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            outputDate.setTimeZone(timeZone);
            outputDate.setCalendar(date);
            System.out.println();
            System.out.print("Now: ");
            System.out.print(outputDate.format(date.getTime()));
            System.out.print(' ');
            System.out.print(timeZone.getDisplayName());
        }
    }
    
	public static void main(String args[]){
	    Calendar dateFormat = Calendar.getInstance();
        monthNum = dateFormat.get(Calendar.MONTH);
        yearNum = dateFormat.get(Calendar.YEAR);
	    toPrintWeek = false;
	    timeZone = TimeZone.getDefault();
	    toPrintTime = false;
	    if (args.length == 0) {
	        /*System.out.println("-m: месяц за который нужно вывести календарь. Задается числами от 1 до 12");
	        System.out.println("-y: год за который нужно вывести календарь. Записывается 4 цифрами");
	        System.out.println("-w: вывести номер недели");
	        System.out.println("-t: выводит под календарем текущее время");*/
	        printData();
	        System.exit(0);
	    }
	    int i = 0;
		while (i < args.length) {
		    if (args[i].equals("-m")) {
		        ++i;
		        if (i < args.length) {
		            int tempMonthNum = 0;
		            try {
		                tempMonthNum = Integer.parseInt(args[i]);
		            } catch (Exception e) {
		                System.out.print("-m: " + args[i] + " isn't a number");
		                System.exit(1);
		            }
		            if ((tempMonthNum >= 1) && (tempMonthNum <= 12)) {
		                monthNum = tempMonthNum - 1;
		            } else {
		                System.out.println("-m: invalid argument");
		                System.exit(1);
		            }
		        } else {
		            System.out.println("-m: too few arguments");
		            System.exit(1);
		        }
		    } else if (args[i].equals("-y")) {
		        ++i;
		        if (i < args.length) {
		            int tempYearNum = 0;
		            try {
		                tempYearNum = Integer.parseInt(args[i]);
		            } catch (Exception e) {
		                System.out.print("-y: " + args[i] + " isn't a number");
		                System.exit(1);
		            }
		            if ((tempYearNum >= 1000) && (tempYearNum <= 9999)) {
		                yearNum = tempYearNum;
		            } else {
		                System.out.println("-y: invalid argument");
		                System.exit(1);
		            }
		        } else {
		            System.out.println("-y: too few arguments");
		            System.exit(1);
		        }
		    } else if (args[i].equals("-w")) {
		        toPrintWeek = true;
		    } else if (args[i].equals("-t")) {
		        toPrintTime = true;
		        ++i;
		        if (i < args.length) {
		            boolean available = false;
		            for (String s : TimeZone.getAvailableIDs()) {
		                if (s.equals(args[i])) {
		                    available = true;
		                    break;
		                }
		            }
		            if (available) {
		                timeZone = TimeZone.getTimeZone(args[i]);
		            } else {
		                System.out.println("-t: time zone not available");
		                System.exit(1);
		            }
		        } else {
		            System.out.println("-t: too few arguments");
		            System.exit(1);
		        }
		    } else {
		        System.out.println("Unknown comand: " + args[i]);
		    }
		    ++i;
		}
		printData();
	}
}