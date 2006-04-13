package org.codehaus.plexus.cling.cli;

/**
 * @author John Casey
 */
public class NoArgOption
    extends AbstractOption
{

    private boolean set = false;

    /** Creates new NoArgOption */
    public NoArgOption( boolean required, Character shortName, String longName, String description, String objectProperty )
    {
        super(required, shortName, longName, description, objectProperty);
    }

    public boolean isSet()
    {
        return set;
    }

    public void set()
    {
        set = true;
    }

    public boolean isSatisfied()
    {
        return set || !isRequired();
    }

    public boolean requiresValue()
    {
        return false;
    }

}