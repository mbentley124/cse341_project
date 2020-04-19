package utilities.database_structures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import utilities.ResultSetConverter;

public class Customer extends Person {

  private Timestamp joinedDate;

  public Customer(long p_id, String full_name, Timestamp joined_date) {
    super(p_id, full_name);
    this.joinedDate = joined_date;
  }

  /**
   * @return the joinedDate
   */
  public Timestamp getJoinedDate() {
    return joinedDate;
  }

  /**
   * Gets the total balance of all the accounts this customer has with the bank.
   * 
   * @param conn Database connection
   * @return The net balance of all the customers accounts. Null if failed to get.
   */
  public Double getNetAccountBalance(Connection conn) {
    try (PreparedStatement statement = conn.prepareStatement(
        "SELECT SUM(balance) net_balance FROM account JOIN account_holder USING (acc_id) WHERE p_id = ?")) {
      statement.setLong(1, this.getPId());
      ResultSet res = statement.executeQuery();
      if (res.next()) {
        return res.getDouble("net_balance");
      } else {
        return 0d;
      }
    } catch (SQLException e) {
      // TODO
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Gets the total amount due for all the loans for the customer of the bank.
   * 
   * @param conn Database connection
   * @return The net loan amount of all a customers loans. Null if failed to get.
   */
  public Double getNetLoanAmountDue(Connection conn) {
    try (PreparedStatement statement = conn
        .prepareStatement("SELECT SUM(amount_due) net_due FROM loan WHERE loanholder_id = ?")) {
      statement.setLong(1, this.getPId());
      ResultSet res = statement.executeQuery();
      if (res.next()) {
        return res.getDouble("net_due");
      } else {
        return 0d;
      }
    } catch (SQLException e) {
      // TODO
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get all the cards that this customer owns, both debit and credit.
   * 
   * @param conn The database connection
   * @return All the cards this customer has with the bank.
   */
  public List<Card> selectCards(Connection conn) {
    List<Card> cards = new ArrayList<>();
    try (PreparedStatement select = conn.prepareStatement(
        "SELECT * FROM customer JOIN card on card_holder_id = customer.p_id LEFT OUTER JOIN credit_card using (card_id) LEFT OUTER JOIN debit_card using (card_id) WHERE card_holder_id = ?")) {
      select.setLong(1, this.getPId());
      cards = ResultSetConverter.toCards(select.executeQuery());
    } catch (SQLException e) {
      // TODO exit quietly
      e.printStackTrace();
    }
    return cards;
  }

  /**
   * Gets all the accounts that this customer owns.
   * 
   * @param conn database connection
   * @return A list of all the customers the inputed customer has
   */
  public List<Account> selectAccounts(Connection conn) {
    List<Account> accounts = new ArrayList<>();

    try (PreparedStatement dept_search = conn.prepareStatement(
        "SELECT * FROM account LEFT OUTER JOIN savings USING (acc_id) JOIN account_holder USING (acc_id) where p_id = ?")) {

      dept_search.setLong(1, this.getPId());

      accounts = ResultSetConverter.toAccounts(dept_search.executeQuery());

    } catch (SQLException e) {
      // TODO exit quietly.
      e.printStackTrace();
    }
    return accounts;
  }
}