package org.github.lendingclubtools.tests;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.github.lendingclubtools.AbstractPaymentScheduler;
import org.github.lendingclubtools.CalendarHelper;
import org.github.lendingclubtools.LoanPayment;

import junit.framework.TestCase;

public class LoanPaymentSchedulerTest extends TestCase {
	
	private Date oneYearAgo() {
		Calendar c = CalendarHelper.newCalendarForDay(new Date());
		c.add(Calendar.YEAR, -1);
		return c.getTime();
	}
	
	public void testLoanFullyPaid() {
		TestablePaymentScheduler scheduler = new TestablePaymentScheduler();
		TestLoan loan = new TestLoan(100, 1, oneYearAgo(), 10, 110, 11);
		LoanPayment[] payments = scheduler.createPayments(loan, BigDecimal.valueOf(10));
		
		assertEquals("Number of payments", 10, payments.length);
		for (int i=0;i<10;i++) {
			assertEquals("Payment " + i + " amount", BigDecimal.valueOf(110, 2), payments[i].getPayment());
			assertEquals("Payment " + i + " id", 1, payments[i].getLoanId());
		}
	}
	
	public void testLoanStillActive() {
		TestablePaymentScheduler scheduler = new TestablePaymentScheduler();
		TestLoan loan = new TestLoan(100, 1, oneYearAgo(), 20, 72, 6);
		LoanPayment[] payments = scheduler.createPayments(loan, BigDecimal.valueOf(10));
		
		assertEquals("Number of payments", 12, payments.length);
		for (int i=0;i<10;i++) {
			assertEquals("Payment " + i + " amount", BigDecimal.valueOf(60, 2), payments[i].getPayment());
			assertEquals("Payment " + i + " id", 1, payments[i].getLoanId());
		}
	}
	
	public void testLoanDefault() {
		TestablePaymentScheduler scheduler = new TestablePaymentScheduler();
		TestLoan loan = new TestLoan(100, 1, oneYearAgo(), 20, 10, 6);
		LoanPayment[] payments = scheduler.createPayments(loan, BigDecimal.valueOf(10));
		
		assertEquals("Number of payments", 12, payments.length);
		assertEquals("Payment 0 amount", BigDecimal.valueOf(60, 2), payments[0].getPayment());
		assertEquals("Payment 1 amount", BigDecimal.valueOf(40, 2), payments[1].getPayment());
		
		for (int i=2;i<10;i++) {
			assertEquals("Payment " + i + " amount", BigDecimal.valueOf(0, 2), payments[i].getPayment());
		}
	}

	
}

class TestLoan {
	
	public final BigDecimal amount;
	public final int id;
	public final Date issueDate;
	public final int length;
	public final BigDecimal totalPayments;
	public final BigDecimal monthlyPayments;

	public TestLoan(int amount, int id, Date issueDate, int length, int totalPayments, int monthlyPayments) {
		this(new BigDecimal(amount), id, issueDate, length, new BigDecimal(totalPayments), BigDecimal.valueOf(monthlyPayments));
	}
	
	public TestLoan(BigDecimal amount, int id, Date issueDate, int length, BigDecimal totalPayments, BigDecimal monthlyPayments) {
		this.amount = amount;
		this.id = id;
		this.issueDate = issueDate;
		this.length = length;
		this.totalPayments = totalPayments;
		this.monthlyPayments = monthlyPayments;
	}
	
}

class TestablePaymentScheduler extends AbstractPaymentScheduler<TestLoan> {
	
	@Override
	protected BigDecimal getAmount(TestLoan loan) {
		return loan.amount;
	}
	
	@Override
	protected int getId(TestLoan loan) {
		return loan.id;
	}

	@Override
	protected Date getIssueDate(TestLoan loan) {
		return loan.issueDate;
	}
	
	@Override
	protected int getLoanLength(TestLoan loan) {
		return loan.length;
	}
	
	@Override
	protected BigDecimal getTotalPayments(TestLoan loan) {
		return loan.totalPayments;
	}
	
	@Override
	protected BigDecimal getMonthlyPayment(TestLoan loan) {
		return loan.monthlyPayments;
	}
	
	
}