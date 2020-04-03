package database_structures;

public class SavingsAccount extends Account {

  private int minimumBalance;
  private int penalty;

  public SavingsAccount(long acc_id, double balance, double acc_interest_rate, int minimum_balance, int penalty) {
    super(acc_id, balance, acc_interest_rate);
    this.minimumBalance = minimum_balance;
    this.penalty = penalty;
  }

  /**
   * @return the minimumBalance
   */
  public int getMinimumBalance() {
    return minimumBalance;
  }

  /**
   * @return the penalty
   */
  public int getPenalty() {
    return penalty;
  }

  @Override
  public boolean isSavings() {
    return true;
  }
}