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
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.codehaus.plexus.ldap.helper.LdapConnectionFactory;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.authentication.AuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.AuthenticationException;
import org.codehaus.plexus.redback.authentication.AuthenticationResult;
import org.codehaus.plexus.redback.authentication.Authenticator;
import org.codehaus.plexus.redback.authentication.PasswordBasedAuthenticationDataSource;
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
     * @plexus.requirement role-hint="default"
     */
    private UserConfiguration config;
    
    public String getId()
    {
        return "LdapBindAuthenticator";
    }

    public AuthenticationResult authenticate( AuthenticationDataSource s )
        throws AuthenticationException
    {
        PasswordBasedAuthenticationDataSource source = (PasswordBasedAuthenticationDataSource) s;

        List baseDns = config.getString( "ldap.user.base.dn" );
        
        Hashtable env = new Hashtable();
        
        env.put( Context.INITIAL_CONTEXT_FACTORY, config.getString( "ldap.context.factory" ) );
        env.put( Context.SECURITY_AUTHENTICATION, config.getString( "ldap.context.authentiction.mechanism") );
        env.put( Context.PROVIDER_URL, config.getString( "ldap.context.provider.url" ) );
        env.put( Context.SECURITY_CREDENTIALS, source.getPassword() );
        
        for ( Iterator i = baseDns.iterator(); i.hasNext(); )
        {
            String baseDn = (String)i.next();
            
            env.put( Context.SECURITY_PRINCIPAL, getLdapPrincipal( source.getPrincipal(), baseDn ) );
            
            try
            {
                DirContext context = new InitialDirContext( env );
                
                return new AuthenticationResult( true, source.getPrincipal(), null );
            }
            catch ( NamingException e )
            {
                getLogger().debug( "Authentication failed to bind on: " + getLdapPrincipal( source.getPrincipal(), baseDn ) + " : " + e.getMessage() );
            }            
        }    
        
        return new AuthenticationResult( false, source.getPrincipal(), null );
    }

    private String getLdapPrincipal( String principal, String baseDn )
    {
        return config.getString( "ldap.user.attribute.userId" ) + "=" + principal + "," + baseDn;
    }
    
    public boolean supportsDataSource( AuthenticationDataSource source )
    {
        return ( source instanceof PasswordBasedAuthenticationDataSource );
    }
}
