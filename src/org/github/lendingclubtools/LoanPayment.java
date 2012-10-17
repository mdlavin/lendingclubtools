package org.github.lendingclubtools;

import java.math.BigDecimal;
import java.util.Date;

public class LoanPayment {

	private final int loanId;
	private final Date paymentDate;
	private final BigDecimal payment;

	public LoanPayment(int loanId, Date paymentDate, BigDecimal payment) {
		this.loanId = loanId;
		this.paymentDate = paymentDate;
		this.payment = payment;
	}
	
	public BigDecimal getPayment() {
		return payment;
	}
	
	public int getLoanId() {
		return loanId;
	}
	
	public Date getPaymentDate() {
		return paymentDate;
	}
	
}
