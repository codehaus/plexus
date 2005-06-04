package org.codehaus.plexus.summit.view;

/**
 * <p>The base class from which all <code>View</code>s are derived.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractView
    implements View
{
    private String name;

    public AbstractView( String name )
    {
        this.name = name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
