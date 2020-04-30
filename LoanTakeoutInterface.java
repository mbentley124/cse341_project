import java.sql.Connection;
import java.util.List;

import utilities.database_structures.Account;
import utilities.database_structures.Customer;
import utilities.database_structures.Location;
import utilities.database_structures.Teller;
import utilities.ConnectionManager;
import utilities.Input;

/**
 * Need to determine loanholder id, loan interest rate (determined by math?),
 * amount loaned (amount due = amount loaned), monthly payment (likely percent
 * of amount loaned), and colatoral (may not exist)
 * 
 * 
 * This below fancy user interface diagram works now even if I do hit the magic
 * autoformat button. I'm learning.
 * 
 * login -> loan amount (<1000000) -> has colatoral? (required if big loan) ->
 * colatoral (if has it) -> agree to terms of loan? (display terms too. duh)
 */
public class LoanTakeoutInterface {

  /**
   * An enum representing the method to go back to after the user enters 'back'.
   * This is required for methods that are called from multiple other methods,
   * otherwise its ambiguious where back goes to.
   */
  private enum BackMethod {
    TELLER, LOCATION, LOAN_AMOUNT, ACCOUNT_OR_CASH, WHICH_ACCOUNT, HAS_COLATORAL, GET_COLATORAL
  }

  public static void run(Connection conn) {
    System.out.println(
        "Welcome to the loan takeout interface! At any time you can type quit to quit the interface and back to go back.");
    loginCustomer(conn);
  }

  public static void loginCustomer(Connection conn) {
    Customer customer = Input.promptCustomer(conn);
    if (Input.isBackSet() || Input.isQuitSet()) {
      return;
    } else {
      location(conn, customer);
    }
  }

  public static void location(Connection conn, Customer customer) {
    List<Location> locations = ConnectionManager.selectLocationsWithHumanTellers(conn);
    if (locations == null) {
      System.out.println("Error retrieving compatible locations");
      loginCustomer(conn);
      return;
    }
    Location location = Input.prompt("Which location are you at?", locations.toArray(new Location[0]));
    if (Input.isBackSet()) {
      loginCustomer(conn);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      teller(conn, customer, location);
    }
  }

  public static void teller(Connection conn, Customer customer, Location location) {
    List<Teller> tellers = location.selectTellers(conn);
    if (tellers == null) {
      System.out.println("Error retreiving tellers");
      location(conn, customer);
      return;
    }
    tellers.removeIf((teller) -> teller.isAtm());
    // At least one teller has to work at the selected location since only locations
    // were shown with at least one human teller. So the first part of the if
    // statement should never be reached. I just kept this here since I already had
    // it, and there is little reason to remove it.
    if (tellers.size() == 0) {
      System.out.println("I'm Sorry. That location does not support loans. No tellers work there.");
      location(conn, customer);
    } else if (tellers.size() == 1) {
      System.out
          .println(tellers.get(0) + " is the only teller that works at that location. You must be working with them");
      loanAmount(conn, customer, location, tellers.get(0), BackMethod.LOCATION);
    } else {
      Teller teller = Input.prompt("Which teller are you working with?", tellers.toArray(new Teller[0]));
      if (Input.isBackSet()) {
        location(conn, customer);
      } else if (Input.isQuitSet()) {
        return;
      } else {
        loanAmount(conn, customer, location, teller, BackMethod.TELLER);
      }
    }
  }

