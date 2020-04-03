package database_structures;

import java.sql.Timestamp;

public class Customer extends Person {
  
  private Timestamp joinedDate;

  public Customer(long p_id, String full_name, Timestamp joined_date) {
    super(p_id, full_name);
    this.joinedDate = joined_date;
  }

  /**
   * @return the joinedDate
   */
  public Timestamp getJoinedDate() {
    return joinedDate;
  }
}