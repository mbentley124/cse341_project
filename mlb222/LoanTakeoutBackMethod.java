package mlb222;

/**
   * An enum representing the method to go back to after the user enters 'back'.
   * This is required for methods that are called from multiple other methods,
   * otherwise its ambiguious where back goes to.
   */
  public enum LoanTakeoutBackMethod {
    TELLER, LOCATION, LOAN_AMOUNT, ACCOUNT_OR_CASH, WHICH_ACCOUNT, HAS_COLATORAL, GET_COLATORAL
  }