  public static void loanAmount(Connection conn, Customer customer, Location location, Teller teller,
      BackMethod amount_back_method) {
    // We don't want a single customer to have too much money loaned out to them.
    Double outstanding_loan_amount = customer.getNetLoanAmountDue(conn);
    if (outstanding_loan_amount == null) {
      System.out
          .println("There was an issue retrieving your outstanding loans. Please use this interface another time.");
      return;
    } else {
      System.out.println("You currently have $" + outstanding_loan_amount + " of loans outstanding");
    }

    Double loan_amount = Input.promptDouble("How big of a loan would you like?", true);
    if (Input.isBackSet()) {
      // There is an obscure issue where this method would be called from something
      // besides loginCustomer (if the customer declines the terms of the loan)
      // however it would need a far more complicated method of going back to allow
      // the user to go back to somewhere else. (Something along the lines of a stack
      // with every method that has been called and with what arguments)
      goBack(amount_back_method, conn, customer, location, teller, amount_back_method, loan_amount, null, null, null,
          null);
    } else if (Input.isQuitSet()) {
      return;
    } else if (outstanding_loan_amount + loan_amount >= 1000000) {
      System.out.println(
          "Woah woah woah! This is nickel savings bank! We don't have that amount of cash on us. (total outstanding loans are limited to $1,000,000)");
      loanAmount(conn, customer, location, teller, amount_back_method);
    } else {
      accountOrCash(conn, customer, location, teller, amount_back_method, loan_amount);
    }
  }

