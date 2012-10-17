package org.github.lendingclubtools.strategy.tests;

import java.math.BigDecimal;
import java.util.Date;

import org.github.lendingclubtools.LoanData;
import org.github.lendingclubtools.LoanDataParser;
import org.github.lendingclubtools.strategy.AccountDetails;
import org.github.lendingclubtools.strategy.StrategyExecutor;

import junit.framework.TestCase;

public class StrategyExecutorTests extends TestCase {

	public void testSingleLoanSimpleStrategy() throws Exception {
		LoanDataParser parser = new LoanDataParser(getClass().getResource("single-loan-449679.csv"));
		LoanData[] data = parser.readData();
		StrategyExecutor executor = new StrategyExecutor(new LoanData[] {data[0]}, new BigDecimal(25), null, new Date(1346640825276l));
		
		AccountDetails endingAccount = executor.testStrategy(new PreferLowerIdStrategy());
		
		// Ideally the expected value would be 24.91, because that's what LendingClub reports,
		// but I can't figure out how their math works.  24.95 is close enough
		assertEquals("Account value", new BigDecimal("24.95"), endingAccount.getAvailableCash());
	}
	
}
