package utilities.database_structures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import utilities.ResultSetConverter;

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

  @Override
  public boolean refresh(Connection conn) {
    try (PreparedStatement select = conn
        .prepareStatement("SELECT * FROM account JOIN savings USING (acc_id) WHERE acc_id = ?")) {
      select.setLong(1, this.getAccId());
      List<SavingsAccount> accounts = ResultSetConverter.toSavingsAccounts(select.executeQuery());
      if (accounts.size() > 0) {
        this.balance = accounts.get(0).getBalance();
        this.accInterestRate = accounts.get(0).getAccInterestRate();
        this.minimumBalance = accounts.get(0).getMinimumBalance();
        this.penalty = accounts.get(0).getPenalty();
        return true;
      }
    } catch (SQLException e) {
      // TODO
      e.printStackTrace();
    }
    return false;
  }
}