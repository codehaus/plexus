package org.codehaus.plexus.cling.cli;

/** Represents a command line non-argument option that (a) accepts exactly one
 * argument to itself, and (b) is required for a valid command line.
 *
 * @author  John Casey
 */
public abstract class AbstractArgOption extends AbstractOption {
  
  private OptionFormat format;
  private Object value;

  /** Creates new AbstractArgOption 
   * @param shortName The single-character short name for this option.
   * @param longNmae The verbose, long name for this option.
   * @param format The OptionFormat used in validating and translating the option's value.
   * @param description The description of what purpose this option serves to the command line.
   */
  protected AbstractArgOption(boolean required, Character shortName, String longName, OptionFormat format, String description, String objectProperty) {
    super(required, shortName, longName, description, objectProperty);
    this.format = format;
  }

  /** Return whether this option has had a value set on it.
   *
   * @return true if this option contains a value, else false.
   */
  public boolean hasValue() {
    return value != null;
  }

  /** Return the value of this option.
   *
   * @return the value.
   */
  public Object getValue() {
    return value;
  }
  
  /** retrieve the option format used in this option.
   * 
   * @return the format for a single value of this option.  NOTE: If this
   * option allows multiple values, then each single item in the list will
   * be validated/formatted by this format.
   */
  protected final OptionFormat getFormat(){
    return format;
  }

  /** Sets (after validating) the value specified on this option.
   *
   * @param the value to validate and then set on this option.
   */
  public void setValue(String value) {
    if(format.isValid(value)){
      this.value = format.getValue(value);
    }
  }
  
  public boolean isValueValid(String value){
    return format.isValid(value);
  }

  /** Return a whether or not this option is satisfied.  In order to be satisfied,
   * a valid value has to have been set on it.
   *
   * @return true if value != null, else false.
   */
  public boolean isSatisfied() {
    return !isRequired() || value != null;
  }
  
  /** Return true: all Arg implementations should...
   *
   * @return true.
   */
  public boolean requiresValue(){
    return true;
  }
  
}