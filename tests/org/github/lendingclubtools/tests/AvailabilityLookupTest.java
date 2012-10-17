package org.github.lendingclubtools.tests;

import java.util.Date;
import java.util.Set;

import junit.framework.Assert;

import org.github.lendingclubtools.AvailabilityLookup;
import org.github.lendingclubtools.CalendarHelper;
import org.github.lendingclubtools.HasAvailabilityDates;
import org.junit.Test;

public class AvailabilityLookupTest {

	@Test
	public void testGetLoans_empty() {
		AvailabilityLookup<TestObj> avail = new AvailabilityLookup<TestObj>(new TestObj[0]);
		Assert.assertEquals("Avaiable loans", 0, avail.getLoans(new Date()).size());
	}
	
	@Test
	public void testGetLoans_before_start_date() {
		TestObj test = new TestObj(CalendarHelper.newDateForDay(2012, 1, 1),
								   CalendarHelper.newDateForDay(2012, 1, 2));
		AvailabilityLookup<TestObj> avail = new AvailabilityLookup<TestObj>(test);
		Set<TestObj> availLoan = avail.getLoans(CalendarHelper.newDateForDay(2011, 1, 2));
		Assert.assertEquals("Avaiable loans", 0, availLoan.size());
	}

	
	@Test
	public void testGetLoans_on_start_date() {
		TestObj test = new TestObj(CalendarHelper.newDateForDay(2012, 1, 1),
								   CalendarHelper.newDateForDay(2012, 1, 2));
		AvailabilityLookup<TestObj> avail = new AvailabilityLookup<TestObj>(test);
		Set<TestObj> availLoan = avail.getLoans(CalendarHelper.newDateForDay(2012, 1, 1));
		Assert.assertEquals("Avaiable loans", 1, availLoan.size());
		Assert.assertTrue("Correct loan available", availLoan.contains(test));
	}
	
	@Test
	public void testGetLoans_on_end_date() {
		TestObj test = new TestObj(CalendarHelper.newDateForDay(2012, 1, 1),
								   CalendarHelper.newDateForDay(2012, 1, 2));
		AvailabilityLookup<TestObj> avail = new AvailabilityLookup<TestObj>(test);
		Set<TestObj> availLoan = avail.getLoans(CalendarHelper.newDateForDay(2012, 1, 2));
		Assert.assertEquals("Avaiable loans", 1, availLoan.size());
		Assert.assertTrue("Correct loan available", availLoan.contains(test));
	}

	@Test
	public void testGetLoans_after_end_date() {
		TestObj test = new TestObj(CalendarHelper.newDateForDay(2012, 1, 1),
								   CalendarHelper.newDateForDay(2012, 1, 2));
		AvailabilityLookup<TestObj> avail = new AvailabilityLookup<TestObj>(test);
		Set<TestObj> availLoan = avail.getLoans(CalendarHelper.newDateForDay(2011, 1, 3));
		Assert.assertEquals("Avaiable loans", 0, availLoan.size());
	}

	
	@Test
	public void testGetLoans_on_in_middle() {
		TestObj test = new TestObj(CalendarHelper.newDateForDay(2012, 1, 1),
								   CalendarHelper.newDateForDay(2012, 1, 3));
		AvailabilityLookup<TestObj> avail = new AvailabilityLookup<TestObj>(test);
		Set<TestObj> availLoan = avail.getLoans(CalendarHelper.newDateForDay(2012, 1, 2));
		Assert.assertEquals("Avaiable loans", 1, availLoan.size());
		Assert.assertTrue("Correct loan available", availLoan.contains(test));
	}
	
	@Test
	public void testGetLoans_overlapping() {
		TestObj test = new TestObj(CalendarHelper.newDateForDay(2012, 1, 1),
								   CalendarHelper.newDateForDay(2012, 1, 3));
		TestObj test2 = new TestObj(CalendarHelper.newDateForDay(2012, 1, 2),
				                    CalendarHelper.newDateForDay(2012, 1, 4));
		
		AvailabilityLookup<TestObj> avail = new AvailabilityLookup<TestObj>(test, test2);
		Set<TestObj> availLoan = avail.getLoans(CalendarHelper.newDateForDay(2012, 1, 2));
		Assert.assertEquals("Avaiable loans", 2, availLoan.size());
		Assert.assertTrue("loan 1 available", availLoan.contains(test));
		Assert.assertTrue("loan 2 available", availLoan.contains(test2));
	}
	

	private class TestObj implements HasAvailabilityDates {
		private final Date start;
		private final Date end;

		private TestObj(Date start, Date end) {
			this.start = start;
			this.end = end;
		}
		
		public Date getAvailabilityEndDate() {
			return end;
		}
		
		public Date getAvailabilityStartDate() {
			return start;
		}
	}
	
}

