import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Bank {

  public static void main(String[] args) {
    try (Connection conn = ConnectionManager.connect()) {
      conn.setAutoCommit(false);
      String input = Input.prompt("Hello! What interface would you like to use?",
          new String[] { "Account deposit/withdrawal" });
      if (input.equals("Account deposit/withdrawal")) {
        // In the deposit/withdrawal interface.
        DepositWithdrawInterface.run(conn);
      }
    } catch (SQLException e) {
      // TODO exit gracefully.
      e.printStackTrace();
    }
  }
}