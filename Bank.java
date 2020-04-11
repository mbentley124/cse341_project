import java.sql.Connection;
import java.sql.SQLException;

public class Bank {

  public static void main(String[] args) {
    try (Connection conn = ConnectionManager.connect()) {
      conn.setAutoCommit(false);
      String input = Input.prompt("Hello! What interface would you like to use?",
          new String[] { "Account deposit/withdrawal", "Card Purchasing" });
      if (Input.isBackSet() || Input.isQuitSet()) {
        return;
      } else if (input.equals("Account deposit/withdrawal")) {
        // In the deposit/withdrawal interface.
        DepositWithdrawInterface.run(conn);
      } else if (input.equals("Card Purchasing")) {
        // Interface 7: Credit card purchasing.
        CardPurchaseInterface.run(conn);
      }
    } catch (SQLException e) {
      // TODO exit gracefully.
      e.printStackTrace();
    }
  }
}