package ru.fizteh.fivt.students.harius.calendar;

import java.util.*;

public class CalendarView {
	private Calendar calendar = Calendar.getInstance();
	private Locale locale = Locale.getDefault();

	public static void main(String[] args) {
		CalendarView view = new CalendarView();
		System.out.println(view);
	}

	public CalendarView() {

	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));
		result.append("\n");

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);

		

		return result.toString();
	}
}