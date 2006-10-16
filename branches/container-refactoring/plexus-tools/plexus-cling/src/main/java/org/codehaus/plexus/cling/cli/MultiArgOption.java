package org.codehaus.plexus.cling.cli;

/**
 * @author John Casey
 */
public class MultiArgOption
    extends AbstractArgOption
{

    public MultiArgOption( boolean required, Character shortName, String longName, OptionFormat format, String splitPattern,
        String description, String objectProperty )
    {
        super( required, shortName, longName, new ListOptionFormat( splitPattern, format ), description, objectProperty );
    }

    public Object[] getValues()
    {
        return (Object[]) getValue();
    }

}