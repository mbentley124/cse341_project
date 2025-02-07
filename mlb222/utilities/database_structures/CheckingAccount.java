package mlb222.utilities.database_structures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import mlb222.utilities.ResultSetConverter;

public class CheckingAccount extends Account {
  private int minimumBalance;
  private int penalty;

  public CheckingAccount(long acc_id, double balance, double acc_interest_rate, int minimum_balance, int penalty,
      Timestamp acc_opened_date) {
    super(acc_id, balance, acc_interest_rate, acc_opened_date);
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
    return false;
  }

  @Override
  public boolean refresh(Connection conn) {
    try (PreparedStatement select = conn
        .prepareStatement("SELECT * FROM account JOIN checking USING (acc_id) WHERE acc_id = ?")) {
      select.setLong(1, this.getAccId());
      List<CheckingAccount> accounts = ResultSetConverter.toCheckingAccounts(select.executeQuery());
      if (accounts.size() > 0) {
        this.balance = accounts.get(0).getBalance();
        this.accInterestRate = accounts.get(0).getAccInterestRate();
        this.minimumBalance = accounts.get(0).getMinimumBalance();
        this.penalty = accounts.get(0).getPenalty();
        return true;
      }
    } catch (SQLException e) {
    }
    return false;
  }
}