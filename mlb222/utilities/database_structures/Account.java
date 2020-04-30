package mlb222.utilities.database_structures;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import mlb222.utilities.ConnectionManager;

public abstract class Account {
  private long accId;
  private Timestamp accOpenedDate;
  protected double balance;
  protected double accInterestRate;

  public Account(long acc_id, double balance, double acc_interest_rate, Timestamp acc_opened_date) {
    this.accId = acc_id;
    this.balance = balance;
    this.accInterestRate = acc_interest_rate;
    this.accOpenedDate = acc_opened_date;
  }

  /**
   * @return the accId
   */
  public long getAccId() {
    return accId;
  }

  /**
   * @return the account Opened Date
   */
  public Timestamp getAccOpenedDate() {
    return accOpenedDate;
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
   * Refreshes the values in this object to what is stored in the db.
   * 
   * @return True if succeeded.
   */
  public abstract boolean refresh(Connection conn);

  /**
   * 
   * @return True if account is a savings account
   */
  public abstract boolean isSavings();

  @Override
  public String toString() {
    String type = this.isSavings() ? "Savings" : "Checking";
    return "Account " + this.accId + " (" + type + ")";
  }

  /**
   * Same as
   * {@link ConnectionManager#accountIdWithdrawBalance(double, long, Connection)}
   * but using this account.
   */
  public int dbWithdrawBalance(double amount, Connection conn) {
    return ConnectionManager.accountIdWithdrawBalance(amount, this.getAccId(), conn);
  }

  /**
   * Adjusts the balance of this account to represent a deposit into the account.
   * 
   * Does NOT commit the transaction.
   * 
   * @param amount The amount deposited
   * @param conn   The db connection.
   * @return True is succeeded.
   */
  public boolean dbDepositBalance(double amount, Connection conn) {
    try (CallableStatement adjust_balance = conn.prepareCall("{call accountDeposit (?, ?)}")) {
      adjust_balance.setLong(1, this.getAccId());
      adjust_balance.setDouble(2, amount);
      adjust_balance.execute();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }
}