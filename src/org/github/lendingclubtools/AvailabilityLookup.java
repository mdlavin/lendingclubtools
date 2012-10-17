package org.github.lendingclubtools;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AvailabilityLookup<L extends HasAvailabilityDates> {
	
	private final Map<Date, Set<L>> availableLoans;

	public AvailabilityLookup(L ... allLoans) {
		this.availableLoans = new HashMap<Date, Set<L>>();

		for (L loan : allLoans) {
			
			Calendar end = CalendarHelper.newCalendarForDay(loan.getAvailabilityEndDate());
			
			Date start = loan.getAvailabilityStartDate();
			Calendar cal = CalendarHelper.newCalendarForDay(start);
			
			while (cal.compareTo(end) <= 0) {
				Date date = cal.getTime();
				
				Set<L> loans = availableLoans.get(date);
				if (loans==null) {
					loans = new HashSet<L>();
					availableLoans.put(date, loans);
				}
				loans.add(loan);
				
				cal.add(Calendar.DATE, 1);
			}
		}
		
	}

	public Set<L> getLoans(Date when) {
		Set<L> loans = availableLoans.get(when);
		if (loans == null) {
			return Collections.emptySet();
		} else {
			return loans;
		}
	}
	
}
