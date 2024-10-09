package dao;

import java.lang.invoke.StringConcatFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.Query;
import com.mysql.cj.x.protobuf.MysqlxCrud.Update;

import entity.CarLoan;
import entity.Customer;
import entity.HomeLoan;
import entity.Loan;
import exception.InvalidLoanException;
import util.DBUtil;

public class LoanRepositoryImpl implements ILoanRepository {

	@Override
	public boolean applyLoan(Loan loan) {
	    
	    
	    String insertCustomerQuery = "INSERT INTO `loan_management_system`.`customer` (`customer_id`, `name`, `email_address`, `phone_number`, `address`, `credit_score`) VALUES (?, ?, ?, ?, ?, ?)";
	    String insertLoanQuery = "INSERT INTO `loan_management_system`.`loan` (`loan_id`, `customer_id`, `principal_amount`, `interest_rate`, `loan_term`, `loan_type`, `loan_status`) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    String insertHomeLoanQuery = "INSERT INTO `loan_management_system`.`homeloan` (`loan_id`, `property_address`, `property_value`) VALUES (?, ?, ?)";
	    String insertCarLoanQuery = "INSERT INTO `loan_management_system`.`carloan` (`loan_id`, `car_model`, `car_value`) VALUES (?, ?, ?)";

	    try 
	    {
	    	Connection connection = DBUtil.getConnection();
	    	Statement statement = connection.createStatement();
	    	PreparedStatement customerStatement = connection.prepareStatement(insertCustomerQuery);
	    	PreparedStatement loanStatement = connection.prepareStatement(insertLoanQuery);
	         PreparedStatement homeLoanStatement = connection.prepareStatement(insertHomeLoanQuery);
	         PreparedStatement carLoanStatement = connection.prepareStatement(insertCarLoanQuery);
	        statement.execute("Set foreign_key_checks =0;");
	        Customer customer = loan.getCustomer();
	        customerStatement.setInt(1, customer.getCustomerId());
	        customerStatement.setString(2, customer.getName());
	        customerStatement.setString(3, customer.getEmailAddress());
	        customerStatement.setString(4, customer.getPhoneNumber());
	        customerStatement.setString(5, customer.getAddress());
	        customerStatement.setInt(6, customer.getCreditScore());
	        customerStatement.executeUpdate();

	        loanStatement.setInt(1, loan.getLoanId());
	        loanStatement.setInt(2, customer.getCustomerId());
	        loanStatement.setDouble(3, loan.getPrincipalAmount());
	        loanStatement.setDouble(4, loan.getInterestRate());
	        loanStatement.setInt(5, loan.getLoanTerm());
	        loanStatement.setString(6, loan.getLoanType());
	        loanStatement.setString(7, loan.getLoanStatus());
	        loanStatement.executeUpdate();

	        if (loan instanceof HomeLoan) {
	            HomeLoan homeLoan = (HomeLoan) loan;
	            homeLoanStatement.setInt(1, homeLoan.getLoanId());
	            homeLoanStatement.setString(2, homeLoan.getPropertyAddress());
	            homeLoanStatement.setDouble(3, homeLoan.getPropertyValue());
	            homeLoanStatement.executeUpdate();
	        } else if (loan instanceof CarLoan) {
	            CarLoan carLoan = (CarLoan) loan;
	            carLoanStatement.setInt(1, carLoan.getLoanId());
	            carLoanStatement.setString(2, carLoan.getCarModel());
	            carLoanStatement.setDouble(3, carLoan.getCarValue());
	            carLoanStatement.executeUpdate();
	        }

	        return true; 
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } 
	    return false; 
	}


