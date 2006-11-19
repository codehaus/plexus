package org.codehaus.plexus.ldap.helper;

import org.codehaus.plexus.ldap.helper.factory.AbstractLdapFactory;
import org.codehaus.plexus.ldap.helper.factory.LdapFactoryHelper;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: DefaultLdapHelper.java,v 1.1 2006/02/11 18:31:32 trygvis Exp $
 */
public class DefaultLdapHelper<T>
    extends AbstractLogEnabled
    implements LdapHelper, Initializable
{
    // ----------------------------------------------------------------------
    // Requirements
    // ----------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private LdapConnectionFactory connectionFactory;

    /**
     * @plexus.requirement
     */
    private LdapFactoryHelper ldapFactoryHelper;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * @plexus.configuration
     */
    private LdapName baseDnLdapName;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void addObjectFactory( Class objectFactoryClass )
    {
        setHelper( objectFactoryClass );

        connectionFactory.addObjectFactory( objectFactoryClass );
    }

    public void addStateFactory( Class stateFactoryClass )
    {
        setHelper( stateFactoryClass );

        connectionFactory.addStateFactory( stateFactoryClass );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public T lookupObject( LdapName objectName )
        throws NamingException
    {
        LdapConnection connection = null;

        try
        {
            connection = connectionFactory.getConnection();

            return (T)connection.getDirContext().lookup( objectName );
        }
        catch ( NameNotFoundException e )
        {
            return null;
        }
        finally
        {
            closeConnection( connection );
        }
    }

    public T lookupObject( Rdn organizationalUnit, Rdn objectRdn )
        throws NamingException
    {
        return lookupObject( makeName( organizationalUnit, objectRdn ) );
    }

    public void bindObject( LdapName objectName, Object object )
        throws NamingException
    {
        LdapConnection connection = null;

        try
        {
            connection = connectionFactory.getConnection();

            connection.getDirContext().bind( objectName, object );
        }
        finally
        {
            closeConnection( connection );
        }
    }

    public void bindObject( Rdn organizationalUnit, Rdn rdn, Object object )
        throws NamingException
    {
        bindObject( makeName( organizationalUnit, rdn ), object );
    }

    public void rebindObject( LdapName objectName, Object object )
        throws NamingException
    {
        LdapConnection connection = null;

        try
        {
            connection = connectionFactory.getConnection();

            connection.getDirContext().rebind( objectName, object );
        }
        finally
        {
            closeConnection( connection );
        }
    }

    public void rebindObject( Rdn organizationalUnit, Rdn objectRdn, Object object )
        throws NamingException
    {
        rebindObject( makeName( organizationalUnit, objectRdn ), object );
    }

    public void unbindObject( LdapName objectName )
        throws NamingException
    {
        LdapConnection connection = null;

        try
        {
            connection = connectionFactory.getConnection();

            connection.getDirContext().unbind( objectName );
        }
        finally
        {
            closeConnection( connection );
        }
    }

    public void unbindObject( Rdn organizationalUnit, Rdn objectRdn )
        throws NamingException
    {
        unbindObject( makeName( organizationalUnit, objectRdn ) );
    }

    // ----------------------------------------------------------------------
    // Searching
    // ----------------------------------------------------------------------

    public List<Attributes> searchForAttributes( Rdn rdn, String filter, Object[] filterArguments )
        throws NamingException
    {
        return searchForAttributes( rdn, filter, filterArguments, null );
    }

    public List<Attributes> searchForAttributes( Rdn rdn, String filter, Object[] filterArguments,
                                                 String[] attributesToReturn )
        throws NamingException
    {
        SearchControls searchControls = new SearchControls( SearchControls.SUBTREE_SCOPE, 0, 0, attributesToReturn, false, true );

        NamingEnumeration<SearchResult> e = search( rdn, filter, filterArguments, searchControls );

        List<Attributes> attributesList = new ArrayList<Attributes>();

        while( e.hasMore() )
        {
            attributesList.add( e.next().getAttributes() );
        }

        return attributesList;
    }

    public List<T> searchForObjects( Rdn rdn, String filter, Object[] filterArguments )
        throws NamingException
    {
        return searchForObjects( rdn, filter, filterArguments, null );
    }

    public List<T> searchForObjects( Rdn rdn, String filter, Object[] filterArguments, String[] attributesToReturn )
        throws NamingException
    {
        SearchControls searchControls = new SearchControls( SearchControls.SUBTREE_SCOPE, 0, 0, attributesToReturn, true, true );

        NamingEnumeration<SearchResult> e = search( rdn, filter, filterArguments, searchControls );

        List<T> objects = new ArrayList<T>();

        while( e.hasMore() )
        {
            objects.add( (T)e.next().getObject() );
        }

        return objects;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public LdapName makeName( Rdn organizationalUnit, Rdn objectRdn )
    {
        LdapName name = new LdapName( baseDnLdapName.getRdns() );

        if ( organizationalUnit != null )
        {
            name.add( organizationalUnit );
        }

        name.add( objectRdn );

        return name;
    }

    public Attribute getAttribute( Rdn rdn, String attributeName )
        throws NamingException
    {
        LdapConnection connection = connectionFactory.getConnection();

        try
        {
            LdapName name = new LdapName( baseDnLdapName.getRdns() );

            Attributes attributes = connection.getDirContext().getAttributes( name, new String[] { attributeName } );

            return attributes.get( attributeName );
        }
        finally
        {
            closeConnection( connection );
        }
    }

    public void replaceAttribute( Rdn rdn, Attribute attribute )
        throws NamingException
    {
        LdapConnection connection = connectionFactory.getConnection();

        try
        {
            LdapName name = new LdapName( baseDnLdapName.getRdns() );

            Attributes attributes = new BasicAttributes();

            attributes.put( attribute );

            connection.getDirContext().modifyAttributes( name, DirContext.REPLACE_ATTRIBUTE, attributes );
        }
        finally
        {
            closeConnection( connection );
        }
    }

    public void replaceAttribute( Rdn rdn, String attributeName, String newValue )
        throws NamingException
    {
        replaceAttribute( rdn, new BasicAttribute( attributeName, newValue ) );
    }

    public void addAttributeValue( Rdn rdn, String attributeName, String value )
        throws NamingException
    {
        throw new RuntimeException( "Not implemented." );
//        LdapConnection connection = connectionFactory.getConnection();
//
//        try
//        {
//            Attributes attributes = new BasicAttributes();
//
//            attributes.put( attributeName, newValue );
//
//            connection.getDirContext().modifyAttributes( objectName, DirContext.ADD_ATTRIBUTE, attributes );
//        }
//        finally
//        {
//            closeConnection( connection );
//        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void setHelper( Class clazz )
    {
        if ( AbstractLdapFactory.class.isAssignableFrom( clazz ) )
        {
            try
            {
                Method setHelper = clazz.getMethod( "setHelper", LdapFactoryHelper.class );
                setHelper.invoke( null, ldapFactoryHelper );
            }
            catch ( NoSuchMethodException e )
            {
                getLogger().warn( "Unable to set the Ldap Helper Factory in the AbstractLdapFactory.", e );
            }
            catch ( IllegalAccessException e )
            {
                getLogger().warn( "Unable to set the Ldap Helper Factory in the AbstractLdapFactory.", e );
            }
            catch ( InvocationTargetException e )
            {
                getLogger().warn( "Unable to set the Ldap Helper Factory in the AbstractLdapFactory.", e );
            }
        }
    }

    private NamingEnumeration<SearchResult> search( Rdn rdn, String filter, Object[] filterArguments, SearchControls searchControls )
        throws NamingException
    {
        LdapConnection connection = null;

        LdapName name = new LdapName( baseDnLdapName.getRdns() );

        name.add( rdn );

        try
        {
            connection = connectionFactory.getConnection();

            return connection.getDirContext().search( name, filter, filterArguments, searchControls );
        }
        finally
        {
            closeConnection( connection );
        }
    }

    private void closeConnection( LdapConnection connection )
    {
        if ( connection != null )
        {
            connection.close();
        }
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        try
        {
            baseDnLdapName = connectionFactory.getBaseDnLdapName();
        }
        catch ( LdapException e )
        {
            throw new InitializationException( "Error while initializing the connection factory.", e );
        }
    }
}
