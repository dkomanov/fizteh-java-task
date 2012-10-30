package ru.fizteh.fivt.students.almazNasibullin.calendar;

import java.util.StringTokenizer;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 30.10.12
 * @author almaz
 */

public class MyCalendar {

    public static void main(String[] args) {
        // месяц
        WrapperPrimitive<Integer> month = new WrapperPrimitive<Integer>(-1);

        // год
        WrapperPrimitive<Integer> year = new WrapperPrimitive<Integer>(-1);

        // номер недели
        WrapperPrimitive<Boolean> weak = new WrapperPrimitive<Boolean>(false);

        // временная зона
        WrapperPrimitive<String> timeZone = new WrapperPrimitive<String>("");

        readArguments(args, month, year, weak, timeZone);

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = null;

        if (month.t > -1) {
            // в этом блоке проверяется, текущий день месяца есть ли в выбранном месяце,
            // если да, то день месяца не меняется, иначе указывается последний день
            // выбранного месяца
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, month.t -1);
            calendar.set(Calendar.DAY_OF_MONTH, Math.min(calendar.get(Calendar.DAY_OF_MONTH)
                    , c.getActualMaximum(Calendar.DAY_OF_MONTH)));
            calendar.set(Calendar.MONTH, month.t - 1);
        }
        if (year.t > -1) {
            calendar.set(Calendar.YEAR, year.t);
        }
        if (!timeZone.t.equals("")) {
            tz = TimeZone.getTimeZone(timeZone.t);
            calendar.setTimeZone(tz);
        }

        printCalendar(calendar, weak,timeZone);
    }

    public static void readArguments(String[] args, WrapperPrimitive<Integer> month,
            WrapperPrimitive<Integer> year, WrapperPrimitive<Boolean> weak,
            WrapperPrimitive<String> timeZone) {
        int length = args.length;

        if (length > 0) {
            StringBuilder sb = new StringBuilder(args[0]);
            for (int i = 1; i < length; ++i) {
                sb.append(" ");
                sb.append(args[i]);
            }

            StringTokenizer st = new StringTokenizer(sb.toString(), " \t");
            while (st.hasMoreTokens()) {
                String str = st.nextToken();
                if (str.equals("-m")) {
                    if (month.t == -1) {
                        if (st.hasMoreTokens()) {
                            try {
                                month.t = Integer.parseInt(st.nextToken());
                                if (!(month.t >= 1 && month.t <= 12)) {
                                    LoUtils.printErrorAndExit("Wrong number of the month");
                                }
                            } catch (Exception e) {
                                LoUtils.printErrorAndExit("Usage: [-m MONTH]");
                            }
                        } else {
                            LoUtils.printErrorAndExit("Usage: [-m MONTH]");
                        }
                    } else {
                        LoUtils.printErrorAndExit("You put number of the month several times");
                    }
                } else if (str.equals("-y")) {
                    if (year.t == -1) {
                        if (st.hasMoreTokens()) {
                            try {
                                year.t = Integer.parseInt(st.nextToken());
                                if (year.t < 0) {
                                    LoUtils.printErrorAndExit("Wrong year");
                                }
                            } catch (Exception e) {
                                LoUtils.printErrorAndExit("Usage: [-y YEAR]");
                            }
                        } else {
                            LoUtils.printErrorAndExit("Usage: [-y YEAR]");
                        }
                    } else {
                        LoUtils.printErrorAndExit("You put year several times");
                    }
                } else if (str.equals("-w")) {
                    if (!weak.t) {
                        weak.t = true;
                    } else {
                        LoUtils.printErrorAndExit("You put key '-w' several times");
                    }
                } else if (str.equals("-t")) {
                    if (timeZone.t.equals("")) {
                        if (st.hasMoreTokens()) {
                            timeZone.t = st.nextToken();
                        } else {
                            LoUtils.printErrorAndExit("Usage: [-t TIMEZONE]");
                        }
                    } else {
                        LoUtils.printErrorAndExit("You put the time zone several times");
                    }
                } else {
                    LoUtils.printErrorAndExit(str + ": bad command");
                }
            }
        }
    }

    public static void printCalendar(Calendar calendar, WrapperPrimitive<Boolean> weak,
            WrapperPrimitive<String> timeZone) {
        String[] months = {"January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"};

        String[] days = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};

        if (weak.t) {
            System.out.print("   ");
        }
        System.out.println("   " + months[calendar.get(Calendar.MONTH)] + " "
                + calendar.get(Calendar.YEAR)); // печатаем месяц и год
        if (weak.t) {
            System.out.print("   ");
        }
        for (int i = 0; i < days.length; ++i) {
            System.out.print(days[i] + " "); // печатаем дни недели
        }
        System.out.println();

        int weekOfYear = getWeekOfYear(calendar);
        int dayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = getDayOfWeek(calendar);

        if (weak.t) {
            if (weekOfYear <= 9) {
                System.out.print(" ");
            }
            System.out.print(weekOfYear + " ");
            if (calendar.get(Calendar.MONTH) == Calendar.JANUARY) {
                // если текущий месяц январь, то номер недели в году, начиная со второй,
                //  возможно придется поменять
                if (dayOfWeek > 1) {
                    weekOfYear = 0;
                } else {
                    weekOfYear = 1;
                }
            }
        }
        for (int i = 1; i < dayOfWeek; ++i) {
            System.out.print("   ");
        }
        while (dayOfMonth <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            // печатаем все дни месяца
            if (dayOfWeek == 8) { // новая неделя
                dayOfWeek = 1;
                System.out.println();
                 ++weekOfYear;
                if (weak.t) {
                    if (weekOfYear <= 9) {
                        System.out.print(" ");
                    }
                    System.out.print(weekOfYear + " ");
                }
            }
            if (dayOfMonth <= 9) {
                System.out.print(" ");
            }
            System.out.print(dayOfMonth + " ");
            ++dayOfMonth;
            ++dayOfWeek;
        }
        System.out.println();

        if (!timeZone.t.equals("")) { // печатаем дату и время в указанной временной зоне
            System.out.println();
            System.out.print("Now: " + calendar.get(Calendar.YEAR) + "." +
                    (calendar.get(Calendar.MONTH) + 1) + "." +
                    calendar.get(Calendar.DAY_OF_MONTH) + " ");
            System.out.print(calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                    calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
            System.out.println(" " + calendar.getTimeZone().getDisplayName());
        }
    }

    public static int getWeekOfYear(Calendar calendar) {
        // находит номер недели в году первой недели текущего месяца
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return weekOfYear;
    }

    public static int getDayOfWeek(Calendar calendar) {
        // находит номер дня в недели первого дня текущего месяца
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(
                Calendar.DAY_OF_MONTH));
        int DayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if (DayOfWeek > 1) {
            return DayOfWeek - 1;
        } else {
            return 7; // sunday
        }
    }
}
