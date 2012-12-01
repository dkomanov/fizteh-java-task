/*
 * ZCalendar.java
 * Dec 1, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.calendar;

import ru.fizteh.fivt.students.harius.argparse.*;
import java.util.*;
import static java.util.Calendar.*;

/*
 * Calendar console utility
 */
public class ZCalendar {

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(MONDAY);
        Locale locale = Locale.getDefault();

        CalendarSettings settings = new CalendarSettings();
        Argparser parser = new Argparser(args);
        try {
            parser.load(settings);
        } catch (ArgparseException parseEx) {
            System.err.println("Exception while parsing arguments:");
            System.err.println(parseEx.getMessage());
            System.exit(1);
        }

        if (!settings.rubbish.isEmpty()) {
            System.err.println("Unrecognized option: "
                + settings.rubbish.get(0));
            System.exit(1);
        }

        if (settings.year != null) {
            if (settings.year < 1000 || settings.year > 9999) {
                System.err.println("Year must be between 1000 and 9999");
                System.exit(1);
            } else {
                calendar.set(YEAR, settings.year);
            }
        }

        if (settings.month != null) {
            if (settings.month < 1 || settings.month > 12) {
                System.err.println("Month must be between 1 and 12");
                System.exit(1);
            } else {
                calendar.set(MONTH, settings.month - 1);
            }
        }

        List<String> zones
            = Arrays.asList(TimeZone.getAvailableIDs());
        if (settings.zone != null
            && zones.indexOf(settings.zone) == -1) {
            
            System.err.println("Wrong time zone");
            System.exit(1);
        }

        calendar.set(DAY_OF_MONTH, 
                    calendar.getActualMinimum(DAY_OF_MONTH));

        CalendarView view = new CalendarView(
            calendar, locale, settings);

        System.out.println(view);
    }
}