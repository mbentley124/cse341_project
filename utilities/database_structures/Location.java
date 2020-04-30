package utilities.database_structures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import utilities.ResultSetConverter;

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

  /**
   * Returns all the tellers at this location.
   * 
   * @param conn The database connection
   * @return All the tellers who work at this location
   */
  public List<Teller> selectTellers(Connection conn) {
    List<Teller> tellers = new ArrayList<>();

    try (PreparedStatement dept_search = conn.prepareStatement(
        "SELECT * FROM person JOIN teller USING (p_id) JOIN location on location.loc_id = teller.teller_loc_id WHERE loc_id = ?")) {
      dept_search.setLong(1, this.getLocId());
      tellers = ResultSetConverter.toTellers(dept_search.executeQuery());
    } catch (SQLException e) {
      return null;
    }
    return tellers;
  }
}