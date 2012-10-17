package org.github.lendingclubtools;

import java.math.BigDecimal;
import java.util.Date;

public class LoanData implements HasAvailabilityDates {
	
	private final int id;
	private final BigDecimal amount;
	private final BigDecimal interestRate;
	private final Date issueDate;
	private final Date applicationStartDate;
	private final Date applicationEndDate;
	private final int loanLengthInMonths;
	private final BigDecimal paymentsToDate;
	private final BigDecimal monthlyPayment;

	public LoanData(int id, BigDecimal amount, BigDecimal interestRate, Date issueDate, int loanLengthInMonths, BigDecimal paymentsToDate, Date applicationStartDate, Date applicationEndDate, BigDecimal monthlyPayment) {
		this.id = id;
		this.amount = amount;
		this.interestRate = interestRate;
		this.issueDate = issueDate;
		this.loanLengthInMonths = loanLengthInMonths;
		this.paymentsToDate = paymentsToDate;
		this.applicationStartDate = applicationStartDate;
		this.applicationEndDate = applicationEndDate;
		this.monthlyPayment = monthlyPayment;
	}
	
	@Override
	public String toString() {
		return String.format("LoanData[id=%d, amount=%f, rate=%f]", id, amount, interestRate);
	}
	
	public int getId() {
		return id;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	
	public int getLoanLengthInMonths() {
		return loanLengthInMonths;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}
	
	public BigDecimal getPaymentsToDate() {
		return paymentsToDate;
	}
	
	public Date getAvailabilityStartDate() {
		return applicationStartDate;
	}
	
	public Date getAvailabilityEndDate() {
		return applicationEndDate;
	}

	public BigDecimal getMonthlyPayment() {
		return monthlyPayment;
	}

}
