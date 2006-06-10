package org.codehaus.plexus.ldap.helper.factory;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.BasicAttribute;
import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.DirStateFactory;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLdapFactory<T>
    implements DirObjectFactory, DirStateFactory
{
    private static LdapFactoryHelper helper;

    private Class clazz;

    /**
     * The LDAP objectClasses that the attributes must contain.
     */
    private String[] requiredObjectClasses;

    /**
     * This is the object class representing clazz's ldap object. It's the aggregate of all the required object
     * classes
     */
    private ObjectClassDescriptor objectClass;

    private Name name;

    private Context nameCtx;

    private Hashtable<?, ?> environment;

    private Attributes attributes;

    public AbstractLdapFactory( Class clazz, String[] requiredObjectClasses )
    {
        this.clazz = clazz;
        // TODO: Assert that the "top" object class is here or just add it later?
        // Or just let the LDAP server fail if it's required and make the application handle it?
        this.requiredObjectClasses = requiredObjectClasses;
    }

    // ----------------------------------------------------------------------
    // Abstract Methods
    // ----------------------------------------------------------------------

    protected abstract T createObject()
        throws NamingException;

    protected abstract void createAttributes( T object )
        throws NamingException;

    // ----------------------------------------------------------------------
    // Methods for the implementations
    // ----------------------------------------------------------------------

    public Name getName()
    {
        return name;
    }

    public Context getNameCtx()
    {
        return nameCtx;
    }

    public Hashtable<?, ?> getEnvironment()
    {
        return environment;
    }

    public Attributes getAttributes()
    {
        return attributes;
    }

    // ----------------------------------------------------------------------
    // DirObjectFactory and DirStateFactory Implementations
    // ----------------------------------------------------------------------

    public T getObjectInstance( Object object, Name name, Context nameCtx, Hashtable<?, ?> environment,
                                     Attributes attributes )
        throws Exception
    {
        this.name = name;
        this.nameCtx = nameCtx;
        this.environment = environment;
        this.attributes = attributes;

        // ----------------------------------------------------------------------
        // Check that the entry contains all the required object classes.
        // ----------------------------------------------------------------------

        Attribute objectClass = getAttribute( "objectClass" );

        for ( String requiredObjectClass : requiredObjectClasses )
        {
            if ( !objectClass.contains( requiredObjectClass ) )
            {
                return null;
            }
        }

        getAggregatedObjectClass();

        return createObject();
    }

    public Result getStateToBind( Object object, Name name, Context nameCtx, Hashtable<?, ?> environment,
                                  Attributes attributes )
        throws NamingException
    {
        if ( ! clazz.isAssignableFrom( object.getClass() ) )
        {
            return null;
        }

        getAggregatedObjectClass();

        if ( attributes != null )
        {
            attributes = (Attributes) attributes.clone();
        }
        else
        {
            attributes = new BasicAttributes();
        }

        // ----------------------------------------------------------------------
        // Copy over the parameters and create the attributes
        // ----------------------------------------------------------------------

        this.name = name;
        this.nameCtx = nameCtx;
        this.environment = environment;
        this.attributes = attributes;

        createAttributes( (T) object );

        // ----------------------------------------------------------------------
        // Set the object class attribute
        // ----------------------------------------------------------------------

        Attribute objectClass = getAttribute( "objectClass" );

        if ( objectClass == null )
        {
            objectClass = new BasicAttribute( "objectClass" );
        }

        for ( String requiredObjectClass : requiredObjectClasses )
        {
            if ( !objectClass.contains( requiredObjectClass ) )
            {
                objectClass.add( requiredObjectClass );
            }
        }

        this.attributes.put( objectClass );

        return new Result( null, this.attributes );
    }

    private void getAggregatedObjectClass()
        throws NamingException
    {
        try
        {
            objectClass = getHelper().getAggregatedObjectClass( requiredObjectClasses );
        }
        catch ( LdapFactoryHelperException e )
        {
            NamingException e2 = new NamingException( "Error while generating the aggregated object class for " + clazz.getName() );
            e2.setRootCause( e );
            throw e2;
        }
    }

    // ----------------------------------------------------------------------
    // These two are returning null by JNDI contract.
    // ----------------------------------------------------------------------

    public Object getObjectInstance( Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment )
        throws Exception
    {
        return null;
    }

    public Object getStateToBind( Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment )
        throws NamingException
    {
        return null;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static void setHelper( LdapFactoryHelper helper )
    {
        AbstractLdapFactory.helper = helper;
    }

    private static LdapFactoryHelper getHelper()
    {
        if ( helper == null )
        {
            throw new RuntimeException( "The ldap factory helper has to be set before trying to use this class." );
        }

        return helper;
    }

    // ----------------------------------------------------------------------
    // Attribute Getters
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

    protected String getStringAttribute( String attributeName )
        throws NamingException
    {
        Object value = getAttributeValue( attributeName );

        if ( value == null || value instanceof String )
        {
            return (String) value;
        }

        if ( value instanceof byte[] )
        {
            return new String( (byte[]) value );
        }

        throw new NamingException( "The attribute '" + attributeName + "' is not of type String or byte[] " +
            "but rather '" + value.getClass().getName() + "'." );
    }

    protected List<String> getStringList( String attributeName )
        throws NamingException
    {
        Attribute attribute = getAttributes().get( attributeName );

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

    protected int getIntAttribute( String attributeName )
        throws NamingException
    {
        String value = getStringAttribute( attributeName );

        return Integer.parseInt( value );
    }
/*
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
*/
    // ----------------------------------------------------------------------
    // Attribute Setters
    // ----------------------------------------------------------------------

    protected void setAttribute( String attributeName, String value )
        throws NamingException
    {
        if ( value == null || value.trim().length() == 0 )
        {
            value = null;
        }

        setAttribute( attributeName, (Object) value );
    }

    protected void setAttribute( String attributeName, int value )
        throws NamingException
    {
        setAttribute( attributeName, Integer.toString( value ) );
    }

    protected void setAttribute( String attributeName, List<String> strings )
        throws NamingException
    {
        if ( strings == null || strings.size() == 0 )
        {
            // TODO: Here the attribute should probably be explicitly removed
            return;
        }

        setAttribute( attributeName, (Object) strings );
    }

    private void setAttribute( String attributeName, Object value )
        throws NamingException
    {
        AttributeTypeDescriptor descriptor;

        descriptor = objectClass.getRequiredAttribute( attributeName );

        if ( descriptor != null )
        {
            if ( value == null )
            {
                throw new SchemaViolationException( "Cannot set the value of '" + attributeName + "' to null." );
            }

            Attribute attribute = new BasicAttribute( descriptor.getPrimaryName(), value );
            attributes.put( attribute );

            return;
        }

        descriptor = objectClass.getOptionalAttribute( attributeName );

        if ( descriptor != null )
        {
            if ( value == null )
            {
                // TODO: Figure out if setting a null value should remove any existing attributes
//                attributes.remove( attributeName );

                return;
            }

            Attribute attribute = new BasicAttribute( descriptor.getPrimaryName(), value );
            attributes.put( attribute );

            return;
        }

        throw new SchemaViolationException( "The attribute '" + attributeName + "' is not a part of this class (" + clazz.getName() + ")." );
    }

    // ----------------------------------------------------------------------
    // Privates
    // ----------------------------------------------------------------------

    private Attribute getAttribute( String attributeName )
        throws NamingException
    {
        NamingEnumeration<? extends Attribute> it = attributes.getAll();

        while( it.hasMore() )
        {
            Attribute attribute = it.nextElement();

            if ( attribute.getID().equalsIgnoreCase( attributeName ) )
            {
                return attribute;
            }
        }

        return null;
    }

    /**
     * For each attribute:
     *
     *   See if it matches the descriptor's primary name, if so return it.
     *   For each alias in the descriptor, see if it matches the attribute's name, if so return it.
     */
    private Attribute getAttribute( AttributeTypeDescriptor descriptor )
        throws NamingException
    {
        NamingEnumeration<? extends Attribute> it = attributes.getAll();

        while( it.hasMore() )
        {
            Attribute attribute = it.nextElement();

            if ( attribute.getID().equalsIgnoreCase( descriptor.getPrimaryName() ) )
            {
                return attribute;
            }

            for ( String alias : descriptor.getAliases() )
            {
                if ( attribute.getID().equalsIgnoreCase( alias ) )
                {
                    return attribute;
                }
            }
        }

        return null;
    }

    private Object getAttributeValue( String attributeName )
        throws NamingException
    {
        AttributeTypeDescriptor descriptor = objectClass.getRequiredAttribute( attributeName );

        if ( descriptor != null )
        {
            Attribute attribute = getAttribute( descriptor );

            if ( attribute == null )
            {
                throw new NamingException( "Missing required attribute '" + attributeName + "' for class " + clazz.getName() + "." );
            }

            return attribute.get();
        }

        descriptor = objectClass.getOptionalAttribute( attributeName );

        if ( descriptor != null )
        {
            Attribute attribute = getAttribute( descriptor );

            if ( attribute == null )
            {
                return null;
            }

            return attribute.get();
        }

        throw new NamingException( "The attribute '" + attributeName + "' is not a part of this class (" + clazz.getName() + ")." );
    }
}
