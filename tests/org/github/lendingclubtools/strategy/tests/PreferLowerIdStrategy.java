package org.github.lendingclubtools.strategy.tests;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.github.lendingclubtools.LoanData;
import org.github.lendingclubtools.strategy.ReadOnlyAccount;
import org.github.lendingclubtools.strategy.Strategy;
import org.github.lendingclubtools.strategy.StrategyExecutor;


public class PreferLowerIdStrategy implements Strategy {

	public Map<LoanData, BigDecimal> chooseLoans(ReadOnlyAccount details, Set<LoanData> availableLoans) {
		Set<Integer> existingLoans = details.getInvestments().keySet();
		
		Set<LoanData> sortedLoans = new TreeSet<LoanData>(new Comparator<LoanData>() {
			public int compare(LoanData o1, LoanData o2) {
				return new Integer(o1.getId()).compareTo(o2.getId());
			}
		});
		for (LoanData loan : availableLoans) {
			int loanId = loan.getId();
			if (existingLoans.contains(loanId) == false) {
				sortedLoans.add(loan);
			}
		}
		
		Map<LoanData, BigDecimal> newInvestments = new HashMap<LoanData, BigDecimal>();
		BigDecimal moneyLeft = details.getAvailableCash();
		
		for (LoanData loan : sortedLoans) {
			if (moneyLeft.compareTo(StrategyExecutor.MIN_INVESTMENT) < 0) {
				break;
			}
			
			moneyLeft = moneyLeft.subtract(StrategyExecutor.MIN_INVESTMENT);
			newInvestments.put(loan, StrategyExecutor.MIN_INVESTMENT);
		}
		
		return newInvestments;
	}

}
