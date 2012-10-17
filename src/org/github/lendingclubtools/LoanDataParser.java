package org.github.lendingclubtools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class LoanDataParser {

	private final URL loanDataCSV;

	/**
	 * @param loanDataCSV the CSV file holding the downloaded loan data
	 * @throws MalformedURLException 
	 */
	public LoanDataParser(File loanDataCSV) throws MalformedURLException {
		this.loanDataCSV = loanDataCSV.toURL();
	}
	
	public LoanDataParser(URL url) {
		this.loanDataCSV = url;
	}
	
	public LoanData[] readData() throws IOException, ParseException {
		List<LoanData> allLoans = new ArrayList<LoanData>();
	
		InputStream fileInput = loanDataCSV.openStream();
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(fileInput, "ASCII"));

			// Skip the first line, a header
			reader.readNext();
			
			// Build the line parser from the header data
			LoanLineParser lineParser = new LoanLineParser(reader.readNext());
			
			try {
				String[] line;
				
				while ((line = reader.readNext()) != null) {
					if (line.length == 1 && line[0].trim().length() == 0) {
						// Skip blank lines
						continue;
					}
					
					if (line.length == 2 && line[0].trim().equals("Loans that do not meet the current credit policy")) {
						// Skip the warning about old loans not meeting credit policy
						continue;
					}
					
					LoanData loan = lineParser.parseLine(line);
					allLoans.add(loan);
				}
				
				BigDecimal totalFunded=new BigDecimal(0);
				for (LoanData data : allLoans) {
					totalFunded = totalFunded.add(data.getAmount());
				}
				
				System.out.println(String.format("Found %d found totalling $%f", allLoans.size(), totalFunded));
			} finally {
				reader.close();
			}
		} finally {
			fileInput.close();
		}
		
		return allLoans.toArray(new LoanData[allLoans.size()]);
	}
	
}

class LoanLineParser {
	
	private final int loanIdColumn;
	private final int amountColumn; 
	private final int rateColumn;
	private final int issueDateColumn;
	private final int loanLengthColumn;
	private final int paymentsToDateColumn; 
	private final int applicationStartDateColumn;
	private final int applicationEndDateColumn;
	private final int monthlyPaymentColumn;
	
	private final SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public LoanLineParser(String[] headers) {
		Map<String, Integer> headerToColumn = new HashMap<String, Integer>(headers.length * 2); 
		for (int i=0;i<headers.length;i++) {
			headerToColumn.put(headers[i].trim(), i);
		}
		
		loanIdColumn = headerToColumn.get("Loan ID");
		amountColumn = headerToColumn.get("Amount Requested");
		rateColumn = headerToColumn.get("Interest Rate");
		issueDateColumn = headerToColumn.get("Issued Date");
		loanLengthColumn = headerToColumn.get("Loan Length");
		paymentsToDateColumn = headerToColumn.get("Payments To Date");
		
		applicationStartDateColumn = headerToColumn.get("Application Date");
		applicationEndDateColumn = headerToColumn.get("Application Expiration Date");
		monthlyPaymentColumn = headerToColumn.get("Monthly PAYMENT");
	}
	
	public LoanData parseLine(String[] lineData) throws ParseException {
		int loanId = Integer.parseInt(lineData[loanIdColumn].trim());
		try {
			BigDecimal amount = new BigDecimal(lineData[amountColumn].trim());
			BigDecimal rate = new BigDecimal(lineData[rateColumn].trim().replace("%", ""));
			Date issueDate = parseIssueDate(lineData[issueDateColumn].trim());
			int loanLength = decodeLoanLength(lineData[loanLengthColumn].trim());
			BigDecimal paymentsToDate = new BigDecimal(lineData[paymentsToDateColumn].trim());
			Date applicationStartDate = parseIssueDate(lineData[applicationStartDateColumn].trim());
			Date applicationEndDate = parseIssueDate(lineData[applicationEndDateColumn].trim());
			BigDecimal monthlyPayment = new BigDecimal(lineData[monthlyPaymentColumn].trim());
			
			return new LoanData(loanId, amount, rate, issueDate, loanLength, paymentsToDate, applicationStartDate, applicationEndDate, monthlyPayment);
		} catch (ParseException e) {
			ParseException parseException = new ParseException(String.format("Failed to parse the line for loan id %d", loanId), 0);
			parseException.initCause(e);
			throw parseException;
		}
	}
	
	private Date parseIssueDate(String string) throws ParseException {
		if (string.length() == 0)
			return null;
		
		return simpleDayFormat.parse(string);
	}
	
	private int decodeLoanLength(String string) throws ParseException {
		if (string.endsWith(" months")) {
			return Integer.parseInt(string.replace(" months", ""));
		} else {
			throw new ParseException("Unparsable loan length: " + string, 0);
		}
	}
	
}
