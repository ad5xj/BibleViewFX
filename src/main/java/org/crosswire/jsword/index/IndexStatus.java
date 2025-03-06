package org.crosswire.jsword.index;

public enum IndexStatus {
  DONE("Indexed"),
  UNDONE("No Index"),
  SCHEDULED("Scheduled"),
  CREATING("Creating"),
  INVALID("Invalid");
  
  private String name;
  
  IndexStatus(String name) {
    this.name = name;
  }
  
  public static IndexStatus fromString(String name) {
    for (IndexStatus o : values()) {
      if (o.name.equalsIgnoreCase(name))
        return o; 
    } 
    assert false;
    return null;
  }
  
  public String toString() {
    return this.name;
  }
}

