package utilities;

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

import utilities.database_structures.Account;
import utilities.database_structures.CreditCard;
import utilities.database_structures.Customer;
import utilities.database_structures.DebitCard;
import utilities.database_structures.Location;
import utilities.database_structures.Teller;
import utilities.database_structures.Vendor;

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
    List<Customer> customers = new ArrayList<>();

    try (PreparedStatement search = conn
        .prepareStatement("SELECT * FROM person JOIN customer USING (p_id) WHERE LOWER(full_name) LIKE ?")) {
      search.setString(1, "%" + user_name.toLowerCase() + "%");

      customers = ResultSetConverter.toCustomers(search.executeQuery());
    } catch (SQLException e) {
      // TODO exit quietly.
      e.printStackTrace();
    }
    return customers;
  }

  /**
   * Searches all the vendors by a substring. Case insensitive.
   * 
   * @param vendor_name A substring for the name of the vendor
   * @param conn        The database connection
   * @return All the vendors who's name contains the substring Case insensitive.
   */
  public static List<Vendor> selectVendors(String vendor_name, Connection conn) {
    List<Vendor> vendor_list = new ArrayList<>();
    try (PreparedStatement select = conn.prepareStatement("SELECT * FROM vendor WHERE LOWER(vendor_name) LIKE ?")) {
      select.setString(1, "%" + vendor_name.toLowerCase() + "%");

      vendor_list = ResultSetConverter.toVendors(select.executeQuery());
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
    List<Location> locations = new ArrayList<>();

    try (PreparedStatement dept_search = conn.prepareStatement("SELECT * FROM location")) {
      locations = ResultSetConverter.toLocations(dept_search.executeQuery());
    } catch (SQLException e) {
      // TODO exit quietly.
      e.printStackTrace();
    }
    return locations;
  }

  /**
   * Inserts the relevant parts of a credit card transaction into the database.
   * Also charges the credit card for the purchase. Commits the transaction.
   * 
   * @param amount   The size of the purchase
   * @param customer The customer making the purchases
   * @param card     The credit card they are using to make the purchase
   * @param vendor   The vendor they are making the purchase at
   * @param conn     The database connection
   * @return True if it purchase insertion suceeded.
   */
  public static boolean purchaseCreditCard(double amount, Customer customer, CreditCard card, Vendor vendor,
      Connection conn) {
    boolean success = card.chargeCard(amount, conn);
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

  /**
   * Makes a purchase on a debit card, inserting all the relevant transaction
   * tables and adjusting the balance on the cards checking account. Commits the
   * transaction. (transaction timestamp is set to the current time)
   * 
   * @param amount The size of the purchase
   * @param card   The debit card the customer is using to make the purchase
   * @param vendor The vendor the purchase is being made at
   * @param conn   The db connection
   * @return True if insertion was succesfully commited.
   */
  public static boolean purchaseDebitCard(double amount, DebitCard card, Vendor vendor, Connection conn) {
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

  /**
   * Inserts the relevant database entries for a withdraw from an account into
   * cash. Transaction timestamp is set to the current time. Commits the
   * transaction.
   * 
   * @param amount  The amount they withdraw
   * @param loc     The location they are making the withdraw at.
   * @param teller  The teller that is processing the withdrawal.
   * @param account The account they are withdrawing with (can be checking or
   *                savings)
   * @param conn    The connection to the db
   * @return The penalty for the withdrawal. -1 if db insertion failed (can be due
   *         to too few funds in account). There is never a penalty if it is a
   *         checking account.
   */
  public static int cashWithdraw(double amount, Location loc, Teller teller, Account account, Connection conn) {
    int penalty = account.dbWithdrawBalance(amount, conn);
    boolean success = penalty != -1;

    long t_id = insertTransaction(amount, now(), conn);

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

  /**
   * Removes money from the balance of an account in the database.
   * 
   * Does NOT commit transaction.
   * 
   * @param amount     The amount to withdraw
   * @param account_id The id of the account to withdraw money from
   * @param conn       The db connection
   * @return The penalty for withdrawal. Always 0 for checking accounts. -1 if the
   *         method failed.
   */
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

  /**
   * Inserts the necesary db entries to represent transfering money from one
   * account into another. Also adjusts the balance of the accounts and commits
   * the transaction.
   * 
   * @param amount       The amount to transfer
   * @param loc          The location this money transfer is happening at.
   * @param teller       The teller performing the transfer
   * @param to_account   The account the money is going to
   * @param from_account The account the money is coming from (may incur a penalty
   *                     if falls below minimum balance).
   * @param conn         The db connection
   * @return The penalty on the from_account. 0 if it is a savings account. -1 if
   *         the transaction failed and was rolled back (may be caused by
   *         insufficient funds).
   */
  public static int accountTransfer(double amount, Location loc, Teller teller, Account to_account,
      Account from_account, Connection conn) {

    boolean success = to_account.dbDepositBalance(amount, conn);
    int penalty = from_account.dbWithdrawBalance(amount, conn);
    success = success && (penalty != -1);

    long t_id = insertTransaction(amount, now(), conn);
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

  /**
   * Inserts the relevant entries to represent a cash deposit into an account.
   * Also adjusts the balance of the account. Transaction timestamp is set to
   * current time. Commits the transaction.
   * 
   * @param amount  The amount deposited
   * @param loc     The location of the deposit
   * @param teller  The teller the deposit was with
   * @param account The account the money was deposted into.
   * @param conn    The db connection.
   * @return The id of the inserted transaction. -1 if transaction was rolled
   *         back/failed.
   */
  public static long cashDeposit(double amount, Location loc, Teller teller, Account account, Connection conn) {
    boolean success = account.dbDepositBalance(amount, conn);
    long t_id = insertTransaction(amount, now(), conn);
    success = success && (t_id != -1);
    success = success && insertCashTransaction(t_id, loc.getLocId(), conn);
    success = success && insertAccountDeposit(t_id, account.getAccId(), teller.getPId(), conn);
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

    return t_id;
  }

  /**
   * Inserts a transaction into the db. Does NOT commit.
   * 
   * @param amount The size of the transaction.
   * @param t_date The timestamp of the transaction.
   * @param conn   The db connection
   * @return Id of the transaction. -1 on failure.
   */
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

  /**
   * The cash side of a transaction. Not the transaction itself but the
   * representation showing that one side of the transaction was done using cash.
   * Does NOT commit.
   * 
   * @param t_id   The id of the transaction
   * @param loc_id The id of the location the cash was taken/given from
   * @param conn   The db connection
   * @return True if transaction succeeded.
   */
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

  /**
   * Inserts into the db the side of the transaction that represents the money was
   * deposited into a specific account. Does NOT commit.
   * 
   * @param t_id      The id of the transaction
   * @param acc_id    The id of the account the money was deposited into
   * @param teller_id The id of the teller which handled the deposit
   * @param conn      The db connection.
   * @return True if succeeded.
   */
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

  /**
   * Inserts into the db the side of the transaction that represents what account
   * the money came from. Does NOT commit.
   * 
   * @param t_id      The id of the transaction
   * @param acc_id    The id of the account the money came from.
   * @param teller_id The id of the teller which approved this transaction.
   * @param conn      The db connection.
   * @return True if insert succeeded.
   */
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

  /**
   * Inserts the side of the transaction showing that it was used to make a
   * purchase from a vendor with a card. Does NOT commit.
   * 
   * @param t_id    The id of the transaction this purchase goes with.
   * @param card_id The card id (can be debit or credit)
   * @param v_id    The id of the vendor the purchase was made at.
   * @param conn    The db connection.
   * @return True if insert succeeded.
   */
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

  /**
   * Inserts a loan into the db.
   * 
   * @param loanholder_id   The id of the person taking out the loan
   * @param interest_rate   The interest rate of the loan.
   * @param loan_amount     The amount loaned.
   * @param amount_due      The amount currently due for the loan. (likely equal
   *                        to the amount loaned)
   * @param monthly_payment The minimum monthly payment for the loan
   * @param collatoral      The colatoral used for the loan (null if none is used)
   * @param conn            The db connection.
   * @return The id of the loan. -1 if insertion failed.
   */
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
      conn.commit();
      return loan_id;
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      conn.rollback();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * Used for inserting the current time into a db entry.
   * 
   * @return The current time as a timestamp
   */
  public static Timestamp now() {
    return new Timestamp(new Date().getTime());
  }
}