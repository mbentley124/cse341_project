package database_structures;

public class Location {

  private long locId;
  private String locName;

  public Location(long loc_id, String loc_name) {
    this.locId = loc_id;
    this.locName = loc_name;
  }

  /**
   * @return the locId
   */
  public long getLocId() {
    return locId;
  }

  /**
   * @return the locName
   */
  public String getLocName() {
    return locName;
  }

  @Override
  public String toString() {
    return this.getLocName();
  }
}