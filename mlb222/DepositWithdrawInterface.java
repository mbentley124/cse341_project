package mlb222;

import java.sql.Connection;
import java.util.List;

import mlb222.utilities.database_structures.Account;
import mlb222.utilities.database_structures.CheckingAccount;
import mlb222.utilities.database_structures.Customer;
import mlb222.utilities.database_structures.Location;
import mlb222.utilities.database_structures.Teller;
import mlb222.utilities.ConnectionManager;
import mlb222.utilities.Input;

/**
 * This was a real fancy diagram showing the path the user goes along when using
 * the app. Then I hit the magic autoformat button...
 * 
 * Navigation Layout: Customer Name Enter Account if Has Accounts Enter Location
 * Withdraw/Deposit if location supports both Go to enter teller name if
 * withdraw Go to deposit otherwise if deposit Deposit otherwise Size of Deposit
 * Enter where money is coming from (cash or another account) Make deposit if
 * Cash Request account id if from another account (Likely only users accounts?)
 * Verify size of transaction works for other account and penalties needed then
 * move money. Enter Teller name if withdrawing Size of transaction Re-enter
 * size of transaction if brings savings account below 0 Impose penalty if size
 * of transaction bring below checking min balance (then proceed to line below)
 * Go back to Enter Account? Back to Customer Name if no accounts
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
    Customer customer = Input.promptCustomer(conn);

    if (Input.isQuitSet() || Input.isBackSet()) {
      return;
    } else {
      promptAccount(conn, customer);
    }
  }

  public static void promptAccount(Connection conn, Customer customer) {
    System.out.println("Welcome " + customer.getFullName() + "! ");
    List<Account> accounts = customer.selectAccounts(conn);
    if (accounts == null) {
      System.out.println("Error retrieving your accounts");
      promptCustomerName(conn);
    } else if (accounts.size() == 0) {
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
        System.out.println("You currently have $" + account.getBalance() + " in your account");
        promptLocation(conn, customer, account);
      }
    }
  }

  public static void promptLocation(Connection conn, Customer customer, Account account) {
    List<Location> locations = ConnectionManager.selectAllLocations(conn);
    if (locations == null) {
      System.out.println("Error retrieving locations");
      promptAccount(conn, customer);
      return;
    }
    Location location = Input.prompt("What location are you transacting from?", locations.toArray(new Location[0]));
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
    List<Teller> compatible_tellers = location.selectTellers(conn);
    if (compatible_tellers == null) {
      System.out.println("Error retrieving location's tellers");
      promptLocation(conn, customer, account);
    } else if (compatible_tellers.size() == 1) {
      System.out.println("Your location only has an ATM which only supports withdrawals");
      accountWithdraw(conn, customer, account, location, compatible_tellers.get(0), BackMethod.PROMPT_LOCATION);
    } else {
      Teller teller = Input.prompt("Which teller are you working with?", compatible_tellers.toArray(new Teller[0]));
      if (Input.isBackSet()) {
        promptLocation(conn, customer, account);
      } else if (Input.isQuitSet()) {
        return;
      } else {
        if (teller.isAtm()) {
          System.out.println("ATMS only support withdrawals");
          accountWithdraw(conn, customer, account, location, teller, BackMethod.PROMPT_TELLER);
        } else {
          promptWithdrawOrDeposit(conn, customer, account, location, teller);
        }
      }
    }
  }

  public static void promptWithdrawOrDeposit(Connection conn, Customer customer, Account account, Location location,
      Teller teller) {
    String action_choice = Input.prompt("What action would you like to perform on your account?",
        new String[] { "Withdraw", "Deposit", "Transfer" });
    // Do the corresponding action for the input. Not possible to have any other
    // results hence no else statment. (Since the way Input.prompt works)
    if (Input.isBackSet()) {
      pathFromLocationTellers(conn, customer, account, location);
    } else if (Input.isQuitSet()) {
      return;
    } else if (action_choice.equals("Withdraw")) {
      accountWithdraw(conn, customer, account, location, teller, BackMethod.PROMPT_WITHDRAW_DEPOSIT);
    } else if (action_choice.equals("Deposit")) {
      accountDeposit(conn, customer, account, location, teller);
    } else if (action_choice.equals("Transfer")) {
      accountTransferSelection(conn, customer, account, location, teller);
    }
  }

  public static void accountTransferSelection(Connection conn, Customer customer, Account account, Location location,
      Teller teller) {
    List<Account> other_accounts = customer.selectAccounts(conn);
    if (other_accounts == null) {
      System.out.println("Error retrieving your other accounts");
      promptWithdrawOrDeposit(conn, customer, account, location, teller);
      return;
    }
    other_accounts.removeIf(acc -> acc.getAccId() == account.getAccId());
    if (other_accounts.size() == 0) {
      System.out.println("You don't have any accounts to transfer from.");
      promptWithdrawOrDeposit(conn, customer, account, location, teller);
    } else {
      Account transfering_account = Input.prompt("Which account would you like to transfer money from?",
          other_accounts.toArray(new Account[0]));
      if (Input.isBackSet()) {
        promptWithdrawOrDeposit(conn, customer, account, location, teller);
      } else if (Input.isQuitSet()) {
        return;
      } else {
        accountMoneyTransfer(conn, customer, transfering_account, account, location, teller);
      }
    }
  }

  public static void accountMoneyTransfer(Connection conn, Customer customer, Account from_account, Account to_account,
      Location location, Teller teller) {
    if (from_account instanceof CheckingAccount) {
      System.out.println("You currently have $" + from_account.getBalance()
          + " in the account you are transfering from (Minimum Balance: $"
          + ((CheckingAccount) from_account).getMinimumBalance() + ", Penalty: $"
          + ((CheckingAccount) from_account).getPenalty() + ")");
    } else {
      System.out
          .println("You currently have $" + from_account.getBalance() + " in the account you are transfering from");
    }
    Double transfer_amount = Input.promptDouble("How much would you like to transfer?", true);
    if (Input.isBackSet()) {
      accountTransferSelection(conn, customer, to_account, location, teller);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      int penalty = ConnectionManager.accountTransfer(transfer_amount, location, teller, to_account, from_account,
          conn);
      if (penalty == -1) {
        System.out.println("Unable to transfer that much!");
        accountMoneyTransfer(conn, customer, from_account, to_account, location, teller);
      } else {
        if (penalty != 0) {
          System.out.println("There is a $" + penalty + " penalty for this transfer");
        }
        to_account.refresh(conn);
        from_account.refresh(conn);
        System.out.println("You have transfered $" + transfer_amount + ". You now have $" + to_account.getBalance()
            + " in " + to_account.toString() + " and $" + from_account.getBalance() + " in " + from_account.toString());
        anotherTransaction(conn, customer, to_account, location, teller);
      }
    }
  }

  public static void accountWithdraw(Connection conn, Customer customer, Account account, Location location,
      Teller teller, BackMethod back_method) {
    if (account instanceof CheckingAccount) {
      System.out.println("You currently have $" + account.getBalance() + " in your account (Minimum Balance: $"
          + ((CheckingAccount) account).getMinimumBalance() + ", Penalty: $" + ((CheckingAccount) account).getPenalty()
          + ")");
    } else {
      System.out.println("You currently have $" + account.getBalance() + " in your account");
    }
    Double withdraw_amount = Input.promptDouble("How much would you like to withdraw?", true);
    if (Input.isBackSet()) {
      goBack(conn, customer, account, location, teller, back_method);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      int penalty = ConnectionManager.cashWithdraw(withdraw_amount, location, teller, account, conn);
      if (penalty == -1) {
        System.out.println("Unable to withdraw that much!");
        accountWithdraw(conn, customer, account, location, teller, back_method);
      } else {
        if (penalty != 0) {
          System.out.println("There is a $" + penalty + " penalty for this withdrawal");
        }
        account.refresh(conn);
        System.out.println(
            "You have withdrew $" + withdraw_amount + ". You now have $" + account.getBalance() + " in your account.");
        anotherTransaction(conn, customer, account, location, teller);
      }
    }
  }

  public static void accountDeposit(Connection conn, Customer customer, Account account, Location location,
      Teller teller) {
    Double deposit_amount = Input.promptDouble("How much would you like to deposit?", true);
    if (Input.isBackSet()) {
      promptWithdrawOrDeposit(conn, customer, account, location, teller);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      long t_id = ConnectionManager.cashDeposit(deposit_amount, location, teller, account, conn);
      if (t_id == -1) {
        System.out.println("Unable to make deposit!");
      } else {
        account.refresh(conn);
        System.out.println(
            "You have deposited $" + deposit_amount + ". You now have $" + account.getBalance() + " in your account.");
      }
      anotherTransaction(conn, customer, account, location, teller);
    }
  }

  public static void anotherTransaction(Connection conn, Customer customer, Account account, Location location, Teller teller) {
    Boolean another_transaction = Input.promptBoolean("Would you like to make another transaction on this account (back/quit will quit)?");
    if (Input.isBackSet() || Input.isQuitSet() || !another_transaction) {
      System.out.println("Thank you for transacting with Nickel Bank");
      return;
    } else {
      pathFromLocationTellers(conn, customer, account, location);
    }
  }

  private static void goBack(Connection conn, Customer customer, Account account, Location location, Teller teller,
      BackMethod back_method) {
    if (back_method == BackMethod.PROMPT_LOCATION) {
      promptLocation(conn, customer, account);
    } else if (back_method == BackMethod.PROMPT_TELLER) {
      pathFromLocationTellers(conn, customer, account, location);
    } else if (back_method == BackMethod.PROMPT_WITHDRAW_DEPOSIT) {
      promptWithdrawOrDeposit(conn, customer, account, location, teller);
    }
  }
}