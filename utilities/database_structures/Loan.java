package utilities.database_structures;

public class Loan {
  private long loanId;
  private long loanApprovedBy;
  private long loanInitialTransactionId;
  long loanholderId;
  double loanInterestRate;
  double amountDue;
  double monthlyPayment;

  public Loan(long l_id, long loanholder_id, long loan_approved_by, long loan_initial_transaction_id,
      double loan_interest_rate, double amount_due, double monthly_payment) {
    this.loanInterestRate = loan_interest_rate;
    this.loanholderId = loanholder_id;
    this.amountDue = amount_due;
    this.monthlyPayment = monthly_payment;
    this.loanId = l_id;
    this.loanApprovedBy = loan_approved_by;
    this.loanInitialTransactionId = loan_initial_transaction_id;
  }

  /**
   * @return the loan Initial Transaction Id
   */
  public long getLoanInitialTransactionId() {
    return loanInitialTransactionId;
  }

  /**
   * @return the loanApprovedBy
   */
  public long getLoanApprovedBy() {
    return loanApprovedBy;
  }

  /**
   * @return the loanId
   */
  public long getLoanId() {
    return loanId;
  }

  /**
   * @return the amountDue
   */
  public double getAmountDue() {
    return amountDue;
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