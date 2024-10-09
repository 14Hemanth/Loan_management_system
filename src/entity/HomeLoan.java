package entity;

public class HomeLoan extends Loan {
    private String propertyAddress;
    private double propertyValue;

    
    public HomeLoan() {}

    
    public HomeLoan(int loanId, Customer customer, double principalAmount, double interestRate, int loanTerm, String loanStatus, String propertyAddress, double propertyValue) {
        super(loanId, customer, principalAmount, interestRate, loanTerm, "HomeLoan", loanStatus);
        this.propertyAddress = propertyAddress;
        this.propertyValue = propertyValue;
    }

    
    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public double getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(double propertyValue) {
        this.propertyValue = propertyValue;
    }
    
    
    @Override
    public String toString() {
        return "HomeLoan{" +
                "loanId=" + loanId +
                ", customer=" + customer +
                ", principalAmount=" + principalAmount +
                ", interestRate=" + interestRate +
                ", loanTerm=" + loanTerm +
                ", loanType='" + loanType + '\'' +
                ", loanStatus='" + loanStatus + '\'' +
                ", propertyAddress='" + propertyAddress + '\'' +
                ", propertyValue=" + propertyValue +
                '}';
    }
}

