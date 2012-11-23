package ru.fizteh.fivt.students.frolovNikolay.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/*
 * Calendar. Фролов Николай, 196 группа.
 */
public class JavaCalendar {
    private static void incorrectArgumentHandler(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
    
    public static void main(String[] args) {
        
        // Обработка аргументов и их корректности.
        boolean isMonthSet = false;
        boolean isYearSet = false;
        boolean writeWeekNumb = false;
        boolean writeTimeInTimeZone = false;
        int month = 0;
        int year = 0;
        TimeZone timeZone = TimeZone.getDefault();
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
            case "-m":
                if (isMonthSet || i + 1 == args.length) {
                    incorrectArgumentHandler("Usage: java JavaCalendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                } else {
                    isMonthSet = true;
                    try {
                        month = Integer.valueOf(args[++i]);
                    } catch (Exception crush) {
                        System.err.println(crush.getMessage());
                        System.exit(1);
                    }
                    if (month < 1 || month > 12) {
                        incorrectArgumentHandler("Error. Month must be integer between 1 and 12.");
                    }
                }
                break;
            case "-y":
                if (isYearSet || i + 1 == args.length) {
                    incorrectArgumentHandler("Usage: java JavaCalendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                } else {
                    isYearSet = true;
                }
                try {
                    year = Integer.valueOf(args[++i]);
                } catch (Exception crush) {
                    System.err.println(crush.getMessage());
                    System.exit(1);
                }
                if (year < 1000 || year > 9999) {
                    incorrectArgumentHandler("Error. Year must be integer between 1000 and 9999.");
                }
                break;
            case "-w":
                if (writeWeekNumb) {
                    incorrectArgumentHandler("Usage: java JavaCalendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
                } else {
                    writeWeekNumb = true;
                }
                break;
            case "-t":
                if (writeTimeInTimeZone || i + 1 == args.length) {
                    incorrectArgumentHandler("Error. Incorrect argument.");
                } else {
                    writeTimeInTimeZone = true;
                    boolean hasCorrectTimeZone = false;
                    String[] timeZones = TimeZone.getAvailableIDs();
                    for (String iter : timeZones) {
                        if (iter.equals(args[i + 1])) {
                            timeZone = TimeZone.getTimeZone(args[++i]);
                            hasCorrectTimeZone = true;
                            break;
                        }
                    }
                    if (!hasCorrectTimeZone) {
                        incorrectArgumentHandler("Error. Incorrect time zone.");
                    }
                }
                break;
            default:
                incorrectArgumentHandler("Usage: java JavaCalendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]");
            }
        }
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        if (month != 0) {
            calendar.set(Calendar.MONTH, --month);
        }
        if (year != 0) {
            calendar.set(Calendar.YEAR, year);
        }
        
        // Печать необходимой информации.
        if (writeWeekNumb) {
            System.out.print("\t\t\t");
        } else {
            System.out.print("\t\t");
        }
        System.out.println(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                            + ' ' + calendar.get(Calendar.YEAR));
        if (writeWeekNumb) {
            System.out.print('\t');
        }
        printDaysOfWeek();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekNumb = calendar.get(Calendar.WEEK_OF_YEAR);
        if (calendar.get(Calendar.DAY_OF_WEEK) != 0) {
            if (writeWeekNumb) {
                System.out.print(weekNumb++ + "\t");
            }
            int add = calendar.get(Calendar.DAY_OF_WEEK) < calendar.getFirstDayOfWeek() ? 7 : 0;
            for (int i = calendar.getFirstDayOfWeek(); i < calendar.get(Calendar.DAY_OF_WEEK) + add; ++i) {
                System.out.print('\t');
            }
        }
        for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); ++i) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            if (calendar.get(Calendar.DAY_OF_WEEK) == calendar.getFirstDayOfWeek()) {
                System.out.println();
                if (writeWeekNumb) {
                    System.out.print(weekNumb++ + "\t");
                }
            }
            System.out.print(i + "\t");
        }
        if (writeTimeInTimeZone) {
            System.out.println('\n');
            SimpleDateFormat timeOutput = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            timeOutput.setTimeZone(timeZone);
            System.out.println("Now: " + timeOutput.format(new Date().getTime())
                             + " " + timeZone.getDisplayName());
        }
    }
    
    private static void printDaysOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        for (int i = calendar.getFirstDayOfWeek(); i < calendar.getFirstDayOfWeek() + 7; ++i) {
            calendar.set(Calendar.DAY_OF_WEEK, i);
            if (i + 1 != calendar.getFirstDayOfWeek() + 7) {
                System.out.print(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + '\t');
            } else {
                System.out.println(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            }
        }
    }
}