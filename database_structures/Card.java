package database_structures;

import java.sql.Timestamp;

public abstract class Card {
  private long cardId;
  private long cardHolderId;
  private String cardName;
  private Timestamp cardOpenedDate;

  public Card(long card_id, String card_name, long card_holder_id, Timestamp card_opened_date) {
    this.cardId = card_id;
    this.cardName = card_name;
    this.cardOpenedDate = card_opened_date;
    this.cardHolderId = card_holder_id;
  }

  /**
   * @return the cardId
   */
  public long getCardId() {
    return cardId;
  }

  /**
   * @return the cardName
   */
  public String getCardName() {
    return cardName;
  }

  /**
   * @return the cardHolderId
   */
  public long getCardHolderId() {
    return cardHolderId;
  }

  /**
   * @return the cardOpenedDate
   */
  public Timestamp getCardOpenedDate() {
    return cardOpenedDate;
  }

  protected abstract String cardType();

  @Override
  public String toString() {
    return this.getCardName() + " (" + this.cardType() + " Card)";
  }
}