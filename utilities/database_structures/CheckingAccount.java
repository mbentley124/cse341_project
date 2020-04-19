package utilities.database_structures;

public class CheckingAccount extends Account {
  public CheckingAccount(long acc_id, double balance, double acc_interest_rate) {
    super(acc_id, balance, acc_interest_rate);
  }

  @Override
  public boolean isSavings() {
    return false;
  }
}