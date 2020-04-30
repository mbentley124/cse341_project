package mlb222.utilities.database_structures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import mlb222.utilities.ResultSetConverter;

public class DebitCard extends Card {
  private long accId;

  public DebitCard(long card_id, String card_name, long card_holder_id, Timestamp card_opened_date, long acc_id) {
    super(card_id, card_name, card_holder_id, card_opened_date);
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

  /**
   * Gets the checking account that this card goes with.
   * 
   * @param conn The db connection.
   * @return The checking account this card goes with. Null if there is none for
   *         some reason.
   */
  public CheckingAccount selectAccount(Connection conn) {
    CheckingAccount account = null;
    try (PreparedStatement select = conn.prepareStatement(
        "SELECT * FROM account LEFT OUTER JOIN checking USING (acc_id) LEFT OUTER JOIN savings USING (acc_id) WHERE acc_id = ?")) {
      select.setLong(1, this.getAccId());
      List<CheckingAccount> accounts = ResultSetConverter.toCheckingAccounts(select.executeQuery());
      // If the checking account exists
      if (accounts.size() > 0) {
        account = accounts.get(0);
      }
    } catch (SQLException e) {
      // e.printStackTrace();
      return null;
    }
    return account;
  }
}