import java.sql.Connection;

import database_structures.Customer;

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
    LOAN_AMOUNT, HAS_COLATORAL, GET_COLATORAL
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
      loanAmount(conn, customer);
    }
  }

  public static void loanAmount(Connection conn, Customer customer) {
    // We don't want a single customer to have too much money loaned out to them.
    Double outstanding_loan_amount = ConnectionManager.getNetCustomerLoanAmountDue(conn, customer);
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
      loginCustomer(conn);
    } else if (Input.isQuitSet()) {
      return;
    } else if (outstanding_loan_amount + loan_amount >= 1000000) {
      System.out.println(
          "Woah woah woah! This is nickel savings bank! We don't have that amount of cash on us. (total outstanding loans are limited to $1,000,000)");
      loanAmount(conn, customer);
    } else {
      if (loan_amount > 100000) {
        // Colatoral required
        getColatoral(conn, customer, loan_amount, BackMethod.GET_COLATORAL);
      } else {
        // Colatoral customers choice
        hasColatoral(conn, customer, loan_amount);
      }
    }
  }

  public static void hasColatoral(Connection conn, Customer customer, double loan_amount) {
    Boolean hasColatoral = Input.promptBoolean(
        "Would you like to have colatoral to lower your interest rate? You are not required with that size of a loan");
    if (Input.isBackSet()) {
      loanAmount(conn, customer);
    } else if (Input.isQuitSet()) {
      return;
    } else if (hasColatoral) {
      // If the customer decided to have colatoral.
      getColatoral(conn, customer, loan_amount, BackMethod.HAS_COLATORAL);
    } else {
      // If the customer doesn't want colatoral.
      agreeToTerms(conn, customer, loan_amount, null, BackMethod.HAS_COLATORAL);
    }
  }

  public static void getColatoral(Connection conn, Customer customer, double loan_amount, BackMethod back_method) {
    String colatoral = Input.prompt("What is your colatoral: ");
    if (Input.isBackSet()) {
      goBack(conn, customer, loan_amount, back_method);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      if (colatoral.toLowerCase().equals("farm")) {
        System.out.println("I hope its a beet farm!");
      } else if (colatoral.toLowerCase().equals("beet farm")) {
        System.out.println("Oh goody its a beet farm!");
      }
      agreeToTerms(conn, customer, loan_amount, colatoral, BackMethod.GET_COLATORAL);
    }
  }

  public static void agreeToTerms(Connection conn, Customer customer, double loan_amount, String colatoral,
      BackMethod back_method) {
    // loanholder id, loan interest rate (determined by math?),
    // * amount loaned (amount due = amount loaned), monthly payment (likely percent
    // * of amount loaned), and colatoral (may not exist)
    Double net_loan_amount_due = ConnectionManager.getNetCustomerLoanAmountDue(conn, customer);
    Double net_account_balance = ConnectionManager.getNetCustomerAccountBalance(conn, customer);

    if (net_account_balance == null || net_loan_amount_due == null) {
      System.out.println("Unable to calculate!");
      goBack(conn, customer, loan_amount, back_method);
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
        goBack(conn, customer, loan_amount, back_method);
      } else if (Input.isQuitSet()) {
        return;
      } else if (agreed) {
        // Have the user provide their digital signature
        promptSignature(conn, customer, loan_amount, colatoral, back_method, interest_rate, monthly_payment);
      } else {
        // If they say no then bring them back to the beginning of the request loan
        // interface. Don't want to let them get away that easily.
        System.out.println("Thats unfortunate. Let's try again!");
        loanAmount(conn, customer);
      }
    }
  }

  public static void promptSignature(Connection conn, Customer customer, double loan_amount, String colatoral,
      BackMethod back_method, double interest_rate, double monthly_payment) {
    // This is legally binding (Section 3.2B of Contract Law states that if a person
    // agrees to a loan with an imaginary bank with no actual contract then the
    // terms are legally binding and must be followed through on by both sides)

    // No need to store their input as the only valid input is their name (except
    // for quit and back but those are stored seperately)
    Input.promptCustomString("Please enter your digital signature (" + customer.getFullName() + ")",
        (input) -> input.toLowerCase().equals(customer.getFullName().toLowerCase()));
    if (Input.isBackSet()) {
      agreeToTerms(conn, customer, loan_amount, colatoral, back_method);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      if (-1 == ConnectionManager.insertLoan(customer.getPId(), interest_rate, loan_amount, loan_amount,
          monthly_payment, colatoral, conn)) {
        System.out.println("There was an error! Please try another day");
        return;
      } else {
        if (loan_amount > 100000) {
          // Some witty humor for the user.
          System.out.println(
              "Loan taken out. We are mailing you a check with the money (For that amount of money its gonna be one of the big checks too)");
        } else {
          System.out.println("Loan taken out. We are mailing you a check with the money");
        }
        System.out.println("Thank you for using this interface");
        return;
      }
    }
  }

  public static void goBack(Connection conn, Customer customer, double loan_amount, BackMethod back_method) {
    switch (back_method) {
    case LOAN_AMOUNT:
      loanAmount(conn, customer);
      break;
    case HAS_COLATORAL:
      hasColatoral(conn, customer, loan_amount);
      break;
    case GET_COLATORAL:
      getColatoral(conn, customer, loan_amount, back_method);
      break;
    }
  }
}