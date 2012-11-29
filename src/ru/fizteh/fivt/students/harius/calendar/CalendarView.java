package ru.fizteh.fivt.students.harius.calendar;

import java.util.*;
import java.text.*;
import static java.util.Calendar.*;

public class CalendarView {
	private Calendar calendar;
	private DateFormat header;
	private DateFormatSymbols names;
	private Locale locale = Locale.getDefault();

	public static void main(String[] args) {
		CalendarView view = new CalendarView(Calendar.getInstance());
		System.out.println(view);
	}

	public CalendarView(Calendar calendar) {
		this.calendar = calendar;
		header = new SimpleDateFormat(
			"        MMMM Y", locale);

		names = new DateFormatSymbols(locale);
	}

	private void generateHeader(StringBuilder builder) {
		builder.append(header.format(calendar.getTime()));
		builder.append("\n");
	}

	private void generateSubheader(StringBuilder builder) {
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
			iterator.getActualMinimum(WEEK_OF_MONTH));

		iterator.set(DAY_OF_WEEK, iterator.getActualMinimum(DAY_OF_WEEK));

		while (iterator.get(MONTH) !=
			calendar.get(MONTH) ||
				iterator.get(DAY_OF_MONTH) <=
					iterator.getActualMaximum(DAY_OF_MONTH)) {

			builder.append(String.format("%3d", iterator.get(DAY_OF_MONTH)));
			if (iterator.get(DAY_OF_WEEK)
				== iterator.getActualMaximum(DAY_OF_WEEK)) {

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

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		generateHeader(result);
		generateSubheader(result);
		generateBody(result);

		return result.toString();
	}
}