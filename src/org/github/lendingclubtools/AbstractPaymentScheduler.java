package org.github.lendingclubtools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractPaymentScheduler<Loan> {

	protected abstract BigDecimal getTotalPayments(Loan loan);
	protected abstract BigDecimal getAmount(Loan loan);
	protected abstract int getLoanLength(Loan loan);
	protected abstract Date getIssueDate(Loan loan);
	protected abstract int getId(Loan loan);
	protected abstract BigDecimal getMonthlyPayment(Loan loan);
	
	public LoanPayment[] createPayments(Loan loan, BigDecimal investment) {
		int loanId = getId(loan);
		List<Date> dates = calculatePaymentDates(loan);
		int numberOfPayments = dates.size();

		BigDecimal actualPaymentsSoFar = BigDecimal.ZERO;
		
		LoanPayment[] payments = new LoanPayment[numberOfPayments];
		for (int i=0;i<dates.size();i++) {
			
			BigDecimal totalPaymentsSoFar;
			BigDecimal idealTotalPaymentsSoFar = getMonthlyPayment(loan).multiply(new BigDecimal(i+1));
			if (getTotalPayments(loan).compareTo(idealTotalPaymentsSoFar) < 0) {
				totalPaymentsSoFar = getTotalPayments(loan);
			} else {
				totalPaymentsSoFar = idealTotalPaymentsSoFar;
			}
			
			
			// The code below should be equal to:
			//                       investment      
			// idealPaymentsSoFar = ------------ * totalPaymentsSoFar
			//                       loanAmount    
			
			BigDecimal idealPaymentsSoFar = totalPaymentsSoFar.multiply(investment).divide(getAmount(loan), 2, RoundingMode.DOWN);
			BigDecimal monthlyPayment = idealPaymentsSoFar.subtract(actualPaymentsSoFar);
			
			payments[i] = new LoanPayment(loanId, dates.get(i), monthlyPayment);
			
			actualPaymentsSoFar = actualPaymentsSoFar.add(monthlyPayment);
		}
		
		return payments;
	}
	
	private List<Date> calculatePaymentDates(Loan loan) {
		Calendar loanEnd = CalendarHelper.newCalendarForDay(getIssueDate(loan));
		loanEnd.add(Calendar.MONTH, getLoanLength(loan));

		Calendar now = CalendarHelper.newCalendarForDay(new Date());

		Calendar current = CalendarHelper.newCalendarForDay(getIssueDate(loan));
		current.add(Calendar.MONTH, 1);
		
		List<Date> paymentDates = new ArrayList<Date>();
		while ((current.compareTo(now) <= 0) && (current.compareTo(loanEnd)) <= 0) {
			paymentDates.add(current.getTime());
			current.add(Calendar.MONTH, 1);
		}
		
		return paymentDates;
	}

}
