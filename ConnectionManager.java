import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database_structures.Account;
import database_structures.CheckingAccount;
import database_structures.Customer;
import database_structures.Location;
import database_structures.SavingsAccount;
import database_structures.Teller;

public class ConnectionManager {

  public static Connection connect() {
    String id = Input.prompt("enter Oracle user id: ");
    String pass = Input.prompt("enter Oracle password for " + id + ": ");
    try {
      return DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", id, pass);
    } catch (SQLException e) {
      System.out.println("connect error. Re-enter login data:");
      return connect();
    }
  }

  /**
   * Gets all the customers with user name as a substring.
   * 
   * @param user_name Name or partial name of customer.
   * @param conn      database connection
   * @return A list of customers with matching names. Case insensitive.
   */
  public static List<Customer> selectCustomers(String user_name, Connection conn) {
    List<Customer> customer_list = new ArrayList<>();

    try (PreparedStatement dept_search = conn
        .prepareStatement("SELECT * FROM person JOIN customer USING (p_id) WHERE LOWER(full_name) LIKE ?")) {
      dept_search.setString(1, "%" + user_name.toLowerCase() + "%");

      ResultSet customer_results = dept_search.executeQuery();

      while (customer_results.next()) {
        customer_list.add(new Customer(customer_results.getLong("p_id"), customer_results.getString("full_name"),
            customer_results.getTimestamp("joined_date")));
      }
    } catch (SQLException e) {
      // TODO exit quietly.
      e.printStackTrace();
    }
    return customer_list;
  }

  /**
   * Gets all the locations.
   * 
   * @param conn database connection
   * @return A list of all the locations
   */
  public static List<Location> selectAllLocations(Connection conn) {
    List<Location> location_list = new ArrayList<>();

    try (PreparedStatement dept_search = conn.prepareStatement("SELECT * FROM location")) {
      ResultSet location_results = dept_search.executeQuery();

      while (location_results.next()) {
        location_list.add(new Location(location_results.getLong("loc_id"), location_results.getString("loc_name")));
      }
    } catch (SQLException e) {
      // TODO exit quietly.
      e.printStackTrace();
    }
    return location_list;
  }

  public static List<Teller> selectLocationTellers(Connection conn, Location loc) {
    List<Teller> teller_list = new ArrayList<>();

    try (PreparedStatement dept_search = conn.prepareStatement(
        "SELECT * FROM person JOIN teller USING (p_id) JOIN location on location.loc_id = teller.teller_loc_id WHERE loc_id = ?")) {
      dept_search.setLong(1, loc.getLocId());
      ResultSet teller_results = dept_search.executeQuery();

      while (teller_results.next()) {
        teller_list.add(new Teller(teller_results.getLong("p_id"), teller_results.getString("full_name"),
            teller_results.getLong("teller_loc_id"), teller_results.getDouble("wage")));
      }
    } catch (SQLException e) {
      // TODO exit quietly.
      e.printStackTrace();
    }
    return teller_list;
  }

  /**
   * Gets all the accounts that a customer owns.
   * 
   * @param customer The customer who's account want to be found.
   * @param conn     database connection
   * @return A list of all the customers the inputed customer has
   */
  public static List<Account> selectUserAccounts(Customer customer, Connection conn) {
    List<Account> account_list = new ArrayList<>();

    try (PreparedStatement dept_search = conn.prepareStatement(
        "SELECT * FROM account LEFT OUTER JOIN savings USING (acc_id) JOIN account_holder USING (acc_id) where p_id = ?")) {

      dept_search.setLong(1, customer.getPId());

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