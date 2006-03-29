package org.codehaus.plexus.ldap.helper.factory;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AttributeType
{
    private String name;

    private String aliases;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getAliases()
    {
        return aliases;
    }

    public void setAliases( String aliases )
    {
        this.aliases = aliases;
    }
}