  public static void accountOrCash(Connection conn, Customer customer, Location location, Teller teller,
      BackMethod amount_back_method, double loan_amount) {
    List<Account> accounts = customer.selectAccounts(conn);
    if (accounts == null) {
      System.out.println("Error finding your accounts");
      loanAmount(conn, customer, location, teller, amount_back_method);
    } else if (accounts.size() == 0) {
      System.out.println("You do not have an account with us, so you will receive this money as a check");
      if (loan_amount > 100000) {
        // Colatoral required
        getColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, null, null,
            BackMethod.LOAN_AMOUNT);
      } else {
        // Colatoral customers choice
        hasColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, null,
            BackMethod.LOAN_AMOUNT);
      }
    } else {
      String choice = Input.prompt("Would you like to receive you money in a check or in one of your accounts with us",
          new String[] { "Check", "Account" });
      if (Input.isBackSet()) {
        loanAmount(conn, customer, location, teller, amount_back_method);
      } else if (Input.isQuitSet()) {
        return;
      } else if (choice.equals("Check")) {
        if (loan_amount > 100000) {
          // Colatoral required
          getColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, null, null,
              BackMethod.ACCOUNT_OR_CASH);
        } else {
          // Colatoral customers choice
          hasColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, null,
              BackMethod.ACCOUNT_OR_CASH);
        }
      } else {
        whichAccount(conn, customer, location, teller, amount_back_method, loan_amount, accounts);
      }
    }
  }

  public static void whichAccount(Connection conn, Customer customer, Location location, Teller teller,
      BackMethod amount_back_method, double loan_amount, List<Account> accounts) {
    if (accounts.size() > 1) {
      Account account = Input.prompt("Which account?", accounts.toArray(new Account[0]));
      if (Input.isBackSet()) {
        accountOrCash(conn, customer, location, teller, amount_back_method, loan_amount);
      } else if (Input.isQuitSet()) {
        return;
      } else {
        if (loan_amount > 100000) {
          // Colatoral required
          getColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account, null,
              BackMethod.WHICH_ACCOUNT);
        } else {
          // Colatoral customers choice
          hasColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account,
              BackMethod.WHICH_ACCOUNT);
        }
      }
    } else {
      System.out.println(
          "You must intend to use this account: " + accounts.get(0).toString() + ". Its the only one you have");
      hasColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, accounts.get(0),
          BackMethod.ACCOUNT_OR_CASH);
    }
  }

  public static void hasColatoral(Connection conn, Customer customer, Location location, Teller teller,
      BackMethod amount_back_method, double loan_amount, List<Account> accounts, Account account,
      BackMethod has_colatoral_back_method) {
    Boolean hasColatoral = Input.promptBoolean(
        "Would you like to have colatoral to lower your interest rate? You are not required with that size of a loan");
    if (Input.isBackSet()) {
      goBack(has_colatoral_back_method, conn, customer, location, teller, amount_back_method, loan_amount, accounts,
          account, has_colatoral_back_method, null);
    } else if (Input.isQuitSet()) {
      return;
    } else if (hasColatoral) {
      // If the customer decided to have colatoral.
      getColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account,
          has_colatoral_back_method, BackMethod.HAS_COLATORAL);
    } else {
      // If the customer doesn't want colatoral.
      agreeToTerms(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account, null,
          has_colatoral_back_method, BackMethod.HAS_COLATORAL);
    }
  }

  public static void getColatoral(Connection conn, Customer customer, Location location, Teller teller,
      BackMethod amount_back_method, double loan_amount, List<Account> accounts, Account account,
      BackMethod has_colatoral_back_method, BackMethod back_method) {
    String colatoral = Input.promptCustomString("What is your colatoral (must be less than 61 characters): ",
        (input) -> input.length() <= 60);
    if (Input.isBackSet()) {
      goBack(back_method, conn, customer, location, teller, amount_back_method, loan_amount, accounts, account,
          has_colatoral_back_method, back_method);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      if (colatoral.toLowerCase().equals("farm")) {
        System.out.println("I hope its a beet farm!");
      } else if (colatoral.toLowerCase().equals("beet farm")) {
        System.out.println("Oh goody its a beet farm!");
      }
      agreeToTerms(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account, colatoral,
          has_colatoral_back_method, BackMethod.GET_COLATORAL);
    }
  }

  public static void agreeToTerms(Connection conn, Customer customer, Location location, Teller teller,
      BackMethod amount_back_method, double loan_amount, List<Account> accounts, Account account, String colatoral,
      BackMethod has_colatoral_back_method, BackMethod back_method) {
    // loanholder id, loan interest rate (determined by math?),
    // * amount loaned (amount due = amount loaned), monthly payment (likely percent
    // * of amount loaned), and colatoral (may not exist)
    Double net_loan_amount_due = customer.getNetLoanAmountDue(conn);
    Double net_account_balance = customer.getNetAccountBalance(conn);

    if (net_account_balance == null || net_loan_amount_due == null) {
      System.out.println("Unable to calculate!");
      goBack(back_method, conn, customer, location, teller, amount_back_method, loan_amount, accounts, account,
          has_colatoral_back_method, back_method);
    } else {
      double net_balance = net_account_balance - net_loan_amount_due;

      // Get a one percent discount for each two thousand dollars in your account
      // minus (base interest rate of 7%)
      double interest_rate = 7.0 - net_balance / 2000;

      // Interest rate penalty if no colatoral.
      if (colatoral == null) {
        interest_rate += 1;
      }

      // Set a minimum interest rate. We don't want to be too generous. (And a max
      // interest rate too...)
      if (interest_rate < 2.0) {
        interest_rate = 2.0;
      } else if (interest_rate > 23.0) {
        interest_rate = 23.0;
      }

      double monthly_payment;

      // Just a set percent of the loan amount. Not too complicated. (with a discount
      // if their colatoral is a beet farm)
      if ("beet farm".equalsIgnoreCase(colatoral)) {
        // This above line is sort of interesting. If its done the other way
        // (colatoral.equals("beet farm")) then there would be a null pointer exception
        // if colatoral is null

        System.out.println("We've lowered your monthly payment since your colatoral is a beet farm");
        monthly_payment = 0.05 * loan_amount;
      } else {
        monthly_payment = 0.1 * loan_amount;
      }

      System.out.println("Here are the terms of the loan:");
      System.out.println("Loan Size: $" + loan_amount);
      System.out.println("Interest Rate: " + interest_rate + "%");
      System.out.println("Montly Payment: $" + monthly_payment);
      if (colatoral != null) {
        if (colatoral.equalsIgnoreCase("farm")) {
          System.out.println("Colatoral: (beet?) " + colatoral);
        } else {
          System.out.println("Colatoral: " + colatoral);
        }
      } else {
        System.out.println("No Colatoral");
      }

      Boolean agreed = Input.promptBoolean("Do you agree to the terms of this loan?");
      if (Input.isBackSet()) {
        goBack(back_method, conn, customer, location, teller, amount_back_method, loan_amount, accounts, account,
            has_colatoral_back_method, back_method);
      } else if (Input.isQuitSet()) {
        return;
      } else if (agreed) {
        // Have the user provide their digital signature
        promptSignature(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account, colatoral,
            has_colatoral_back_method, back_method, interest_rate, monthly_payment);
      } else {
        // If they say no then bring them back to the beginning of the request loan
        // interface. Don't want to let them get away that easily.
        System.out.println("Thats unfortunate. Let's try again!");
        loanAmount(conn, customer, location, teller, amount_back_method);
      }
    }
  }

  public static void promptSignature(Connection conn, Customer customer, Location location, Teller teller,
      BackMethod amount_back_method, double loan_amount, List<Account> accounts, Account account, String colatoral,
      BackMethod has_colatoral_back_method, BackMethod back_method, double interest_rate, double monthly_payment) {
    // This is legally binding (Section 3.2B of Contract Law states that if a person
    // agrees to a loan with an imaginary bank with no actual contract then the
    // terms are legally binding and must be followed through on by both sides)

    // No need to store their input as the only valid input is their name (except
    // for quit and back but those are stored seperately)
    Input.promptCustomString("Please enter your digital signature (" + customer.getFullName() + ")",
        (input) -> input.toLowerCase().equals(customer.getFullName().toLowerCase()));
    if (Input.isBackSet()) {
      agreeToTerms(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account, colatoral,
          has_colatoral_back_method, back_method);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      boolean success;
      Double outstanding_loan_amount = customer.getNetLoanAmountDue(conn);
      if (outstanding_loan_amount == null) {
        System.out.println("There was an error confirming your loan. ");
        success = false;
      } else if (outstanding_loan_amount + loan_amount >= 1000000) {
        System.out.println("Tryin to pull a fast one I see! This loan would bring you above $1,000,000 of loans");
        success = false;
      } else if (account == null) {
        success = ConnectionManager.insertLoanCashTakeout(conn, customer, colatoral, interest_rate, loan_amount,
            monthly_payment, teller, location);
        if (success) {
          // Giving them one of the big checks is one of our secrets. They're just
          // cardboard! You can't deposit those
          System.out.println(
              "Loan succesfully taken out. The teller will hand you a check for the amount now (one of those big checks!)");
        }
      } else {
        success = ConnectionManager.insertLoanAccountTakeout(conn, customer, account, colatoral, interest_rate,
            loan_amount, monthly_payment, teller);
        if (success) {
          System.out.println("Loan succesfully taken out. Your account will have $" + loan_amount + " in it now");
        }
      }
      if (!success) {
        System.out.println("Unable to takeout loan");
      }
      resetInterface(conn, customer, location, teller, amount_back_method);
    }
  }

  public static void resetInterface(Connection conn, Customer customer, Location location, Teller teller,
      BackMethod amount_back_method) {
    Boolean differentLoan = Input.promptBoolean("Would you like to take out a different loan (back/quit will quit)?");
    if (Input.isBackSet() || Input.isQuitSet() || !differentLoan) {
      return;
    } else {
      loanAmount(conn, customer, location, teller, amount_back_method);
    }
  }

  public static void goBack(BackMethod used_back_method, Connection conn, Customer customer, Location location,
      Teller teller, BackMethod amount_back_method, Double loan_amount, List<Account> accounts, Account account,
      BackMethod has_colatoral_back_method, BackMethod back_method) {
    switch (used_back_method) {
    case ACCOUNT_OR_CASH:
      accountOrCash(conn, customer, location, teller, amount_back_method, loan_amount);
      break;
    case WHICH_ACCOUNT:
      whichAccount(conn, customer, location, teller, amount_back_method, loan_amount, accounts);
      break;
    case LOCATION:
      location(conn, customer);
      break;
    case TELLER:
      teller(conn, customer, location);
      break;
    case LOAN_AMOUNT:
      loanAmount(conn, customer, location, teller, amount_back_method);
      break;
    case HAS_COLATORAL:
      hasColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account,
          has_colatoral_back_method);
      break;
    case GET_COLATORAL:
      getColatoral(conn, customer, location, teller, amount_back_method, loan_amount, accounts, account,
          has_colatoral_back_method, back_method);
      break;
    }
  }
}