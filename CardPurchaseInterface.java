import java.sql.Connection;
import java.util.List;

import database_structures.Card;
import database_structures.Customer;
import database_structures.Vendor;

/**
 * Enter User Name
 *   Enter Card Name (if has cards)
 *     Enter Store Name
 *       Enter Purchase Amount
 *         Confirm purchase and go back to 
 *   Go Back to Enter User Name (if has no cards)
 * 
 */
public class CardPurchaseInterface {

  public static void run(Connection conn) {
    System.out.println("Welcome to the card purchase interface! At any time you can type quit to quit the interface and back to go back.");
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
    List<Card> cards = ConnectionManager.selectCustomerCards(conn, customer);
    if (cards.size() == 0) {
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
    Double amount = Input.promptDouble("What is the value of the items you're purchasing?", true);
    if (Input.isBackSet()) {
      promptVendor(conn, customer, card);
    } else if (Input.isQuitSet()) {
      return;
    } else {
      // TODO make purchase. 
      System.out.println("Purchase $" + amount);
      // TODO go back to proper options. 
    }
  }
}