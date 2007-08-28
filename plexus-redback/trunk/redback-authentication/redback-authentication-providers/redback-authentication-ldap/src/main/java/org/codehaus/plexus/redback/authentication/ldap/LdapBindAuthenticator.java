package org.codehaus.plexus.redback.authentication.ldap;

/*
 * Copyright 2005 The Codehaus.
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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.authentication.AuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.AuthenticationException;
import org.codehaus.plexus.redback.authentication.AuthenticationResult;
import org.codehaus.plexus.redback.authentication.Authenticator;
import org.codehaus.plexus.redback.authentication.PasswordBasedAuthenticationDataSource;
import org.codehaus.plexus.redback.common.ldap.UserMapper;
import org.codehaus.plexus.redback.common.ldap.connection.LdapConnectionFactory;
import org.codehaus.plexus.redback.common.ldap.connection.LdapException;
import org.codehaus.plexus.redback.configuration.UserConfiguration;

/**
 * LdapBindAuthenticator:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.redback.authentication.Authenticator"
 *   role-hint="ldap"
 */
public class LdapBindAuthenticator extends AbstractLogEnabled
    implements Authenticator
{
    /**
     * @plexus.requirement role-hint="ldap"
     */
    private UserMapper mapper;
    
    /**
     * @plexus.requirement role-hint="configurable"
     */
    private LdapConnectionFactory connectionFactory;
    
    public String getId()
    {
        return "LdapBindAuthenticator";
    }

    public AuthenticationResult authenticate( AuthenticationDataSource s )
        throws AuthenticationException
    {
        PasswordBasedAuthenticationDataSource source = (PasswordBasedAuthenticationDataSource) s;

        SearchControls ctls = new SearchControls();

        ctls.setCountLimit( 1 );

        ctls.setDerefLinkFlag( true );
        ctls.setSearchScope( SearchControls.SUBTREE_SCOPE );

        String filter = "(&(objectClass=" + mapper.getUserObjectClass() + ")(" + mapper.getUserIdAttribute() + "=" +  source.getPrincipal() + "))";
        
        getLogger().info( "Searching for users with filter: \'" + filter + "\'" + " from base dn: " + mapper.getUserBaseDn() );

        try
        {
            DirContext context = connectionFactory.getConnection().getDirContext();
        
            NamingEnumeration<SearchResult> results = context.search( mapper.getUserBaseDn(), filter, ctls );
            
            getLogger().info( "Found user?: " + results.hasMoreElements() );
            
            if ( results.hasMoreElements() )
            {
                SearchResult result = results.nextElement();
                
                String userDn = result.getNameInNamespace();
                
                getLogger().info( "Attempting Authenication: + " + userDn );
                
                connectionFactory.getConnection( userDn, source.getPassword() );
                
                return new AuthenticationResult( true, source.getPrincipal(), null );
            }
            else
            {
                return new AuthenticationResult( false, source.getPrincipal(), null );
            }
        }
        catch ( LdapException e )
        {
            return new AuthenticationResult( false, source.getPrincipal(), e );
        }
        catch ( NamingException e )
        {
            return new AuthenticationResult( false, source.getPrincipal(), e );
        }
    }
    
    public boolean supportsDataSource( AuthenticationDataSource source )
    {
        return ( source instanceof PasswordBasedAuthenticationDataSource );
    }
}
