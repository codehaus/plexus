package org.codehaus.plexus.cling.cli;

/** Represents a command line non-argument option that (a) accepts exactly one
 * argument to itself, and (b) is not required for a valid command line.
 *
 * @author  John Casey
 */
public class SingleArgOption extends AbstractArgOption{
  
  /** Creates new SingleArgOption 
   * @param shortName The single-character short name for this option.
   * @param longNmae The verbose, long name for this option.
   * @param format The OptionFormat used in validating and translating the option's value.
   * @param description The description of what purpose this option serves to the command line.
   */
  public SingleArgOption(boolean required, Character shortName, String longName, OptionFormat format, String description, String objectProperty) {
    super(required, shortName, longName, format, description, objectProperty);
  }

}