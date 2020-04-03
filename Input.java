import java.util.Scanner;

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