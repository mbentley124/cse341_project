package database_structures;

public class Vendor {
  private long vId;
  private String vendorName;

  public Vendor(long v_id, String vendor_name) {
    this.vId = v_id;
    this.vendorName = vendor_name;
  }

  /**
   * @return the vendorName
   */
  public String getVendorName() {
    return vendorName;
  }

  /**
   * @return the vId
   */
  public long getVId() {
    return vId;
  }

  @Override
  public String toString() {
    return this.getVendorName();
  }
}