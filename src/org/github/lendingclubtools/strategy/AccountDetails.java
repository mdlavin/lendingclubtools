package org.github.lendingclubtools.strategy;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class AccountDetails implements ReadOnlyAccount {
	
	private BigDecimal availableCash;
	private BigDecimal paymentsReceived;
	private Map<Integer, BigDecimal> investments;
	
	public AccountDetails(BigDecimal startingAmount) {
		availableCash = startingAmount;
		investments = new HashMap<Integer, BigDecimal>();
		paymentsReceived = BigDecimal.ZERO;
	}

	public BigDecimal getAvailableCash() {
		return availableCash;
	}
	
	public Map<Integer, BigDecimal> getInvestments() {
		return investments;
	}
	
	public BigDecimal getPaymentsReceived() {
		return paymentsReceived;
	}
	
	public void addInvestment(int loanId, BigDecimal amountToInvest) {
		withdrawlCash(amountToInvest);
		
		BigDecimal existingInvestment = investments.get(loanId);
		if (existingInvestment == null) {
			investments.put(loanId, amountToInvest);
		} else {
			investments.put(loanId, existingInvestment.add(amountToInvest));
		}
	}
	
	public void receivePayment(BigDecimal payment) {
		paymentsReceived = paymentsReceived.add(payment);
		addCash(payment);
	}

	public void addCash(BigDecimal cash) {
		BigDecimal newValue = availableCash.add(cash);
		if (newValue.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(MessageFormat.format("The current balance is {0} and a withdrawl of {1} was attempted", availableCash, cash));
		}
		availableCash = newValue;
	}
	
	public void withdrawlCash(BigDecimal cash) {
		if (cash.compareTo(availableCash) > 0) {
			throw new IllegalArgumentException(String.format("The avaiable cash is %f but the withdrawl amount requested is %f", availableCash.doubleValue(), cash.doubleValue()));
		}
		
		availableCash = availableCash.subtract(cash);
	}
	
}
