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
import org.codehaus.plexus.redback.common.ldap.LdapUser;
import org.codehaus.plexus.redback.common.ldap.MappingException;
import org.codehaus.plexus.redback.common.ldap.UserMapper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
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
     * @plexus.requirement role-hint="ldap"
     */
    private UserMapper mapper;

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#removeUser(java.lang.Object, javax.naming.directory.DirContext)
	 */
    public void removeUser( Object principal, DirContext context )
        throws LdapControllerException
    {
   
    }

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#updateUser(org.codehaus.plexus.redback.users.User, javax.naming.directory.DirContext)
	 */
    public void updateUser( User user, DirContext context )
        throws LdapControllerException, MappingException
    {
    	
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

        ctls.setDerefLinkFlag( true );
        ctls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        ctls.setReturningAttributes( new String[] { "*" } );

        String filter = "(&(objectClass=" + mapper.getUserObjectClass() + ")(" + mapper.getUserIdAttribute() + "=" + ( key != null ? key : "*" ) + "))";
                
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
    	
    }

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#getUser(java.lang.Object, javax.naming.directory.DirContext)
	 */
    public LdapUser getUser( Object key, DirContext context )
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

    /* (non-Javadoc)
	 * @see org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerI#enableLogging(org.codehaus.plexus.logging.Logger)
	 */
    public void enableLogging( Logger logger )
    {
        log = logger;
    }
}
