package main;

import java.util.List;
import java.util.Scanner;


import dao.LoanRepositoryImpl;
import entity.CarLoan;
import entity.Customer;
import entity.HomeLoan;
import entity.Loan;
import exception.InvalidLoanException;

public class LoanManagemant {
	final static LoanRepositoryImpl loanRepositoryImpl = new LoanRepositoryImpl();
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to Loan management system !!!");
		System.out.println("Please enter the following option\r\n"
				+ "\r\n1. Apply Home Loan"
				+ "\r\n2. Apply Car Loan"
				+ "\r\n3. Get all Loan"
				+ "\r\n4. Get Loan "
				+ "\r\n5. Loan Repayment"
				+ "\r\n6. Exit"
				
				);
		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();
		switch(choice) {
		case 1:
			applyForLoan(scanner, loanRepositoryImpl,1);
			break;
		case 2:
			applyForLoan(scanner, loanRepositoryImpl,2);
			break;
		case 3:
			getAllLoan(scanner);
			break;
		case 4:
			getloan(scanner,loanRepositoryImpl);
			break;
		case 5:
			loanRepayment(scanner , loanRepositoryImpl);
			break;	
		case 6:
			scanner.close();
			
		default:
			System.err.println("Inavlid option");
		
		
		}
		
		
	}

	private static void loanRepayment(Scanner scanner, LoanRepositoryImpl loanrepository) {
		System.out.println("Enter the loan ID :");
		int id =	scanner.nextInt();
		System.out.print("Enter the amount you need to pay :");
		double amount = scanner.nextDouble();
		
		double remaining_amount =loanrepository.loanRepayment(id,amount);
		System.out.println("The amount left is " + remaining_amount);
	}

	private static void getAllLoan(Scanner scanner) {
		List<Loan> loans = loanRepositoryImpl.getAllLoan();
		for (Loan loan : loans) {
			System.out.println(loan);
		}
	}

	private static void showStatus(Scanner scanner, LoanRepositoryImpl loanrepo) {
		
		System.out.println("Enter Loan Id");
		int loanId = scanner.nextInt();
		
		
		try {
		String stauString =	loanrepo.loanStatus(loanId);
		System.out.println("Your Loan Status is:"+stauString);
		} catch (InvalidLoanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}		

	private static void getloan(Scanner scanner, LoanRepositoryImpl loanrepo) {
		System.out.println("Enter Loan Id");
		int loanId = scanner.nextInt();
		Loan loan = loanrepo.getLoanById(loanId);
		System.out.println(loan);
		
	}

	private static void applyForLoan(Scanner scanner, LoanRepositoryImpl loanRepo,int loantype) {
	    System.out.println("Applying for Home Loan");
	    System.out.println("Please provide the following valid details:");

	    
	    System.out.print("Customer ID: ");
	    int customerId = scanner.nextInt();
	    scanner.nextLine();
	    
	    System.out.print("Name: ");
	    String name = scanner.nextLine();

	    System.out.print("Email Address: ");
	    String emailAddress = scanner.nextLine();

	    System.out.print("Phone Number: ");
	    String phoneNumber = scanner.nextLine();

	    System.out.print("Address: ");
	    String address = scanner.nextLine();

	    System.out.print("Credit Score: ");
	    int creditScore = scanner.nextInt();

	    
	    Customer customer = new Customer(customerId, name, emailAddress, phoneNumber, address, creditScore);

	    
	    System.out.print("Principal Amount: ");
	    double principalAmount = scanner.nextDouble();
 
	    System.out.print("Loan Term (months): ");
	    int loanTerm = scanner.nextInt();
	    
	    int loanid = (int) (Math.random()*10000);
	    scanner.nextLine(); 
	    if(loantype==1) {
	    	System.out.print("Property Address: ");
		    String propertyAddress = scanner.nextLine();

		    System.out.print("Property Value: ");
		    double propertyValue = scanner.nextDouble();
		    
		    double interestRate=0.5;
		    
		    HomeLoan homeLoan = new HomeLoan(loanid, customer, principalAmount, interestRate, loanTerm, "Pending", propertyAddress, propertyValue);
		    
		    if(loanRepo.applyLoan(homeLoan)) {
			    System.out.println("Home Loan applied successfully!");
			    Double intrestDouble = loanRepo.calculateInterest(loanid);
			    System.out.println("The intrest is " + intrestDouble);
			    try {
					String loanstatuString = loanRepo.loanStatus(loanid);
					System.out.println("Loan status is :" + loanstatuString);
				} catch (InvalidLoanException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    } else {
			    	System.out.println("Some Problem Occured");
			    }
	    	
	    } else {
	    	
	    	System.out.print("CarModel: ");
		    String carModel = scanner.nextLine();

		    System.out.print("Car Value: ");
		    double carValue = scanner.nextDouble();
		    double interestRate= 0.10;
	    	CarLoan carLoan = new CarLoan(loanid, customer, principalAmount, interestRate, loanTerm, "Pending ", carModel, carValue);
	    	
	    	if(loanRepo.applyLoan(carLoan)) {
	    		System.out.println("Car loan has been applied ");
	    		double intrest = loanRepo.calculateInterest(loanid);
	    		System.out.println("The loan intrest is : ");
	    		loanRepo.calculateInterest(loanid);
	    	} else {
	    		System.out.println("Some problem occured ");
	    	}
	    
	    }
	    
	}
	
	

	

}
