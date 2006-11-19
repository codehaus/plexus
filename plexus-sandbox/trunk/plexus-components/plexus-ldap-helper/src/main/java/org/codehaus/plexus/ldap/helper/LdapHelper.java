package org.codehaus.plexus.ldap.helper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.DirStateFactory;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: LdapHelper.java,v 1.1 2006/02/11 18:31:32 trygvis Exp $
 */
public interface LdapHelper<T>
{
    String ROLE = LdapHelper.class.getName();

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    void addObjectFactory( Class<? extends DirObjectFactory> objectFactoryClass );

    void addStateFactory( Class<? extends DirStateFactory> stateFactoryClass );

    // ----------------------------------------------------------------------
    // Object Manipulation
    // ----------------------------------------------------------------------

    T lookupObject( LdapName objectName )
        throws NamingException;

    T lookupObject( Rdn organizationalUnit, Rdn objectRdn )
        throws NamingException;

    void bindObject( LdapName objectName, T object )
        throws NamingException;

    public void bindObject( Rdn organizationalUnit, Rdn rdn, Object object )
        throws NamingException;
    
    void rebindObject( LdapName objectName, T object )
        throws NamingException;

    void rebindObject( Rdn organizationalUnit, Rdn objectRdn, T object )
        throws NamingException;

    void unbindObject( LdapName objectName )
        throws NamingException;

    void unbindObject( Rdn organizationalUnit, Rdn objectRdn )
        throws NamingException;

    // ----------------------------------------------------------------------
    // Searching
    // ----------------------------------------------------------------------

    List<Attributes> searchForAttributes( Rdn rdn, String filter, Object[] filterArguments )
        throws NamingException;

    List<Attributes> searchForAttributes( Rdn rdn, String filter, Object[] filterArguments, String[] attributesToReturn )
        throws NamingException;

    List<T> searchForObjects( Rdn rdn, String filter, Object[] filterArguments  )
        throws NamingException;

    List<T> searchForObjects( Rdn rdn, String filter, Object[] filterArguments, String[] attributesToReturn )
        throws NamingException;

    // ----------------------------------------------------------------------
    // Utilities
    // ----------------------------------------------------------------------

    LdapName makeName( Rdn organizationalUnit, Rdn objectRdn )
        throws NamingException;

    Attribute getAttribute( Rdn rdn, String attributeName )
        throws NamingException;

    void replaceAttribute( Rdn rdn, Attribute attribute )
        throws NamingException;

    void replaceAttribute( Rdn rdn, String attributeName, String newValue )
        throws NamingException;

    void addAttributeValue( Rdn rdn, String attributeName, String value )
        throws NamingException;
}
