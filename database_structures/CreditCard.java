package database_structures;

import java.sql.Timestamp;

public class CreditCard extends Card {

  long cardHolderId;
  double creditInterestRate;
  double creditLimit;
  double balanceDue;
  double rollingBalance;

  public CreditCard(long card_id, String card_name, Timestamp card_opened_date, long card_holder_id,
      double credit_interest_rate, double credit_limit, double balance_due, double rolling_balance) {
    super(card_id, card_name, card_opened_date);
    this.cardHolderId = card_holder_id;
    this.creditInterestRate = credit_interest_rate;
    this.creditLimit = credit_limit;
    this.balanceDue = balance_due;
    this.rollingBalance = rolling_balance;
  }

  /**
   * @return the cardHolderId
   */
  public long getCardHolderId() {
    return cardHolderId;
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
}