package org.github.lendingclubtools.strategy;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.github.lendingclubtools.AvailabilityLookup;
import org.github.lendingclubtools.CalendarHelper;
import org.github.lendingclubtools.LoanData;
import org.github.lendingclubtools.LoanPayment;
import org.github.lendingclubtools.PaymentScheduler;


public class StrategyExecutor {

	public final static BigDecimal MIN_INVESTMENT = new BigDecimal(25);

	private final Date startDate;
	private final Date endDate;
	private final BigDecimal startingBalance;
	
	private final Map<PaymentLookup, LoanPayment> allPayments;
	private final AvailabilityLookup<LoanData> availableLoans;
	
	public StrategyExecutor(LoanData[] allLoans, BigDecimal startingBalance, Date startDate, Date endDate) {
		if (startDate == null) {
			startDate = allLoans[0].getAvailabilityStartDate();
			for (LoanData loan : allLoans) {
				if (loan.getAvailabilityStartDate().before(startDate)) {
					startDate = loan.getAvailabilityStartDate();
				}
			}
		}
		this.startDate = startDate;
		
		if (endDate == null) {
			endDate = new Date();
		}
		this.endDate = endDate;
		this.startingBalance = startingBalance;
		this.allPayments = new HashMap<PaymentLookup, LoanPayment>();
		this.availableLoans = new AvailabilityLookup<LoanData>(allLoans);
	}
	
	/**
	 * Tests the given strategy against loan data between the start and end date for this executor.
	 *  
	 * @return the account details at the end of the test
	 */
	public AccountDetails testStrategy(Strategy strategy) {
		AccountDetails account = new AccountDetails(startingBalance);
		
		Calendar cal = CalendarHelper.newCalendarForDay(startDate);
		
		while (cal.getTime().compareTo(endDate) <= 0) {
			// Process payments from existing loans
			processPayments(account, cal);
			
			// Find new loans to purchase
			makeNewInvestments(strategy, account, cal.getTime());
			
			// Increment time
			cal.add(Calendar.DATE, 1);
		}
		
		return account;
	}
	
	private void processPayments(AccountDetails account, Calendar date) {
		Map<Integer, BigDecimal> existingInvestments = account.getInvestments();
		
		for (Map.Entry<Integer, BigDecimal> investment : existingInvestments.entrySet()) {
			int loanId = investment.getKey();
			BigDecimal investmentAmount = investment.getValue();
			LoanPayment loanPayment = allPayments.get(new PaymentLookup(loanId, date.getTime(), investmentAmount));
			if (loanPayment != null) {
				BigDecimal paymentAmount = loanPayment.getPayment();
				System.out.println(String.format("Payment of $%2f from loan %d on %d/%d/%d", paymentAmount.doubleValue(), loanId, date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1, date.get(Calendar.DAY_OF_MONTH)));				
				account.receivePayment(paymentAmount);
			}
		}
	}
	
	private void makeNewInvestments(Strategy strategy, AccountDetails account, Date date) {
		Set<LoanData> availableLoans = this.availableLoans.getLoans(date);
		Map<LoanData, BigDecimal> purchases = strategy.chooseLoans(account, availableLoans);
		
		for (Map.Entry<LoanData, BigDecimal> purchase : purchases.entrySet()) {
			LoanData loan = purchase.getKey();
			int loanId = loan.getId();
			BigDecimal amountInvested = purchase.getValue();
			if (amountInvested.compareTo(MIN_INVESTMENT) < 0) {
				throw new RuntimeException(String.format("The minimum investment is $%f but an investment of $%f was requested.", MIN_INVESTMENT.doubleValue(), amountInvested.doubleValue()));
			}
			
			System.out.println(String.format("Investing $%f in loan %d ", amountInvested.doubleValue(), loanId));
			
			account.addInvestment(loanId, amountInvested);
			
			PaymentScheduler scheduler = new PaymentScheduler();
			LoanPayment[] payments = scheduler.createPayments(loan, amountInvested);
			for (LoanPayment payment : payments) {
				PaymentLookup paymentLookup = new PaymentLookup(loanId, payment.getPaymentDate(), amountInvested);
				if (allPayments.containsKey(paymentLookup) == false) {
					allPayments.put(paymentLookup, payment);
				}
			}

		}
		
	}
	
}

class PaymentLookup {
	private final int loanId;
	private final Date date;
	private final BigDecimal investmentAmount;

	public PaymentLookup(int loanId, Date date, BigDecimal investmentAmount) {
		this.loanId = loanId;
		this.date = date;
		this.investmentAmount = investmentAmount;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime
				* result
				+ ((investmentAmount == null) ? 0 : investmentAmount.hashCode());
		result = prime * result + loanId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaymentLookup other = (PaymentLookup) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (investmentAmount == null) {
			if (other.investmentAmount != null)
				return false;
		} else if (!investmentAmount.equals(other.investmentAmount))
			return false;
		if (loanId != other.loanId)
			return false;
		return true;
	}

}
