import java.sql.Connection;
import java.sql.SQLException;

import utilities.ConnectionManager;
import utilities.Input;

// TODO need to make sure that values that can be null don't break the db. 

public class Bank {

  public static void main(String[] args) {
    try (Connection conn = ConnectionManager.connect()) {
      conn.setAutoCommit(false);
      boolean exit = false;
      while (!exit) {
        String input = Input.prompt("Hello! What interface would you like to use?",
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
      // TODO exit gracefully.
      e.printStackTrace();
    }
  }
}