import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class ConnectionManager {
  
  static Scanner scan = new Scanner(System.in);

  public static Connection connect() {
    System.out.print("enter Oracle user id: ");
    String id = scan.nextLine();
    System.out.print("enter Oracle password for " + id + ": ");
    String pass = scan.nextLine();
    try {
      return DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", id, pass);
    } catch (SQLException e) {
      System.out.println("connect error. Re-enter login data:");
      return connect();
    }
  }
  
}