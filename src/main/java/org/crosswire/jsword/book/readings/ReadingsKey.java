package org.crosswire.jsword.book.readings;

import org.crosswire.common.icu.DateFormatter;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;

import java.text.MessageFormat;

import java.util.Calendar;
import java.util.Date;

public class ReadingsKey extends DefaultLeafKeyList {
  
  private static final long serialVersionUID = -5500401548068844993L;
  private static final MessageFormat KEY_FORMAT = new MessageFormat("{0,number,00}.{1,number,00}");
  
  private Date date;
  
  protected ReadingsKey(String text, String osisName, Key parent) {
    super(text, osisName, parent);
    DateFormatter formatter = DateFormatter.getDateInstance();
    formatter.setLenient(true);
    this.date = formatter.parse(text);
  }
  
  protected ReadingsKey(Date date) {
    super(DateFormatter.getDateInstance().format(date), DateFormatter.getSimpleDateInstance("d.MMMM").format(date));
    this.date = date;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (!obj.getClass().equals(getClass()))
      return false; 
    ReadingsKey that = (ReadingsKey)obj;
    return getName().equals(that.getName());
  }
  
  public int hashCode() {
    return this.date.hashCode();
  }
  
  public int compareTo(Key obj) {
    ReadingsKey that = (ReadingsKey)obj;
    return this.date.compareTo(that.date);
  }
  
  public ReadingsKey clone() {
    return (ReadingsKey)super.clone();
  }
  
  public static String external2internal(Calendar externalKey) {
    Object[] objs = { Integer.valueOf(1 + externalKey.get(2)), Integer.valueOf(externalKey.get(5)) };
    return KEY_FORMAT.format(objs);
  }
}