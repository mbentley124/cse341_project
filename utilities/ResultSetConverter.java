package utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import utilities.database_structures.Account;
import utilities.database_structures.Card;
import utilities.database_structures.CheckingAccount;
import utilities.database_structures.CreditCard;
import utilities.database_structures.Customer;
import utilities.database_structures.DebitCard;
import utilities.database_structures.Location;
import utilities.database_structures.SavingsAccount;
import utilities.database_structures.Teller;
import utilities.database_structures.Vendor;

/**
 * This class is in charge of converting ResultSet to lists of
 * database_structures.
 */
public class ResultSetConverter {

  public static List<Vendor> toVendors(ResultSet rs) throws SQLException {
    List<Vendor> out = new ArrayList<>();
    while (rs.next()) {
      out.add(new Vendor(rs.getLong("v_id"), rs.getString("vendor_name")));
    }
    return out;
  }

  public static List<Location> toLocations(ResultSet rs) throws SQLException {
    List<Location> out = new ArrayList<>();
    while (rs.next()) {
      out.add(new Location(rs.getLong("loc_id"), rs.getString("loc_name")));
    }
    return out;
  }

  public static List<Customer> toCustomers(ResultSet rs) throws SQLException {
    List<Customer> out = new ArrayList<>();
    while (rs.next()) {
      out.add(new Customer(rs.getLong("p_id"), rs.getString("full_name"), rs.getTimestamp("joined_date")));
    }
    return out;
  }

  public static List<SavingsAccount> toSavingsAccounts(ResultSet rs) throws SQLException {
    List<SavingsAccount> out = new ArrayList<>();
    while (rs.next()) {
      long acc_id = rs.getInt("acc_id");
      double balance = rs.getDouble("balance");
      double acc_interest_rate = rs.getDouble("acc_interest_rate");
      Timestamp opened_date = rs.getTimestamp("acc_opened_date");

      out.add(new SavingsAccount(acc_id, balance, acc_interest_rate, opened_date));
    }
    return out;
  }

  public static List<CreditCard> toCreditCards(ResultSet rs) throws SQLException {
    List<CreditCard> out = new ArrayList<>();
    while (rs.next()) {
      long card_id = rs.getLong("card_id");
      long card_holder_id = rs.getLong("card_holder_id");
      String card_name = rs.getString("card_name");
      Timestamp card_opened_date = rs.getTimestamp("card_opened_date");

      double credit_interest_rate = rs.getDouble("credit_interest_rate");
      double credit_limit = rs.getDouble("credit_limit");
      double balance_due = rs.getDouble("balance_due");
      double rolling_balance = rs.getDouble("rolling_balance");
      long approved_by = rs.getLong("credit_card_approved_by");
      out.add(new CreditCard(card_id, card_name, card_opened_date, card_holder_id, credit_interest_rate, credit_limit,
          balance_due, rolling_balance, approved_by));

    }
    return out;
  }

  public static List<Card> toCards(ResultSet rs) throws SQLException {
    List<Card> out = new ArrayList<>();
    while (rs.next()) {
      long card_id = rs.getLong("card_id");
      long card_holder_id = rs.getLong("card_holder_id");
      String card_name = rs.getString("card_name");
      Timestamp card_opened_date = rs.getTimestamp("card_opened_date");

      Double credit_interest_rate = rs.getDouble("credit_interest_rate");
      if (rs.wasNull()) {
        // Debit Card
        long acc_id = rs.getLong("acc_id");
        out.add(new DebitCard(card_id, card_name, card_holder_id, card_opened_date, acc_id));
      } else {
        // Credit Card
        double credit_limit = rs.getDouble("credit_limit");
        double balance_due = rs.getDouble("balance_due");
        double rolling_balance = rs.getDouble("rolling_balance");
        long approved_by = rs.getLong("credit_card_approved_by");
        out.add(new CreditCard(card_id, card_name, card_opened_date, card_holder_id, credit_interest_rate, credit_limit,
            balance_due, rolling_balance, approved_by));
      }
    }
    return out;
  }

  public static List<Account> toAccounts(ResultSet rs) throws SQLException {
    List<Account> out = new ArrayList<>();
    while (rs.next()) {
      long acc_id = rs.getInt("acc_id");
      double balance = rs.getDouble("balance");
      double acc_interest_rate = rs.getDouble("acc_interest_rate");
      int minimum_balance = rs.getInt("minimum_balance");
      Timestamp opened_date = rs.getTimestamp("acc_opened_date");

      int penalty = rs.getInt("penalty");
      if (rs.wasNull()) {
        // Savings account (If penalty was null)
        out.add(new SavingsAccount(acc_id, balance, acc_interest_rate, opened_date));
      } else {
        // Checking account
        out.add(new CheckingAccount(acc_id, balance, acc_interest_rate, minimum_balance, penalty, opened_date));
      }
    }
    return out;
  }

  public static List<CheckingAccount> toCheckingAccounts(ResultSet rs) throws SQLException {
    List<CheckingAccount> out = new ArrayList<>();
    while (rs.next()) {
      long acc_id = rs.getLong("acc_id");
      double balance = rs.getDouble("balance");
      double acc_interest_rate = rs.getDouble("acc_interest_rate");
      Timestamp opened_date = rs.getTimestamp("acc_opened_date");
      int minimum_balance = rs.getInt("minimum_balance");
      int penalty = rs.getInt("penalty");

      // Checking account
      out.add(new CheckingAccount(acc_id, balance, acc_interest_rate, minimum_balance, penalty, opened_date));
    }
    return out;
  }

  public static List<Teller> toTellers(ResultSet rs) throws SQLException {
    List<Teller> out = new ArrayList<>();

    while (rs.next()) {
      out.add(new Teller(rs.getLong("p_id"), rs.getString("full_name"), rs.getLong("teller_loc_id"),
          rs.getDouble("wage"), rs.getInt("is_atm") == 1));
    }

    return out;
  }
}