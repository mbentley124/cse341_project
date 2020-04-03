package database_structures;

public abstract class Account {
  private long accId;
  private double balance;
  private double accInterestRate;

  public Account(long acc_id, double balance, double acc_interest_rate) {
    this.accId = acc_id;
    this.balance = balance;
    this.accInterestRate = acc_interest_rate;
  }

  /**
   * @return the accId
   */
  public long getAccId() {
    return accId;
  }

  /**
   * @return the balance
   */
  public double getBalance() {
    return balance;
  }

  /**
   * @return the accInterestRate
   */
  public double getAccInterestRate() {
    return accInterestRate;
  }

  /**
   * Adjusts the balance then returns the new value.
   * 
   * @param adjustment How much to change the balance by
   * @return The new balance.
   */
  public double adjustBalance(double adjustment) {
    this.balance += adjustment;
    return this.balance;
  }

  public abstract boolean isSavings();

  @Override
  public String toString() {
    String type = this.isSavings() ? "Savings" : "Checking";
    return "Account " + this.accId + " (" + type + ")";
  }
}