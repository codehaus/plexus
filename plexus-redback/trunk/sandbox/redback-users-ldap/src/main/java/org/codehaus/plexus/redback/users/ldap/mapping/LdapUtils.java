package org.codehaus.plexus.redback.users.ldap.mapping;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public final class LdapUtils
{

    private LdapUtils()
    {
    }

    @SuppressWarnings("unchecked")
    public static String getLabeledUriValue( Attributes attributes, String attrName, String label, String attributeDescription )
        throws MappingException
    {
        Attribute attribute = attributes.get( attrName );
        if ( attribute != null )
        {
            NamingEnumeration attrs;
            try
            {
                attrs = attribute.getAll();
            }
            catch ( NamingException e )
            {
                throw new MappingException( "Failed to retrieve " + attributeDescription + " (attribute: \'" + attrName
                    + "\').", e );
            }

            while ( attrs.hasMoreElements() )
            {
                Object value = attrs.nextElement();

                String val = String.valueOf( value );

                if ( val.endsWith( " " + label ) )
                {
                    return val.substring( 0, val.length() - ( label.length() + 1 ) );
                }
            }
        }

        return null;
    }

    public static String getAttributeValue( Attributes attributes, String attrName, String attributeDescription )
        throws MappingException
    {
        Attribute attribute = attributes.get( attrName );
        if ( attribute != null )
        {
            try
            {
                Object value = attribute.get();

                return String.valueOf( value );
            }
            catch ( NamingException e )
            {
                throw new MappingException( "Failed to retrieve " + attributeDescription + " (attribute: \'" + attrName
                    + "\').", e );
            }
        }

        return null;
    }

    public static String getAttributeValueFromByteArray( Attributes attributes, String attrName, String attributeDescription )
        throws MappingException
    {
        Attribute attribute = attributes.get( attrName );
        if ( attribute != null )
        {
            try
            {
                byte[] value = (byte[]) attribute.get();

                return new String( value );
            }
            catch ( NamingException e )
            {
                throw new MappingException( "Failed to retrieve " + attributeDescription + " (attribute: \'" + attrName
                    + "\').", e );
            }
        }

        return null;
    }
}
