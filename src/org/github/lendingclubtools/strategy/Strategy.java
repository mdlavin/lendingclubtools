package org.github.lendingclubtools.strategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.github.lendingclubtools.LoanData;


public interface Strategy {

	public Map<LoanData, BigDecimal> chooseLoans(ReadOnlyAccount details, Set<LoanData> availableLoans);
	
}
