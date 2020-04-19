package utilities.database_structures;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CreditCard extends Card {

  private double creditInterestRate;
  private double creditLimit;
  private double balanceDue;
  private double rollingBalance;

  public CreditCard(long card_id, String card_name, Timestamp card_opened_date, long card_holder_id,
      double credit_interest_rate, double credit_limit, double balance_due, double rolling_balance) {
    super(card_id, card_name, card_holder_id, card_opened_date);
    this.creditInterestRate = credit_interest_rate;
    this.creditLimit = credit_limit;
    this.balanceDue = balance_due;
    this.rollingBalance = rolling_balance;
  }

  // TODO need to replace this with a method that just resets everything in the
  // object to what is stored in the db.
  public double adjustRollingBalance(double adjustment) {
    this.rollingBalance += adjustment;
    return this.getRollingBalance();
  }

  /**
   * @return the creditInterestRate
   */
  public double getCreditInterestRate() {
    return creditInterestRate;
  }

  /**
   * @return the creditLimit
   */
  public double getCreditLimit() {
    return creditLimit;
  }

  /**
   * @return the balanceDue
   */
  public double getBalanceDue() {
    return balanceDue;
  }

  /**
   * @return the rollingBalance
   */
  public double getRollingBalance() {
    return rollingBalance;
  }

  @Override
  protected String cardType() {
    return "Credit";
  }

  /**
   * Charges this credit card, by adjusting the balance on the database.
   * 
   * Does NOT commit the database insertion.
   * 
   * @param amount The amount to charge
   * @param conn   The database connection
   * @return True if succeeded
   */
  public boolean chargeCard(double amount, Connection conn) {
    try (CallableStatement adjust_balance = conn.prepareCall("{call creditCardPurchase (?, ?)}")) {
      adjust_balance.setLong(1, this.getCardId());
      adjust_balance.setDouble(2, amount);
      adjust_balance.execute();
      return true;
    } catch (SQLException e) {
      // TODO
      e.printStackTrace();
      return false;
    }
  }
}