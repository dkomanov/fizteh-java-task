package ru.fizteh.fivt.students.harius.calendar;

import java.util.*;
import java.text.*;
import static java.util.Calendar.*;

public class CalendarView {
	private Calendar calendar;
	private DateFormat header;
	private DateFormatSymbols names;
	private Locale locale;
	private CalendarSettings settings;
	private DateFormat footer = null;

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

	private void generateHeader(StringBuilder builder) {
		if (settings.week) {
			builder.append("     ");
		}
		builder.append(header.format(calendar.getTime()));
		builder.append("\n");
	}

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
		builder.append("\n");
	}

	private void generateBody(StringBuilder builder) {
		Calendar iterator = Calendar.getInstance(locale);
		iterator.setTime(calendar.getTime());

		iterator.set(WEEK_OF_MONTH,
			iterator.getActualMinimum(WEEK_OF_MONTH) + 1);

		iterator.set(DAY_OF_WEEK, MONDAY);

		while (iterator.get(MONTH) !=
			calendar.get(MONTH) ||
				iterator.get(DAY_OF_MONTH) <=
					iterator.getActualMaximum(DAY_OF_MONTH)) {

			if (settings.week
				&& iterator.get(DAY_OF_WEEK)
				== MONDAY) {

				builder.append(
					String.format("%3d  ", iterator.get(WEEK_OF_YEAR)));
			}

			if (iterator.get(MONTH) == 
				calendar.get(MONTH)) {

				builder.append(
					String.format("%3d", iterator.get(DAY_OF_MONTH)));
			} else {
				builder.append("   ");
			}

			if (iterator.get(DAY_OF_WEEK)
				== SUNDAY) {

				builder.append("\n");
			} else {
				builder.append(" ");
			}
			if (iterator.get(MONTH) ==
				calendar.get(MONTH) &&
				iterator.get(DAY_OF_MONTH)
			 	== iterator.getActualMaximum(DAY_OF_MONTH)) {
			 	break;
			}
			iterator.set(DAY_OF_MONTH,
				iterator.get(DAY_OF_MONTH) + 1);
		}
	}

	private void generateFooter(StringBuilder builder) {
		if (footer != null) {
			builder.append("\n\nNow: ");
			builder.append(
				footer.format(Calendar.getInstance(locale).getTime()));
		}
	}

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