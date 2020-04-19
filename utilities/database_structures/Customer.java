package utilities.database_structures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    List<Card> card_list = new ArrayList<>();
    try (PreparedStatement select = conn.prepareStatement(
        "SELECT * FROM customer JOIN card on card_holder_id = customer.p_id LEFT OUTER JOIN credit_card using (card_id) LEFT OUTER JOIN debit_card using (card_id) WHERE card_holder_id = ?")) {
      select.setLong(1, this.getPId());
      ResultSet cards = select.executeQuery();
      while (cards.next()) {
        long card_id = cards.getLong("card_id");
        long card_holder_id = cards.getLong("card_holder_id");
        String card_name = cards.getString("card_name");
        Timestamp card_opened_date = cards.getTimestamp("card_opened_date");

        Double credit_interest_rate = cards.getDouble("credit_interest_rate");
        if (cards.wasNull()) {
          // Debit Card
          long acc_id = cards.getLong("acc_id");
          card_list.add(new DebitCard(card_id, card_name, card_holder_id, card_opened_date, acc_id));
        } else {
          // Credit Card
          double credit_limit = cards.getDouble("credit_limit");
          double balance_due = cards.getDouble("balance_due");
          double rolling_balance = cards.getDouble("rolling_balance");
          card_list.add(new CreditCard(card_id, card_name, card_opened_date, card_holder_id, credit_interest_rate,
              credit_limit, balance_due, rolling_balance));
        }
      }
    } catch (SQLException e) {
      // TODO exit quietly
      e.printStackTrace();
    }
    return card_list;
  }

  /**
   * Gets all the accounts that this customer owns.
   * 
   * @param conn     database connection
   * @return A list of all the customers the inputed customer has
   */
  public List<Account> selectAccounts(Connection conn) {
    List<Account> account_list = new ArrayList<>();

    try (PreparedStatement dept_search = conn.prepareStatement(
        "SELECT * FROM account LEFT OUTER JOIN savings USING (acc_id) JOIN account_holder USING (acc_id) where p_id = ?")) {

      dept_search.setLong(1, this.getPId());

      ResultSet account_results = dept_search.executeQuery();

      while (account_results.next()) {
        long acc_id = account_results.getInt("acc_id");
        double balance = account_results.getDouble("balance");
        double acc_interest_rate = account_results.getDouble("acc_interest_rate");
        int minimum_balance = account_results.getInt("minimum_balance");
        int penalty = account_results.getInt("penalty");
        if (account_results.wasNull()) {
          // Checking account (If penalty was null)
          account_list.add(new CheckingAccount(acc_id, balance, acc_interest_rate));
        } else {
          // Savings account
          account_list.add(new SavingsAccount(acc_id, balance, acc_interest_rate, minimum_balance, penalty));
        }
      }
    } catch (SQLException e) {
      // TODO exit quietly.
      e.printStackTrace();
    }
    return account_list;
  }
}