package org.codehaus.plexus.cling.cli;

/**
 * @author John Casey
 * @version
 */
public interface Option
{

    public static final char EMPTY_SHORT_NAME = '\00';

    public static final String EMPTY_LONG_NAME = null;

    public boolean isRequired();

    public Character getShortName();

    public String getLongName();
    
    public String getObjectProperty();

    public boolean requiresValue();

    public String getUsage();

    public boolean isSatisfied();

}