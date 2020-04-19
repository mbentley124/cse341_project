package database_structures;

public class Loan {
  long loanholderId;
  double loanInterestRate;
  double amountLoaned;
  double amountDue;
  double monthlyPayment;

  public Loan(long loanholder_id, double loan_interest_rate, double amount_loaned, double amount_due, double monthly_payment) {
    this.loanInterestRate = loan_interest_rate;
    this.loanholderId = loanholder_id;
    this.amountDue = amount_due;
    this.amountLoaned = amount_loaned;
    this.monthlyPayment = monthly_payment;
  }

  /**
   * @return the amountDue
   */
  public double getAmountDue() {
    return amountDue;
  }

  /**
   * @return the amountLoaned
   */
  public double getAmountLoaned() {
    return amountLoaned;
  }

  /**
   * @return the loanInterestRate
   */
  public double getLoanInterestRate() {
    return loanInterestRate;
  }

  /**
   * @return the loanholderId
   */
  public long getLoanholderId() {
    return loanholderId;
  }

  /**
   * @return the monthlyPayment
   */
  public double getMonthlyPayment() {
    return monthlyPayment;
  }
}