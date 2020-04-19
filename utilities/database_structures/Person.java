package utilities.database_structures;

public class Person {

  private String fullName;
  private long pId;

  public Person(long p_id, String full_name) {
    this.fullName = full_name;
    this.pId = p_id;
  }

  /**
   * @return the pId
   */
  public long getPId() {
    return pId;
  }

  /**
   * @return the fullName
   */
  public String getFullName() {
    return fullName;
  }

  @Override
  public String toString() {
    return this.getFullName();
  }
}