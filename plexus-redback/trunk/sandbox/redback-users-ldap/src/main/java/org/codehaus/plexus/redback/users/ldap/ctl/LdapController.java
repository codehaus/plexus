package org.codehaus.plexus.redback.users.ldap.ctl;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

public class LdapController
    implements LogEnabled
{

    private Logger log;

    private UserMapper mapper;

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
            ctls.setCountLimit( 1 );
        }

        ctls.setDerefLinkFlag( true );
        ctls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        ctls.setReturningAttributes( userAttributes );

        String filter = "(&(objectClass=" + mapper.getUserObjectClass() + ")(" + mapper.getUserIdAttribute() + "=" + ( key != null ? key : "*" ) + "))";

        log.info( "Searching for users with filter: \'" + filter + "\'" );

        return context.search( mapper.getUserBaseDn(), filter, ctls );
    }

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
                return mapper.getUser( next.getAttributes() );
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


    public void enableLogging( Logger logger )
    {
        log = logger;
    }
}
