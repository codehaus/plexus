package org.codehaus.plexus.ldap.helper.factory;

import org.codehaus.plexus.util.StringUtils;

import javax.naming.spi.DirStateFactory;
import javax.naming.directory.Attributes;
import javax.naming.directory.SchemaViolationException;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: AbstractDirStateFactory.java,v 1.1 2006/02/11 19:03:12 trygvis Exp $
 */
public abstract class AbstractDirStateFactory
    implements DirStateFactory
{
    public Object getStateToBind( Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment )
        throws NamingException
    {
        return null;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void setRequiredAttribute( Attributes attributes, String key, int value )
        throws SchemaViolationException
    {
        setRequiredAttribute( attributes, key, Integer.toString( value ) );
    }

    protected void setRequiredAttribute( Attributes attributes, String key, Object value )
        throws SchemaViolationException
    {
        if ( attributes.get( key ) != null && attributes.get( key ).size() > 0 )
        {
            return;
        }

        if ( value == null )
        {
            throw new SchemaViolationException( "Missing value for key '" + key + "'." );
        }

        if ( value instanceof String && StringUtils.isEmpty( (String) value ) )
        {
            throw new SchemaViolationException( "Missing value for key '" + key + "'." );
        }

        attributes.put( key, value );
    }

    protected void setOptionalAttribute( Attributes attributes, String key, Object value )
        throws SchemaViolationException
    {
        if ( value == null )
        {
            return;
        }

        if ( value instanceof String && StringUtils.isEmpty( (String) value ) )
        {
            return;
        }

        attributes.put( key, value );
    }
}
