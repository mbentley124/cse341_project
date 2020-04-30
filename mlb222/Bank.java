package mlb222;

import java.sql.Connection;
import java.sql.SQLException;

import mlb222.utilities.ConnectionManager;
import mlb222.utilities.Input;

public class Bank {

  public static void main(String[] args) {
    try (Connection conn = ConnectionManager.connect()) {
      conn.setAutoCommit(false);
      boolean exit = false;
      while (!exit) {
        String input = Input.prompt("Hello! What interface would you like to use? (quit/back to exit)",
            new String[] { "Account deposit/withdrawal", "Card Purchasing", "Takeout Loan" });
        if (Input.isBackSet() || Input.isQuitSet()) {
          exit = true;
        } else if (input.equals("Account deposit/withdrawal")) {
          // In the deposit/withdrawal interface.
          DepositWithdrawInterface.run(conn);
        } else if (input.equals("Card Purchasing")) {
          // Interface 7: Credit card purchasing.
          CardPurchaseInterface.run(conn);
        } else if (input.equals("Takeout Loan")) {
          // The takeout loan interface
          LoanTakeoutInterface.run(conn);
        }
      }
    } catch (SQLException e) {
      System.out.println("There was an error connecting to the bank");
    }
  }
}