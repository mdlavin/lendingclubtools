package org.github.lendingclubtools;

import java.math.BigDecimal;
import java.util.Date;


public class PaymentScheduler extends AbstractPaymentScheduler<LoanData> {
	
	@Override
	protected BigDecimal getAmount(LoanData loan) {
		return loan.getAmount();
	}
	
	@Override
	protected int getId(LoanData loan) {
		return loan.getId();
	}
	
	protected BigDecimal getMonthlyPayment(LoanData loan) {
		return loan.getMonthlyPayment();
	}
	
	@Override
	protected Date getIssueDate(LoanData loan) {
		return loan.getIssueDate();
	}
	
	@Override
	protected int getLoanLength(LoanData loan) {
		return loan.getLoanLengthInMonths();
	}
	
	@Override
	protected BigDecimal getTotalPayments(LoanData loan) {
		return loan.getPaymentsToDate();
	}
	
}
