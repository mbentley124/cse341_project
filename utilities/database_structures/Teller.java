package utilities.database_structures;

public class Teller extends Person {

  long tellerLocId;
  double wage;

  public Teller(long p_id, String full_name, long teller_loc_id, double wage) {
    super(p_id, full_name);
    this.tellerLocId = teller_loc_id;
    this.wage = wage;
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

  public boolean isAtm() {
    return this.getFullName().endsWith(" ATM") && wage == 0;
  }

  @Override
  public String toString() {
    return this.getFullName();
  }
}