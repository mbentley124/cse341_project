import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import database_structures.Customer;
import database_structures.Vendor;

public class Input {

  // Probably not ideal that everything is static, but it works well for what I am
  // doing. Only need one instance of this class and don't want to have to pass
  // it through.

  private static Scanner scan = new Scanner(System.in);

  private static boolean quitSet = false;
  private static boolean backSet = false;

  /**
   * Requests input without a required list of options. Free response.
   * 
   * Sets the quitSet and backSet flags.
   */
  public static String prompt(String text) {
    quitSet = false;
    backSet = false;

    System.out.print(text);
    String out = scan.nextLine();
    if (isBack(out)) {
      backSet = true;
    } else if (isQuit(out)) {
      quitSet = true;
    }
    return out;
  }

  // I'm very pleased I have an excuse to use lambda expressions. They're very
  // fun!
  public interface Search<T> {
    public List<T> find(String substring);
  }

  public static <T> T promptSearch(Search<T> search, String prompt_text, String empty_text) {
    // Input.prompt sets quitSet, backSet flags for me.
    String substring = Input.prompt(prompt_text);
    if (Input.isQuitSet() || Input.isBackSet()) {
      return null;
    }
    List<T> results = search.find(substring);
    int count = results.size();

    if (count == 0) {
      System.out.println(empty_text);
      return promptSearch(search, prompt_text, empty_text);
    } else if (count > 1) {
      // Check if they entered an exact match.
      for (T possibility : results) {
        if (possibility.toString().equals(substring)) {
          // This is the corresponding outpu.
          return possibility;
        }
      }
      // They didn't say a specific enough substring so list out the possible values
      // and prompt them again.
      System.out.println("Which of these did you mean: ");
      for (T result : results) {
        System.out.println(result.toString());
      }
      return promptSearch(search, prompt_text, empty_text);
    } else {
      // Only one result.
      System.out.println("You must have meant " + results.get(0).toString() + "! That's the only one that matches");
      return results.get(0);
    }
  }

  public static Vendor promptVendor(Connection conn) {
    return promptSearch((substring) -> ConnectionManager.selectVendors(substring, conn), "Enter the vendor name: ",
        "No vendors found with names like that");
  }

  public static Customer promptCustomer(Connection conn) {
    return promptSearch((substring) -> ConnectionManager.selectCustomers(substring, conn), "Enter your name: ",
        "No customers found with names like that");
  }

  public interface CustomCondition {
    public boolean isValidInput(String input);
  }

  public static String promptCustomString(String text, CustomCondition condition) {
    quitSet = false;
    backSet = false;

    System.out.println(text);
    String input = scan.nextLine();
    if (isQuit(input)) {
      quitSet = true;
      return null;
    } else if (isBack(input)) {
      backSet = true;
      return null;
    } else if (condition.isValidInput(input)) {
      return input;
    } else {
      System.out.println("Invalid input!");
      return promptCustomString(text, condition);
    }
  }

  public static Boolean promptBoolean(String text) {
    quitSet = false;
    backSet = false;

    System.out.println(text + " [y/n]");
    String input = scan.nextLine();
    if (input.toLowerCase().equals("yes") || input.toLowerCase().equals("y")) {
      return true;
    } else if (input.toLowerCase().equals("no") || input.toLowerCase().equals("n")) {
      return false;
    } else if (isBack(input)) {
      backSet = true;
      return null;
    } else if (isQuit(input)) {
      quitSet = true;
      return null;
    } else {
      System.out.println("Please enter yes or no");
      return promptBoolean(text);
    }
  }

  /**
   * Requests double value input.
   * 
   * Sets the quitSet and backSet flags. Returns null if one of them are set (this
   * way the program won't silently fail. for better or worse).
   */
  public static Double promptDouble(String text, boolean only_above_zero) {
    quitSet = false;
    backSet = false;

    System.out.println(text);
    String out = scan.nextLine();
    if (isBack(out)) {
      backSet = true;
      return null;
    } else if (isQuit(out)) {
      quitSet = true;
      return null;
    }

    try {
      double out_double = Double.parseDouble(out);
      if (only_above_zero && out_double <= 0) {
        System.out.println("Please enter a value greater than 0.");
        return promptDouble(text, only_above_zero);
      }
      return Double.parseDouble(out);
    } catch (NumberFormatException e) {
      System.out.println("Thats not a number!");
      return promptDouble(text, only_above_zero);
    }
  }

  /**
   * Prompts user with text requiring them to input corresponding number of
   * desired option or text of desired option. Generics are fun.
   * 
   * Sets the backSet and quitSet flags.
   * 
   * @return the text of the selected option.
   */
  public static <T> T prompt(String text, T[] options) {
    quitSet = false;
    backSet = false;

    System.out.println(text);
    for (int i = 1; i <= options.length; i++) {
      System.out.print("[" + i + "] " + options[i - 1].toString() + "      ");
    }
    System.out.println();
    String response = scan.nextLine();

    // If they entered one of the base menu controls.
    if (isBack(response)) {
      backSet = true;
      return null;
    } else if (isQuit(response)) {
      quitSet = true;
      return null;
    }
    // See if they entered exactly one of the options.
    for (T option : options) {
      if (response.toLowerCase().equals(option.toString().toLowerCase())) {
        return option;
      }
    }
    try {
      // Check if the response was an integer that corresponded to one of the values.
      // Sort of gross how java makes you use a try/catch to see if a string is a
      // number.
      int int_response = Integer.parseInt(response);
      if (int_response <= options.length && int_response > 0) {
        return options[int_response - 1];
      }
    } catch (NumberFormatException e) {
      // No need to error. This means the user did not enter a valid input which is
      // handled later.
    }

    // Reprompt the user.
    System.out.println("That was not one of the options!");
    // Fun with recursion.
    return prompt(text, options);
  }

  /**
   * Checks if string corresponds to the back command.
   */
  private static boolean isBack(String input) {
    return input.toLowerCase().equals("back");
  }

  /**
   * Checks if string corresponds to the quit command.
   */
  private static boolean isQuit(String input) {
    return input.toLowerCase().equals("quit");
  }

  public static boolean isBackSet() {
    return backSet;
  }

  public static boolean isQuitSet() {
    return quitSet;
  }
}