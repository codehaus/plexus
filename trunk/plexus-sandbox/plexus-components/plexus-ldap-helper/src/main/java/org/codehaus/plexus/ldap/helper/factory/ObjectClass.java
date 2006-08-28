package org.codehaus.plexus.ldap.helper.factory;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ObjectClass
{
    private String name;

    private String inherits;

    private String requiredAttributes;

    private String optionalAttributes;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getInherits()
    {
        return inherits;
    }

    public void setInherits( String inherits )
    {
        this.inherits = inherits;
    }

    public String getRequiredAttributes()
    {
        return requiredAttributes;
    }

    public void setRequiredAttributes( String requiredAttributes )
    {
        this.requiredAttributes = requiredAttributes;
    }

    public String getOptionalAttributes()
    {
        return optionalAttributes;
    }

    public void setOptionalAttributes( String optionalAttributes )
    {
        this.optionalAttributes = optionalAttributes;
    }
}
