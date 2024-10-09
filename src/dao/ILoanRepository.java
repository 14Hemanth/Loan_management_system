package dao;

import java.util.List;

import entity.Loan;
import exception.InvalidLoanException;

public interface ILoanRepository {
	
	public boolean applyLoan(Loan loan);
	
	public double calculateInterest(int loanId);
	
	public String	loanStatus(int loanId) throws InvalidLoanException;
	
	public double calculateEMI(int loanId);
	
	public double loanRepayment(int loanId, double amount);
	
	public List<Loan> getAllLoan();
	
	public Loan getLoanById(int loanId);
	
	

}
