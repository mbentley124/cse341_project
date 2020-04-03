import java.sql.Connection;
import java.util.List;

import database_structures.Account;
import database_structures.Customer;
import database_structures.Location;
import database_structures.Teller;

/**
 * Navigation Layout:
 * Customer Name
 *   Enter Account if Has Accounts
 *     Enter Location
 *       Withdraw/Deposit if location supports both
 *         Go to enter teller name if withdraw
 *         Go to deposit otherwise if deposit
 *       Deposit otherwise
 *         Size of Deposit
 *           Enter where money is coming from (cash or another account)
 *             Make deposit if Cash
 *             Request account id if from another account (Likely only users accounts?)
 *               Verify size of transaction works for other account and penalties needed then move money. 
 *       Enter Teller name if withdrawing
 *         Size of transaction
 *           Re-enter size of transaction if brings savings account below 0
 *           Impose penalty if size of transaction bring below checking min balance (then proceed to line below)
 *           Go back to Enter Account?
 *   Back to Customer Name if no accounts
 * 
 * 
 */
public class DepositWithdrawInterface {

  // All this back functionality would be so much easier to implement if 
  // java supported pointers to functions. And more fun too!
  // Now I have to do something gross like this instead. Eww. 
  // I'm usually a fan of enums too (they have some neato functionality) 
  // but I just dislike this.
  private enum BackMethod {
    PROMPT_LOCATION, PROMPT_TELLER, PROMPT_WITHDRAW_DEPOSIT
  }

  public static void run(Connection conn) {
    System.out.println(
        "Welcome to the deposit/withdrawal interface! At any time you can type quit to quit the interface and back to go back.");
    promptCustomerName(conn);
  }

  /**
   * Prompts the user until they enter a name that
   */
  public static void promptCustomerName(Connection conn) {
    String user_name = Input.prompt("Enter your name: ");
    if (Input.isQuitSet() || Input.isBackSet()) {
      return;
    }
    List<Customer> customers = ConnectionManager.selectCustomers(user_name, conn);
    int customer_count = customers.size();

    if (customer_count == 0) {
      System.out.println("No customers found with names like that");
      promptCustomerName(conn);
      return;
    } else if (customer_count > 1) {
      // Check if they entered an exact name.
      for (Customer customer : customers) {
        if (customer.getFullName().equals(user_name)) {
          // This is the corresponding customer.
          promptAccount(conn, customer);
          return;
        }
      }
      // They didn't say a specific enough name so list out the possible names and
      // prompt them again.
      System.out.println("Which of these names did you mean: ");
      for (Customer customer : customers) {
        System.out.println(customer.getFullName());
      }
      promptCustomerName(conn);
    } else {
      promptAccount(conn, customers.get(0));
    }
  }

  public static void promptAccount(Connection conn, Customer customer) {
    System.out.print("Welcome " + customer.getFullName() + "! ");
    List<Account> accounts = ConnectionManager.selectUserAccounts(customer, conn);
    if (accounts.size() == 0) {
      System.out.println("You don't seem to have any accounts with us! Please login as someone else");
      promptCustomerName(conn);
    } else {
      Account account = Input.prompt("Which account would you like to use?", accounts.toArray(new Account[0]));
      if (Input.isBackSet()) {
        promptCustomerName(conn);
      } else if (Input.isQuitSet()) {
        return;
      } else {
        // Go to the next step.
        promptLocation(conn, customer, account);
      }
    }
  }

  public static void promptLocation(Connection conn, Customer customer, Account account) {
    List<Location> locations = ConnectionManager.selectAllLocations(conn);
    Location location = Input.prompt("What location are you withdrawing from?", locations.toArray(new Location[0]));
    if (Input.isBackSet()) {
      promptAccount(conn, customer);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      if (location.getLocName().equals("Scranton")) {
        // Who doesn't love scranton!
        System.out.println("That's my favorite branch!");
      }
      pathFromLocationTellers(conn, customer, account, location);
    }
  }

  public static void pathFromLocationTellers(Connection conn, Customer customer, Account account, Location location) {
    List<Teller> compatible_tellers = ConnectionManager.selectLocationTellers(conn, location);
    if (compatible_tellers.size() == 1) {
      System.out.println("This location only has an ATM which only supports deposits");
      // TODO go to atm only deposit. 
      accountDeposit(conn, customer, account, location, compatible_tellers.get(0), BackMethod.PROMPT_LOCATION);
    } else {
      Teller teller = Input.prompt("Which teller are you working with?", compatible_tellers.toArray(new Teller[0]));
      if (Input.isBackSet()) {
        promptLocation(conn, customer, account);
      } else if (Input.isQuitSet()) {
        return;
      } else {
        if (teller.isAtm()) {
          accountDeposit(conn, customer, account, location, teller, BackMethod.PROMPT_TELLER);
        } else {
          promptWithdrawOrDeposit(conn, customer, account, location, teller);
        }
      }
      // promptWithdrawOrDeposit(conn, customer, account, location);
    }
  }

  public static void promptWithdrawOrDeposit(Connection conn, Customer customer, Account account, Location location, Teller teller) {
    String action_choice = Input.prompt("What action would you like to perform on your account?",
        new String[] { "Withdraw", "Deposit" });
    // Do the corresponding action for the input. Not possible to have any other
    // results hence no else statment. (Since the way Input.prompt works)
    if (Input.isBackSet()) {
      pathFromLocationTellers(conn, customer, account, location);
    } else if (Input.isQuitSet()) {
      return;
    } else if (action_choice.equals("Withdraw")) {
      accountWithdraw(conn, customer, account, location, teller);
    } else if (action_choice.equals("Deposit")) {
      accountDeposit(conn, customer, account, location, teller, BackMethod.PROMPT_WITHDRAW_DEPOSIT);
    } 
  }

  public static void accountWithdraw(Connection conn, Customer customer, Account account, Location location, Teller teller) {
    // TODO prompt amount of cash money then 
    System.out.println("You are withdrawing now");
    // TODO
  }

  public static void accountDeposit(Connection conn, Customer customer, Account account, Location location, Teller teller, BackMethod back_method) {
    Double deposit_amount = Input.promptDouble("How much would you like to deposit?");
    if (Input.isBackSet()) {
      goBack(conn, customer, account, location, teller, back_method);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      // TODO deposit money in database
      double new_balance = account.adjustBalance(deposit_amount);
      System.out.println("You have deposited $" + deposit_amount + ". You now have $" + new_balance + " in your account.");
      goBack(conn, customer, account, location, teller, back_method);
    }
  }

  private static void goBack(Connection conn, Customer customer, Account account, Location location, Teller teller, BackMethod back_method) {
    if (back_method == BackMethod.PROMPT_LOCATION) {
      promptLocation(conn, customer, account);
    } else if (back_method == BackMethod.PROMPT_TELLER) {
      pathFromLocationTellers(conn, customer, account, location);
    } else if (back_method == BackMethod.PROMPT_WITHDRAW_DEPOSIT) {
      promptWithdrawOrDeposit(conn, customer, account, location, teller);
    }
  }
}