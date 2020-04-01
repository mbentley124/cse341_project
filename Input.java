import java.util.Scanner;

public class Input {

  private static Scanner scan = new Scanner(System.in);

  /**
   * Requests input without a required list of options. Free response. 
   */
  public static String prompt(String text) {
    System.out.println(text);
    return scan.nextLine();
  }

  /**
   * Prompts user with text requiring them to input corresponding number of
   * desired option or text of desired option.
   * 
   * @return the text of the selected option.
   */
  public static String prompt(String text, String[] options) {
    System.out.println(text);
    for (int i = 1; i <= options.length; i++) {
      System.out.print("[" + i + "] " + options[i - 1] + "      ");
    }
    System.out.println();
    String response = scan.nextLine();
    // See if they entered exactly one of the options.
    for (String option : options) {
      if (response.equals(option)) {
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
    System.out.println("That was not a valid response.");
    // Fun with recursion.
    return prompt(text, options);
  }
}