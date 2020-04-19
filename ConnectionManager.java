import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database_structures.Account;
import database_structures.Card;
import database_structures.CheckingAccount;
import database_structures.CreditCard;
import database_structures.Customer;
import database_structures.DebitCard;
import database_structures.Location;
import database_structures.SavingsAccount;
import database_structures.Teller;
import database_structures.Vendor;

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
   * Gets the total balance of all the accounts the customer has with the bank.
   * 
   * @param conn     Database connection
   * @param customer The customer to get the net
   * @return The net balance of all the customers accounts. Null if failed to get.
   */
  public static Double getNetCustomerAccountBalance(Connection conn, Customer customer) {
    try (PreparedStatement statement = conn.prepareStatement(
        "SELECT SUM(balance) net_balance FROM account JOIN account_holder USING (acc_id) WHERE p_id = ?")) {
      statement.setLong(1, customer.getPId());
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
   * Gets the total amount due for all the loans for a customer of the bank.
   * 
   * @param conn     Database connection
   * @param customer The customer to get the net
   * @return The net loan amount of all a customers loans. Null if failed to get.
   */
  public static Double getNetCustomerLoanAmountDue(Connection conn, Customer customer) {
    try (PreparedStatement statement = conn
        .prepareStatement("SELECT SUM(amount_due) net_due FROM loan WHERE loanholder_id = ?")) {
      statement.setLong(1, customer.getPId());
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

  public static List<Vendor> selectVendors(String vendor_name, Connection conn) {
    List<Vendor> vendor_list = new ArrayList<>();
    try (PreparedStatement select = conn.prepareStatement("SELECT * FROM vendor WHERE LOWER(vendor_name) LIKE ?")) {
      select.setString(1, "%" + vendor_name.toLowerCase() + "%");

      ResultSet results = select.executeQuery();

      while (results.next()) {
        vendor_list.add(new Vendor(results.getLong("v_id"), results.getString("vendor_name")));
      }
    } catch (SQLException e) {
      // TODO exit quietly
      e.printStackTrace();
    }
    return vendor_list;
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

  public static List<Card> selectCustomerCards(Connection conn, Customer customer) {
    List<Card> card_list = new ArrayList<>();
    try (PreparedStatement select = conn.prepareStatement(
        "SELECT * FROM customer JOIN card on card_holder_id = customer.p_id LEFT OUTER JOIN credit_card using (card_id) LEFT OUTER JOIN debit_card using (card_id) WHERE card_holder_id = ?")) {
      select.setLong(1, customer.getPId());
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

  public static boolean purchaseCreditCard(double amount, Customer customer, CreditCard card, Vendor vendor,
      Connection conn) {
    boolean success = chargeCreditCard(amount, card, conn);
    long t_id = insertTransaction(amount, now(), conn);
    success = success && (t_id != -1);
    success = success && insertCardPurchase(t_id, card.getCardId(), vendor.getVId(), conn);
    try {
      if (success) {
        conn.commit();
      } else {
        conn.rollback();
      }
    } catch (SQLException e) {
      // TODO
      e.printStackTrace();
      return false;
    }
    return success;
  }

  public static boolean chargeCreditCard(double amount, CreditCard card, Connection conn) {
    try (CallableStatement adjust_balance = conn.prepareCall("{call creditCardPurchase (?, ?)}")) {
      adjust_balance.setLong(1, card.getCardId());
      adjust_balance.setDouble(2, amount);
      adjust_balance.execute();
      return true;
    } catch (SQLException e) {
      // TODO
      e.printStackTrace();
      return false;
    }
  }

  public static boolean purchaseDebitCard(double amount, Customer customer, DebitCard card, Vendor vendor,
      Connection conn) {
    // Can't be a penalty since accounts must be checking
    boolean success = -1 != accountIdWithdrawBalance(amount, card.getAccId(), conn);
    long t_id = insertTransaction(amount, now(), conn);
    success = success && (t_id != -1);
    success = success && insertCardPurchase(t_id, card.getCardId(), vendor.getVId(), conn);
    try {
      if (success) {
        conn.commit();
      } else {
        conn.rollback();
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      return false;
    }
    return success;
  }

  public static int cashWithdraw(double amount, Timestamp t_date, Location loc, Teller teller, Account account,
      Connection conn) {
    int penalty = accountWithdrawBalance(amount, account, conn);
    boolean success = penalty != -1;

    long t_id = insertTransaction(amount, t_date, conn);

    success = success && (t_id != -1);

    success = success && insertCashTransaction(t_id, loc.getLocId(), conn);
    success = success && insertAccountWithdraw(t_id, account.getAccId(), teller.getPId(), conn);

    try {
      if (success) {
        conn.commit();
      } else {
        conn.rollback();
        return -1;
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return -1;
    }
    return penalty;
  }

  public static int accountWithdrawBalance(double amount, Account account, Connection conn) {
    return accountIdWithdrawBalance(amount, account.getAccId(), conn);
  }

  public static int accountIdWithdrawBalance(double amount, long account_id, Connection conn) {
    int penalty = -1;
    try (CallableStatement adjust_balance = conn.prepareCall("{? = call accountWithdraw (?, ?)}")) {
      adjust_balance.registerOutParameter(1, Types.INTEGER);
      adjust_balance.setLong(2, account_id);
      adjust_balance.setDouble(3, amount);
      adjust_balance.execute();
      penalty = adjust_balance.getInt(1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return penalty;
  }

  public static int accountTransfer(double amount, Timestamp t_date, Location loc, Teller teller, Account to_account,
      Account from_account, Connection conn) {

    boolean success = accountDepositBalance(amount, to_account, conn);
    int penalty = accountWithdrawBalance(amount, from_account, conn);
    success = success && (penalty != -1);

    long t_id = insertTransaction(amount, t_date, conn);
    success = success && (t_id != -1);

    success = success && insertAccountDeposit(t_id, to_account.getAccId(), teller.getPId(), conn);
    success = success && insertAccountWithdraw(t_id, from_account.getAccId(), teller.getPId(), conn);
    try {
      if (success) {
        conn.commit();
      } else {
        conn.rollback();
        return -1;
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return -1;
    }
    return penalty;
  }

  public static Account selectDebitCardAccount(DebitCard card, Connection conn) {
    Account account = null;
    try (PreparedStatement select = conn.prepareStatement(
        "SELECT * FROM account LEFT OUTER JOIN checking USING (acc_id) LEFT OUTER JOIN savings USING (acc_id) WHERE acc_id = ?")) {
      select.setLong(1, card.getAccId());
      ResultSet res = select.executeQuery();
      if (res.next()) {
        long acc_id = res.getLong("acc_id");
        double balance = res.getDouble("balance");
        double acc_interest_rate = res.getDouble("acc_interest_rate");
        Integer minimum_balance = res.getInt("minimum_balance");
        if (res.wasNull()) {
          // Checking account
          account = new CheckingAccount(acc_id, balance, acc_interest_rate);
        } else {
          // Savings account
          int penalty = res.getInt("penalty");
          account = new SavingsAccount(acc_id, balance, acc_interest_rate, minimum_balance, penalty);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return account;
  }

  public static long cashDeposit(double amount, Timestamp t_date, Location loc, Teller teller, Account account,
      Connection conn) {
    boolean success = accountDepositBalance(amount, account, conn);
    long t_id = insertTransaction(amount, t_date, conn);
    success = success && (t_id != -1);
    success = success && insertCashTransaction(t_id, loc.getLocId(), conn);
    success = success && insertAccountDeposit(t_id, account.getAccId(), teller.getPId(), conn);
    try {
      if (success) {
        conn.commit();
      } else {
        conn.rollback();
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return t_id;
  }

  public static boolean accountDepositBalance(double amount, Account account, Connection conn) {
    try (CallableStatement adjust_balance = conn.prepareCall("{call accountDeposit (?, ?)}")) {
      adjust_balance.setLong(1, account.getAccId());
      adjust_balance.setDouble(2, amount);
      adjust_balance.execute();
      return true;
    } catch (SQLException e) {
      // TODO
      e.printStackTrace();
      return false;
    }
  }

  public static long insertTransaction(double amount, Timestamp t_date, Connection conn) {
    try (PreparedStatement insert_transaction = conn
        .prepareStatement("INSERT INTO transaction (amount, t_date) VALUES (?, ?)", new String[] { "t_id" })) {
      insert_transaction.setDouble(1, amount);
      insert_transaction.setTimestamp(2, t_date);
      insert_transaction.execute();
      ResultSet results = insert_transaction.getGeneratedKeys();
      results.next();
      return results.getLong(1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  public static boolean insertCashTransaction(long t_id, long loc_id, Connection conn) {
    try (PreparedStatement insert = conn.prepareStatement("INSERT INTO cash_transaction VALUES (?, ?)")) {
      insert.setLong(1, t_id);
      insert.setLong(2, loc_id);
      insert.execute();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean insertAccountDeposit(long t_id, long acc_id, long teller_id, Connection conn) {
    try (PreparedStatement insert = conn.prepareStatement("INSERT INTO account_deposit VALUES (?, ?, ?)")) {
      insert.setLong(1, t_id);
      insert.setLong(2, acc_id);
      insert.setLong(3, teller_id);
      insert.execute();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean insertAccountWithdraw(long t_id, long acc_id, long teller_id, Connection conn) {
    try (PreparedStatement insert = conn.prepareStatement("INSERT INTO account_withdraw VALUES (?, ?, ?)")) {
      insert.setLong(1, t_id);
      insert.setLong(2, acc_id);
      insert.setLong(3, teller_id);
      insert.execute();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean insertCardPurchase(long t_id, long card_id, long v_id, Connection conn) {
    try (PreparedStatement insert = conn.prepareStatement("INSERT INTO card_purchase VALUES (?, ?, ?)")) {
      insert.setLong(1, t_id);
      insert.setLong(2, card_id);
      insert.setLong(3, v_id);
      insert.execute();
      return true;
    } catch (Exception e) {
      // e.printStackTrace();
      return false;
    }
  }

  public static long insertLoan(long loanholder_id, double interest_rate, double loan_amount, double amount_due,
      double monthly_payment, String collatoral, Connection conn) {
    try (PreparedStatement insert_loan = conn.prepareStatement(
        "INSERT INTO loan (loanholder_id, loan_interest_rate, amount_loaned, amount_due, monthly_payment) VALUES (?, ?, ?, ?, ?)",
        new String[] { "l_id" });
        PreparedStatement insert_collatoral = conn.prepareStatement("INSERT INTO secured_loan VALUES (?, ?)")) {
      insert_loan.setLong(1, loanholder_id);
      insert_loan.setDouble(2, interest_rate);
      insert_loan.setDouble(3, loan_amount);
      insert_loan.setDouble(4, amount_due);
      insert_loan.setDouble(5, monthly_payment);
      insert_loan.execute();
      ResultSet results = insert_loan.getGeneratedKeys();
      results.next();
      long loan_id = results.getLong(1);
      if (collatoral != null) {
        insert_collatoral.setLong(1, loan_id);
        insert_collatoral.setString(2, collatoral);
        insert_collatoral.execute();
      }
      return loan_id;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  public static Timestamp now() {
    return new Timestamp(new Date().getTime());
  }
}