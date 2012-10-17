package org.github.lendingclubtools.strategy;

import java.math.BigDecimal;
import java.util.Map;

public interface ReadOnlyAccount {

	public BigDecimal getAvailableCash();
	
	public Map<Integer, BigDecimal> getInvestments();
	
}
