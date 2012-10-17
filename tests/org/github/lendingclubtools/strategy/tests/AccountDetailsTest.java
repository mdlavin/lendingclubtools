package org.github.lendingclubtools.strategy.tests;

import java.math.BigDecimal;
import java.util.Map;

import junit.framework.Assert;

import org.github.lendingclubtools.strategy.AccountDetails;
import org.junit.Test;

public class AccountDetailsTest {

	@Test(expected=IllegalArgumentException.class)
	public void testWithdrawlCash_overdraw() {
		AccountDetails details = new AccountDetails(BigDecimal.ZERO);
		details.withdrawlCash(BigDecimal.ONE);
	}

	@Test
	public void testWithdrawlCash_zero() {
		AccountDetails details = new AccountDetails(BigDecimal.ZERO);
		details.withdrawlCash(BigDecimal.ZERO);
	}

	@Test
	public void testWithdrawlCash_all() {
		AccountDetails details = new AccountDetails(BigDecimal.ONE);
		details.withdrawlCash(BigDecimal.ONE);
		Assert.assertEquals("Cash balance", BigDecimal.ZERO, details.getAvailableCash());
	}
	
	@Test
	public void testConstructor() {
		AccountDetails details = new AccountDetails(BigDecimal.ONE);
		Assert.assertEquals("Starting balance", BigDecimal.ONE, details.getAvailableCash());
		Assert.assertNotNull("Starting investments should not be null", details.getInvestments());
		Assert.assertTrue("Starting investments should be empty", details.getInvestments().isEmpty());
		Assert.assertEquals("Initial payments", BigDecimal.ZERO, details.getPaymentsReceived());
	}
	
	@Test
	public void testAddInvestment_initial() {
		AccountDetails details = new AccountDetails(BigDecimal.ONE);
		details.addInvestment(0, BigDecimal.ONE);
		Map<Integer, BigDecimal> investments = details.getInvestments();
		Assert.assertNotNull("Investments should not be null", investments);
		Assert.assertEquals("Investments size", 1, investments.size());
		Assert.assertEquals("Investments 0", BigDecimal.ONE, investments.get(0));
		Assert.assertEquals("New cash", BigDecimal.ZERO, details.getAvailableCash());
	}

	@Test
	public void testAddInvestment_additional() {
		AccountDetails details = new AccountDetails(BigDecimal.TEN);
		details.addInvestment(0, BigDecimal.ONE);
		details.addInvestment(0, BigDecimal.ONE);
		Map<Integer, BigDecimal> investments = details.getInvestments();
		Assert.assertNotNull("Investments should not be null", investments);
		Assert.assertEquals("Investments size", 1, investments.size());
		Assert.assertEquals("Investments 0", BigDecimal.valueOf(2), investments.get(0));
		Assert.assertEquals("New cash", BigDecimal.valueOf(8), details.getAvailableCash());
	}
	
	@Test
	public void testAddCash() {
		AccountDetails details = new AccountDetails(BigDecimal.TEN);
		details.addCash(BigDecimal.ONE);
		
		Assert.assertEquals("New cash value", BigDecimal.valueOf(11), details.getAvailableCash());
	}
	
	@Test
	public void testAddCash_withdrawlAll() {
		AccountDetails details = new AccountDetails(BigDecimal.TEN);
		details.addCash(BigDecimal.TEN.negate());
		
		Assert.assertEquals("New cash value", BigDecimal.ZERO, details.getAvailableCash());
	}

	
	@Test
	public void testAddCash_negative() {
		AccountDetails details = new AccountDetails(BigDecimal.TEN);
		details.addCash(BigDecimal.valueOf(-1));
		
		Assert.assertEquals("New cash value", BigDecimal.valueOf(9), details.getAvailableCash());
	}
	
	public void testAddCash_bounce() {
		AccountDetails details = new AccountDetails(BigDecimal.ZERO);
		try {
			details.addCash(BigDecimal.valueOf(-1));
			Assert.fail("An IllegalArgumentException was expected");
		} catch (IllegalArgumentException exp) {
			// Good, this should happen
			Assert.assertEquals("Cash value", BigDecimal.ZERO, details.getAvailableCash());
		}
	}
	
	@Test
	public void testReceivePayment() {
		AccountDetails details = new AccountDetails(BigDecimal.TEN);
		details.receivePayment(BigDecimal.valueOf(1));
		
		Assert.assertEquals("New cash value", BigDecimal.valueOf(11), details.getAvailableCash());
		Assert.assertEquals("New payment value", BigDecimal.valueOf(1), details.getPaymentsReceived());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testReceivePayment_negative() {
		AccountDetails details = new AccountDetails(BigDecimal.ZERO);
		details.receivePayment(BigDecimal.valueOf(-1));
	}
	
	
}
