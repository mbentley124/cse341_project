import java.sql.Connection;
import java.util.List;

import utilities.ConnectionManager;
import utilities.Input;
import utilities.database_structures.Account;
import utilities.database_structures.Card;
import utilities.database_structures.CheckingAccount;
import utilities.database_structures.CreditCard;
import utilities.database_structures.Customer;
import utilities.database_structures.DebitCard;
import utilities.database_structures.Vendor;

/**
 * Fancy diagram below that I accidentaly ruined. I have a habit of doing
 * this...
 * 
 * Enter User Name Enter Card Name (if has cards) Enter Store Name Enter
 * Purchase Amount Confirm purchase and go back to Go Back to Enter User Name
 * (if has no cards)
 * 
 */
public class CardPurchaseInterface {

  public static void run(Connection conn) {
    System.out.println(
        "Welcome to the card purchase interface! At any time you can type quit to quit the interface and back to go back.");
    promptCustomerName(conn);
  }

  public static void promptCustomerName(Connection conn) {
    Customer customer = Input.promptCustomer(conn);
    if (Input.isBackSet() || Input.isQuitSet()) {
      return;
    } else {
      promptCard(conn, customer);
    }
  }

  public static void promptCard(Connection conn, Customer customer) {
    List<Card> cards = customer.selectCards(conn);
    if (cards == null) {
      System.out.println("There was an error retrieving your cards");
      promptCustomerName(conn);
    } else if (cards.size() == 0) {
      System.out.println("You don't have any cards with us. Please login with a different user");
      promptCustomerName(conn);
    } else {
      Card card = Input.prompt("Which card are you using?", cards.toArray(new Card[0]));
      if (Input.isBackSet()) {
        promptCustomerName(conn);
      } else if (Input.isQuitSet()) {
        return;
      } else {
        promptVendor(conn, customer, card);
      }
    }
  }

  public static void promptVendor(Connection conn, Customer customer, Card card) {
    Vendor vendor = Input.promptVendor(conn);
    if (Input.isBackSet()) {
      promptCard(conn, customer);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      promptAmount(conn, customer, card, vendor);
    }
  }

  public static void promptAmount(Connection conn, Customer customer, Card card, Vendor vendor) {
    if (card instanceof CreditCard) {
      CreditCard c_card = (CreditCard) card;
      System.out.println("You currently have a rolling balance of $" + c_card.getRollingBalance()
          + " with a credit limit of $" + c_card.getCreditLimit());
    } else if (card instanceof DebitCard) {
      DebitCard d_card = (DebitCard) card;
      CheckingAccount account = d_card.selectAccount(conn);
      if (account != null) {
        System.out.println("You currently have $" + account.getBalance() + " in your account (Minimum Balance: $"
            + account.getMinimumBalance() + ", Penalty: $" + account.getPenalty() + ")");
      }
    }
    Double amount = Input.promptDouble("What is the value of the items you're purchasing?", true);
    if (Input.isBackSet()) {
      promptVendor(conn, customer, card);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      System.out.println("Purchase $" + amount);
      if (card instanceof CreditCard) {
        CreditCard c_card = (CreditCard) card;
        boolean success = ConnectionManager.purchaseCreditCard(amount, customer, c_card, vendor, conn);
        if (success) {
          c_card.refresh(conn);
          System.out.println("Transaction completed. You know have a $" + c_card.getRollingBalance() + " rolling balance");

          promptTransactionCompletion(conn, customer);
        } else {
          System.out.println("Card declined!");
          promptAmount(conn, customer, card, vendor);
        }
      } else if (card instanceof DebitCard) {
        DebitCard d_card = (DebitCard) card;
        int penalty = ConnectionManager.purchaseDebitCard(amount, d_card, vendor, conn);
        if (penalty != -1) {
          if (penalty > 0) {
            System.out.println("$" + penalty + " Penalty imposed for going below minimum balance");
          }
          CheckingAccount account = d_card.selectAccount(conn);
          if (account == null) {
            System.out.println("Transaction completed");
          } else {
            System.out.println("Transaction completed. New balance of $" + account.getBalance());
          }

          promptTransactionCompletion(conn, customer);
        } else {
          System.out.println("Card declined!");
          promptAmount(conn, customer, card, vendor);
        }
      }
    }
  }

  public static void promptTransactionCompletion(Connection conn, Customer customer) {
    Boolean anotherPurchase = Input.promptBoolean("Would you like to make another purchase?");
    if (Input.isBackSet() || Input.isQuitSet() || !anotherPurchase) {
      // TODO maybe back should move the user to the previous screen?
      return;
    } else {
      promptCard(conn, customer);
    }
  }
}