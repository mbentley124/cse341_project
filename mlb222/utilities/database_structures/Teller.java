package mlb222.utilities.database_structures;

public class Teller extends Person {

  long tellerLocId;
  double wage;
  boolean isAtm;

  public Teller(long p_id, String full_name, long teller_loc_id, double wage, boolean is_atm) {
    super(p_id, full_name);
    this.tellerLocId = teller_loc_id;
    this.wage = wage;
    this.isAtm = is_atm;
  }

  /**
   * @return the tellerLocId
   */
  public long getTellerLocId() {
    return tellerLocId;
  }

  /**
   * @return the wage
   */
  public double getWage() {
    return wage;
  }

  /**
   * @return if this is an atm
   */
  public boolean isAtm() {
    return isAtm;
  }

  @Override
  public String toString() {
    return this.getFullName();
  }
}