	@Override
	public double calculateInterest(int loanId) {
	    String query = "SELECT principal_amount, interest_rate, loan_term FROM loan WHERE loan_id = ?";
	    
	    try (Connection connection = DBUtil.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        
	        preparedStatement.setInt(1, loanId);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        
	        if (resultSet.next()) {
	            double principalAmount = resultSet.getDouble("principal_amount");
	            double interestRate = resultSet.getDouble("interest_rate");
	            int loanTerm = resultSet.getInt("loan_term");
	            
	            return (principalAmount * interestRate * loanTerm) / 12.0;
	        } else {
	            throw new InvalidLoanException("Loan not found with ID: " + loanId);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (InvalidLoanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return 0;
	}

	public double calculateInterest(double principalAmount, double interestRate, int loanTerm) {
	    return (principalAmount * interestRate * loanTerm) / 12.0;
	}


	@Override
	public String loanStatus(int loanId) throws InvalidLoanException {
	    Loan loan = getLoanById(loanId);
	    if (loan == null) {
	        throw new InvalidLoanException("Loan not found.");
	    }

	    int custId = loan.getCustomer().getCustomerId();
	    int creditScore = getCreditScoreFromDatabase(custId);

	    String status;

	    if (creditScore > 650) {
	        status = "Approved";
	        loan.setLoanStatus(status);
	        updateLoanStatusInDatabase(loanId, status);
	    } else {
	        status = "Rejected";
	        loan.setLoanStatus(status);
	        updateLoanStatusInDatabase(loanId, status);
	    }

	    return status;
	}

	private int getCreditScoreFromDatabase(int custId) {
	    String query = "SELECT credit_score FROM customer WHERE customer_id = ?";
	    try (Connection connection = DBUtil.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setInt(1, custId);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        if (resultSet.next()) {
	            return resultSet.getInt("credit_score");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0; 
	}


	private void updateLoanStatusInDatabase(int loanId, String status) {
	    String query = "UPDATE loan SET loan_status = ? WHERE loan_id = ?";
	    try (Connection connection = DBUtil.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, status);
	        preparedStatement.setInt(2, loanId);
	        preparedStatement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public double calculateEMI(int loanId) {
	    String query = "SELECT loan_id, interest_rate, loan_term, principal_amount FROM loan_management_system.loan WHERE loan_id = ?";
	    double emi = 0;

	    try (Connection connection = DBUtil.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	        preparedStatement.setInt(1, loanId);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            double principalAmount = resultSet.getDouble("principal_amount");
	            double annualInterestRate = resultSet.getDouble("interest_rate");
	            int loanTerm = resultSet.getInt("loan_term");

	            double monthlyInterestRate = annualInterestRate / 12 / 100; 

	            
	            if (monthlyInterestRate > 0) {
	                emi = (principalAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm)) /
	                      (Math.pow(1 + monthlyInterestRate, loanTerm) - 1);
	            } else {
	                emi = principalAmount / loanTerm;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return emi; 
	}


	@Override
	public double loanRepayment(int loanId, double amount) {
		Loan loan = getLoanById(loanId);
		double emi = calculateEMI(loanId);
		if(amount < emi) {
			System.out.println("The amount is lesser that EMI amount must be paid more than the EMI");
			return 0;
		}
		else {
				if(updateAmount(loanId, amount)) {
					System.out.println("Amount paid ");
				double balance=	getBalance(loanId);
				return balance;
				} 
		}
		return emi;
			
}

	private double getBalance(int loanId) {
		
	String	Query = "Select principal_amount from loan where loan_id = ?";
	
		Connection connection = DBUtil.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(Query);
			
			preparedStatement.setInt(1, loanId);
			
		ResultSet resultSet =	preparedStatement.executeQuery();
		
		if(resultSet.next()) {
			
			 return	resultSet.getDouble("principal_amount");
		
		}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
		
		
		
	}


	@Override
	public List<Loan> getAllLoan() {
	    List<Loan> loanList = new ArrayList<>();
	    String query = "SELECT * FROM loan";

	    try (Connection connection = DBUtil.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query);
	         ResultSet resultSet = preparedStatement.executeQuery()) {
	        
	        while (resultSet.next()) {
	            int loanId = resultSet.getInt("loan_id");
	            int customerId = resultSet.getInt("customer_id");
	            double principalAmount = resultSet.getDouble("principal_amount");
	            double interestRate = resultSet.getDouble("interest_rate");
	            int loanTerm = resultSet.getInt("loan_term");
	            String loanType = resultSet.getString("loan_type");
	            String loanStatus = resultSet.getString("loan_status");

	            Customer customer = new Customer();
	            customer.setCustomerId(customerId);
	            
	            Loan loan = new Loan(loanId, customer, principalAmount, interestRate, loanTerm, loanType, loanStatus);

	            loanList.add(loan);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return loanList;
	}



	@Override
	public Loan getLoanById(int loanId) {

	    String loanQuery = "SELECT * FROM loan WHERE loan_id = ?";
	    
	    try (Connection connection = DBUtil.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(loanQuery)) {
	        
	        preparedStatement.setInt(1, loanId);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        
	        if (resultSet.next()) {
	            int customerId = resultSet.getInt("customer_id");
	            double principalAmount = resultSet.getDouble("principal_amount");
	            double interestRate = resultSet.getDouble("interest_rate");
	            int loanTerm = resultSet.getInt("loan_term");
	            String loanType = resultSet.getString("loan_type");
	            String loanStatus = resultSet.getString("loan_status");
	            Customer customer = new Customer();
	            customer.setCustomerId(customerId);
	            Loan loan = new Loan(loanId,customer,principalAmount,interestRate,loanTerm,loanType,loanStatus);
	            return loan;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return null;
	}

	public boolean updateAmount(int loanID , double amount)  {
		String query = "UPDATE `loan_management_system`.`loan` SET `principal_amount` = `principal_amount` - ? WHERE (`loan_id` = ?);\r\n";
		try(Connection connection = DBUtil.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);){
			preparedStatement.setDouble(1, amount);
			preparedStatement.setInt(2,loanID);
			int res = preparedStatement.executeUpdate();
			return res==1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}

}
