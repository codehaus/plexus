package org.codehaus.plexus.ldap.helper.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collection;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ObjectClassDescriptor
{
    private String name;

    private ObjectClassDescriptor parent;

    private Map<String, AttributeTypeDescriptor> requiredAttributes;

    private Map<String, AttributeTypeDescriptor> optionalAttributes;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public ObjectClassDescriptor getParent()
    {
        return parent;
    }

    public void setParent( ObjectClassDescriptor parent )
    {
        this.parent = parent;
    }

    public Map<String, AttributeTypeDescriptor> getRequiredAttributes()
    {
        if ( requiredAttributes == null )
        {
            requiredAttributes = new HashMap<String, AttributeTypeDescriptor>();
        }

        return requiredAttributes;
    }

    public AttributeTypeDescriptor getRequiredAttribute( String attributeName )
    {
        return getAliasedAttribute( getRequiredAttributes().values(), attributeName );
    }

    public void setRequiredAttributes( Map<String, AttributeTypeDescriptor> requiredAttributes )
    {
        this.requiredAttributes = requiredAttributes;
    }

    public Map<String, AttributeTypeDescriptor> getOptionalAttributes()
    {
        if ( optionalAttributes == null )
        {
            optionalAttributes = new HashMap<String, AttributeTypeDescriptor>();
        }

        return optionalAttributes;
    }

    public AttributeTypeDescriptor getOptionalAttribute( String attributeName )
    {
        return getAliasedAttribute( getOptionalAttributes().values(), attributeName );
    }

    public void setOptionalAttributes( Map<String, AttributeTypeDescriptor> optionalAttributes )
    {
        this.optionalAttributes = optionalAttributes;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private AttributeTypeDescriptor getAliasedAttribute( Collection<AttributeTypeDescriptor> attributes, String attributeName )
    {
        for ( AttributeTypeDescriptor attributeTypeDescriptor : attributes )
        {
            if ( attributeTypeDescriptor.getPrimaryName().equals( attributeName ) )
            {
                return attributeTypeDescriptor;
            }

            for ( String alias : attributeTypeDescriptor.getAliases() )
            {
                if ( alias.equals( attributeName ) )
                {
                    return attributeTypeDescriptor;
                }
            }
        }

        return null;
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

        ObjectClassDescriptor that = (ObjectClassDescriptor) o;

        return name.equals( that.name );
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public String toString()
    {
        String p = "";

        if ( parent != null )
        {
            p = "parent='" + parent.getName() + "'\n";
        }

        return "{ObjectClassDescriptor: \n" +
               "name='" + name + "'\n" +
               p +
               "requiredAttributes=" + new TreeMap<String, AttributeTypeDescriptor>( requiredAttributes ) + "\n" +
               "optionalAttributes=" + new TreeMap<String, AttributeTypeDescriptor>( optionalAttributes ) + "}";
    }
}
