/*
 * CalendarView.java
 * Dec 1, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.calendar;

import java.util.*;
import java.text.*;
import static java.util.Calendar.*;


/*
 * Converts Calendar to human-readable String
 */
public class CalendarView {
	private final String newl
		= System.lineSeparator();

	private Calendar calendar;
	private DateFormat header;
	private DateFormatSymbols names;
	private Locale locale;
	private CalendarSettings settings;
	private DateFormat footer = null;

	/* Initializes the viewer */
	public CalendarView(Calendar calendar, Locale locale,
		CalendarSettings settings) {

		this.calendar = calendar;
		this.locale = locale;
		this.settings = settings;
		
		header = new SimpleDateFormat(
			"        MMMM Y", locale);

		if (settings.zone != null) {
			footer = new SimpleDateFormat(
				"yyyy.MM.dd HH:mm:ss zzzz");

			footer.setTimeZone(TimeZone.getTimeZone(
				settings.zone));
		}

		names = new DateFormatSymbols(locale);
	}

	/* Writes month and year */
	private void generateHeader(StringBuilder builder) {
		if (settings.week) {
			builder.append("     ");
		}
		builder.append(header.format(calendar.getTime()));
		builder.append(newl);
	}

	/* Writes week days */
	private void generateSubheader(StringBuilder builder) {
		if (settings.week) {
			builder.append("     ");
		}

		int[] indices = {MONDAY, TUESDAY,
			WEDNESDAY, THURSDAY,
			FRIDAY, SATURDAY, SUNDAY};

		String[] days = names.getShortWeekdays();
		for (int day : indices) {
			builder.append(String.format("%3s ", days[day]));
		}
		builder.append(newl);
	}

	/* Writes the main part */
	private void generateBody(StringBuilder builder) {
		Calendar iterator = Calendar.getInstance(locale);
		iterator.setFirstDayOfWeek(MONDAY);
		iterator.setTime(calendar.getTime());

		iterator.set(DAY_OF_MONTH,
			iterator.getActualMinimum(DAY_OF_MONTH));

		iterator.set(DAY_OF_WEEK, MONDAY);

		while (iterator.get(MONTH) != calendar.get(MONTH)
				|| iterator.get(DAY_OF_MONTH)
					<= iterator.getActualMaximum(DAY_OF_MONTH)) {

			if (settings.week
				&& iterator.get(DAY_OF_WEEK)
				== MONDAY) {

				builder.append(
					String.format("%3d  ", iterator.get(WEEK_OF_YEAR)));
			}

			if (iterator.get(MONTH) == calendar.get(MONTH)) {

				builder.append(
					String.format("%3d", iterator.get(DAY_OF_MONTH)));
			} else {
				builder.append("   ");
			}

			if (iterator.get(MONTH) == calendar.get(MONTH)
				&& iterator.get(DAY_OF_MONTH)
			 	== iterator.getActualMaximum(DAY_OF_MONTH)) {
			 	break;
			}

			if (iterator.get(DAY_OF_WEEK)
				== SUNDAY) {

				builder.append(newl);
			} else {
				builder.append(" ");
			}

			iterator.set(DAY_OF_MONTH,
				iterator.get(DAY_OF_MONTH) + 1);
		}
	}

	/* Writes current time and date */
	private void generateFooter(StringBuilder builder) {
		if (footer != null) {
			builder.append(newl);
			builder.append(newl);
			builder.append("Now: ");
			builder.append(
				footer.format(Calendar.getInstance(locale).getTime()));
		}
	}

	/* Returns a string representing given Calendar */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		generateHeader(result);
		generateSubheader(result);
		generateBody(result);
		generateFooter(result);

		return result.toString();
	}
}