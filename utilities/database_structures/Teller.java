package utilities.database_structures;

public class Teller extends Person {

  // It would be sort of neat to have a data structure that slowly built itself
  // out. i.e. Teller would have getLocation method which would get the location
  // from the id in the table. Would cache the location after the getLocation
  // method is first called. This would sort of allow for a simple way to sort of
  // "remember" which tellers get linked to which location, and other similar
  // relations such as that. This would make everything less reliant on the
  // ConnectionManager object and more built into these "database objects". Not
  // sure how beneficial it would really be but sort of fun to consider. Would
  // likely make more sense in a more used database as it would help limit sql
  // queries, though less so in this projects context.
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