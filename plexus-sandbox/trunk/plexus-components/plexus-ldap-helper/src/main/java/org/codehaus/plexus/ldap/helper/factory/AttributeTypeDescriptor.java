package org.codehaus.plexus.ldap.helper.factory;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AttributeTypeDescriptor
{
    private String primaryName;

    private List<String> aliases;

    public String getPrimaryName()
    {
        return primaryName;
    }

    public void setPrimaryName( String primaryName )
    {
        this.primaryName = primaryName;
    }

    public List<String> getAliases()
    {
        if ( aliases == null )
        {
            aliases = new ArrayList<String>();
        }

        return aliases;
    }

    public void setAliases( List<String> aliases )
    {
        this.aliases = aliases;
    }

    // ----------------------------------------------------------------------
    // Object Overrides
    // ----------------------------------------------------------------------

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        AttributeTypeDescriptor that = (AttributeTypeDescriptor) o;

        return primaryName.equals( that.primaryName );
    }

    public int hashCode()
    {
        return primaryName.hashCode();
    }

    public String toString()
    {
        return "{AttributeTypeDescriptor: " +
               "primary name=" + primaryName + ", " +
               "aliases=" + new TreeSet<String>( getAliases() ) + "}";
    }
}
