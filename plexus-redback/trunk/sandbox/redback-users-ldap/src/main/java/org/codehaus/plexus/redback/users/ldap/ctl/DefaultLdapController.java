package org.codehaus.plexus.redback.users.ldap.ctl;

import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.ldap.BasicUser;
import org.codehaus.plexus.redback.users.ldap.mapping.MappingException;
import org.codehaus.plexus.redback.users.ldap.mapping.UserMapper;
import org.codehaus.plexus.redback.users.ldap.mapping.UserUpdate;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * 
 * @author <a href="jesse@codehaus.org"> jesse
 * @version "$Id$"
 *
 * @plexus.component role="org.codehaus.plexus.redback.users.ldap.ctl.LdapController" role-hint="default"
 */
public class DefaultLdapController
    implements LogEnabled, LdapController
{

    private Logger log;

    /**
     * @plexus.requirement role-hint="basic"
     */
    private UserMapper mapper;

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#removeUser(java.lang.Object, javax.naming.directory.DirContext)
	 */
    public void removeUser( Object principal, DirContext context )
        throws LdapControllerException
    {
        log.info( "Searching for user: " + principal );

        try
        {
            context = (DirContext) context.lookup( mapper.getUserBaseDn() );

            context.destroySubcontext( mapper.getUserIdAttribute() + "=" + principal );
        }
        catch ( NamingException e )
        {
            String message = "Failed to remove user: " + principal;

            throw new LdapControllerException( message, e );
        }
    }

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#updateUser(org.codehaus.plexus.redback.users.User, javax.naming.directory.DirContext)
	 */
    public void updateUser( User user, DirContext context )
        throws LdapControllerException, MappingException
    {
        BasicUser inLdap = getUser( user.getPrincipal(), context );

        String userIdAttribute = mapper.getUserIdAttribute();
        String userBaseDn = mapper.getUserBaseDn();

        DirContext userContext;

        try
        {
            userContext = (DirContext) context.lookup( userBaseDn );
        }
        catch ( NamingException e )
        {
            String message = "Failed to find user parent-context for: " + user.getPrincipal();

            throw new LdapControllerException( message, e );
        }

        UserUpdate update = mapper.getUpdate( inLdap );

        if ( update.hasAdditions() )
        {
            try
            {
                userContext.modifyAttributes( userIdAttribute + "=" + user.getPrincipal(), DirContext.ADD_ATTRIBUTE, update.getAddedAttributes() );
            }
            catch ( NamingException e )
            {
                String message = "Failed to update user: " + user.getUsername();

                throw new LdapControllerException( message, e );
            }
        }

        if ( update.hasModifications() )
        {
            try
            {
                userContext.modifyAttributes( userIdAttribute + "=" + user.getPrincipal(), DirContext.REPLACE_ATTRIBUTE, update.getModifiedAttributes() );
            }
            catch ( NamingException e )
            {
                String message = "Failed to update user: " + user.getUsername();

                throw new LdapControllerException( message, e );
            }
        }

    }

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#userExists(java.lang.Object, javax.naming.directory.DirContext)
	 */
    public boolean userExists( Object key, DirContext context )
        throws LdapControllerException
    {
        try
        {
            return searchUsers( key, context ).hasMoreElements();
        }
        catch ( NamingException e )
        {
            throw new LdapControllerException( "Error searching for the existence of user: " + key, e );
        }
    }

    protected NamingEnumeration<SearchResult> searchUsers( Object key, DirContext context )
        throws NamingException
    {
        return searchUsers( key, context, null );
    }

    protected NamingEnumeration<SearchResult> searchUsers( DirContext context )
        throws NamingException
    {
        return searchUsers( null, context, null );
    }

    protected NamingEnumeration<SearchResult> searchUsers( DirContext context, String[] returnAttributes )
        throws NamingException
    {
        return searchUsers( null, context, returnAttributes );
    }

    protected NamingEnumeration<SearchResult> searchUsers( Object key, DirContext context, String[] returnAttributes )
        throws NamingException
    {
        String[] userAttributes = returnAttributes;
        if ( userAttributes == null )
        {
            userAttributes = mapper.getUserAttributeNames();
        }

        SearchControls ctls = new SearchControls();

        if ( key != null )
        {
            ctls.setCountLimit( 0 );
        }

        //ctls.setReturningObjFlag(false);
        ctls.setDerefLinkFlag( true );
        ctls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        ctls.setReturningAttributes( new String[] { "*" } );

        //String filter = "(&(objectClass=" + mapper.getUserObjectClass() + ")(" + mapper.getUserIdAttribute() + "=" + ( key != null ? key : "*" ) + "))";
        String filter = "(" + mapper.getUserIdAttribute() + "=" + ( key != null ? key : "*" ) + ")";
        //String filter = "(objectClass=" + mapper.getUserObjectClass() + ")";
        
        
        log.info( "Searching for users with filter: \'" + filter + "\'" + " from base dn: " + mapper.getUserBaseDn());
        
        return context.search( mapper.getUserBaseDn(), filter, ctls );
    }

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#getUsers(javax.naming.directory.DirContext)
	 */
    public Collection<User> getUsers( DirContext context )
        throws LdapControllerException, MappingException
    {
        try
        {
            NamingEnumeration<SearchResult> results = searchUsers( null, context, null );
            Set<User> users = new LinkedHashSet<User>();
            
            while ( results.hasMoreElements() )
            {
                SearchResult result = results.nextElement();
                users.add( mapper.getUser( result.getAttributes() ) );
            }
            
            return users;
        }
        catch ( NamingException e )
        {
            String message = "Failed to retrieve ldap information for users.";

            throw new LdapControllerException( message, e );
        }
    }

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#createUser(org.codehaus.plexus.redback.users.User, javax.naming.directory.DirContext, boolean)
	 */
    public void createUser( User user, DirContext context, boolean encodePasswordIfChanged )
        throws LdapControllerException, MappingException
    {
        String userIdAttribute = mapper.getUserIdAttribute();
        String userBaseDn = mapper.getUserBaseDn();

        try
        {
            NamingEnumeration<SearchResult> existing = searchUsers( user.getPrincipal(), context, new String[] { userIdAttribute } );

            if ( existing.hasMoreElements() )
            {
                throw new LdapControllerException( "User: " + user.getUsername() + " already exists!" );
            }
        }
        catch ( NamingException e )
        {
            String message = "Error while checking for existing user: " + user.getUsername();

            throw new LdapControllerException( message, e );
        }

        DirContext userContext;

        try
        {
            userContext = (DirContext) context.lookup( userBaseDn );
        }
        catch ( NamingException e )
        {
            String message = "Failed to create user for: " + user.getUsername();

            throw new LdapControllerException( message, e );
        }

        Attributes userAttrs = mapper.getCreationAttributes( user, encodePasswordIfChanged );

        try
        {
            userContext.createSubcontext( userIdAttribute + "=" + user.getPrincipal(), userAttrs );
        }
        catch ( NamingException e )
        {
            String message = "Failed to create user for: " + user.getUsername();

            throw new LdapControllerException( message, e );
        }
    }

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#getUser(java.lang.Object, javax.naming.directory.DirContext)
	 */
    public BasicUser getUser( Object key, DirContext context )
        throws LdapControllerException, MappingException
    {
        String username = key.toString();

        log.info( "Searching for user: " + username );

        try
        {
            NamingEnumeration<SearchResult> result = searchUsers( username, context, null );

            if ( result.hasMoreElements() )
            {
                SearchResult next = result.nextElement();
                Attributes attrs = next.getAttributes();
                System.out.println( "ATTRIBUTES: + " + AttributeUtils.toString( attrs ) );
                System.out.println( "ATTR: " + attrs.toString() + "\n\nName: "+ next.getNameInNamespace() );
                Object o = context.lookup( next.getNameInNamespace() );                
                Attributes attributes = (Attributes) o;
                System.out.println( "ATTR2: " + attributes.toString() );
                return mapper.getUser( attributes );
            }
            else
            {
                return null;
            }
        }
        catch ( NamingException e )
        {
            String message = "Failed to retrieve information for user: " + username;

            throw new LdapControllerException( message, e );
        }
    }

//    private User createUser( SearchResult result )
//        throws LdapControllerException
//    {
//        Attributes attributes = result.getAttributes();
//
//        User user = new User();
//
//        String userIdAttribute = configuration.mapper.getUserIdAttribute();
//        String emailAddressAttribute = configuration.getEmailAddressAttribute();
//        String nameAttribute = configuration.getUserRealNameAttribute();
//        String websiteAttribute = configuration.getWebsiteAttribute();
//        String websiteUriLabel = configuration.getWebsiteUriLabel();
//        String passwordAttribute = configuration.getPasswordAttribute();
//
//        user.setUsername( LdapUtils.getAttributeValue( attributes, userIdAttribute, "username" ) );
//        user.setEmail( LdapUtils.getAttributeValue( attributes, emailAddressAttribute, "email address" ) );
//        user.setRealName( LdapUtils.getAttributeValue( attributes, nameAttribute, "name" ) );
//        user.setPassword( LdapUtils.getAttributeValueFromByteArray( attributes, passwordAttribute, "password" ) );
//
//        if ( configuration.isWebsiteAttributeLabelUri() )
//        {
//            user.setWebsite( LdapUtils.getLabeledUriValue( attributes, websiteAttribute, websiteUriLabel, "website" ) );
//        }
//        else
//        {
//            user.setWebsite( LdapUtils.getAttributeValue( attributes, websiteAttribute, "website" ) );
//        }
//
//        return user;
//    }

//    public PasswordEncoder getPasswordEncoder()
//    {
//        return passwordEncoder;
//    }
//
//    public void setPasswordEncoder( PasswordEncoder passwordEncoder )
//    {
//        this.passwordEncoder = passwordEncoder;
//    }


    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#enableLogging(org.codehaus.plexus.logging.Logger)
	 */
    public void enableLogging( Logger logger )
    {
        log = logger;
    }
}
