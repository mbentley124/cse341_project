package mlb222;

// All this back functionality would be so much easier to implement if
  // java supported pointers to functions. And more fun too!
  // Now I have to do something gross like this instead. Eww.
  // I'm usually a fan of enums too (they have some neato functionality)
  // but I just dislike this.
  public enum DepositWithdrawBackMethod {
    PROMPT_LOCATION, PROMPT_TELLER, PROMPT_WITHDRAW_DEPOSIT
  }