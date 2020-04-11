package database_structures;

import java.sql.Timestamp;

public class DebitCard extends Card {
  private long accId;

  public DebitCard(long card_id, String card_name, Timestamp card_opened_date, long acc_id) {
    super(card_id, card_name, card_opened_date);
    this.accId = acc_id;
  }

  /**
   * @return the accId
   */
  public long getAccId() {
    return accId;
  }

  @Override
  protected String cardType() {
    return "Debit";
  }
}