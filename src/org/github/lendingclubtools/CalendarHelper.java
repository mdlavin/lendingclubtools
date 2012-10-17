package org.github.lendingclubtools;

import java.util.Calendar;
import java.util.Date;

public final class CalendarHelper {
	
	private CalendarHelper() {
		throw new UnsupportedOperationException();
	}
	
	public static Date newDateForDay(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month+1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}


	public static Calendar newCalendarForDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal;
	}
	
}
