package org.codehaus.plexus.ldap.helper.factory;

import org.codehaus.plexus.logging.AbstractLogEnabled;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.spi.DirObjectFactory;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractDirObjectFactory.java,v 1.1 2006/02/06 16:24:25 trygvis Exp $
 */
public abstract class AbstractDirObjectFactory
    extends AbstractLogEnabled
    implements DirObjectFactory
{
    // ----------------------------------------------------------------------
    // ObjectFactory Implementation
    // ----------------------------------------------------------------------

    public Object getObjectInstance( Object object, Name name, Context nameCtx, Hashtable<?, ?> environment )
        throws Exception
    {
        return null;
    }

    // ----------------------------------------------------------------------
    // Utils
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Attribute Retrieval
    // ----------------------------------------------------------------------

    protected boolean requiredObjectClass( Attributes attributes, String objectClass )
        throws NamingException
    {
        Attribute attribute = attributes.get( "objectClass" );

        NamingEnumeration<?> values = attribute.getAll();

        while ( values.hasMore() )
        {
            Object value = values.next();

            if ( value != null && value instanceof String && value.toString().equals( objectClass ) )
            {
                return true;
            }
        }

        return false;
    }

    protected List<String> getStringList( Attributes attributes, String attributeName )
        throws NamingException
    {
        Attribute attribute = attributes.get( attributeName );

        List<String> strings = new ArrayList<String>();

        if ( attribute == null )
        {
            return strings;
        }

        NamingEnumeration<?> values = attribute.getAll();

        while( values.hasMore() )
        {
            Object value = values.next();

            if ( value instanceof String )
            {
                strings.add( (String) value );
            }
            if ( value instanceof byte[] )
            {
                strings.add( new String( (byte[]) value ) );
            }
        }

        return strings;
    }

    protected int getIntAttribute( Attributes attributes, String attributeName )
        throws NamingException
    {
        String value = getStringAttribute( attributes, attributeName );

        return Integer.parseInt( value );
    }

    protected String getOptionalStringAttribute( Attributes attributes, String attributeName )
        throws NamingException
    {
        Object value = getOptionalAttribute( attributes, attributeName );

        if ( value == null || value instanceof String )
        {
            return (String) value;
        }

        if ( value instanceof byte[] )
        {
            return new String( (byte[]) value );
        }

        throw new NamingException( "The attribute '" + attributeName + "' is not of type String but rather '" + value.getClass().getName() + "'." );
    }

    protected String getStringAttribute( Attributes attributes, String attributeName )
        throws NamingException
    {
        String value = getOptionalStringAttribute( attributes, attributeName );

        if ( value == null )
        {
            throw new NamingException( "Missing required attribute '" + attributeName + "'." );
        }

        return value;
    }

    protected File getFileAttribute( Attributes attributes, String attributeName )
        throws NamingException
    {
        String value = getOptionalStringAttribute( attributes, attributeName );

        if ( value == null )
        {
            throw new NamingException( "Missing required attribute '" + attributeName + "'." );
        }

        return new File( value );
    }

    protected Object getAttribute( Attributes attributes, String attributeName )
        throws NamingException
    {
        Object value = getOptionalAttribute( attributes, attributeName );

        if ( value == null )
        {
            throw new NamingException( "Missing required attribute '" + attributeName + "'." );
        }

        return value;
    }

    protected Object getOptionalAttribute( Attributes attributes, String attributeName )
        throws NamingException
    {
        NamingEnumeration<? extends Attribute> it = attributes.getAll();

        while( it.hasMore() )
        {
            Attribute attribute = it.nextElement();

            if ( attribute.getID().equalsIgnoreCase( attributeName ) )
            {
                return attribute.get();
            }
        }

        return null;
    }
}
