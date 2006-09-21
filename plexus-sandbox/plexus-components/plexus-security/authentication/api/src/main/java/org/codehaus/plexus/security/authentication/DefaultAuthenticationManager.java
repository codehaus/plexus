package org.codehaus.plexus.security.authentication;

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

import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.policy.MustChangePasswordException;

import java.util.List;
import java.util.Iterator;


/**
 * DefaultAuthenticationManager: the goal of the authentication manager is to act as a conduit for
 * authentication requests into different authentication schemes
 *
 * For example, the default implementation can be configured with any number of authenticators and will
 * sequentially try them for an authenticated result.  This allows you to have the standard user/pass
 * auth procedure followed by authentication based on a known key for 'remember me' type functionality.
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.authentication.AuthenticationManager"
 *   role-hint="default"
 */
public class DefaultAuthenticationManager
    implements AuthenticationManager
{
    /**
     * @plexus.requirement role="org.codehaus.plexus.security.authentication.Authenticator"
     */
    private List authenticators;

    public String getId()
    {
        return this.getClass().getName() + "\n" + knownAuthenticators();
    }

    public AuthenticationResult authenticate( AuthenticationDataSource source )
        throws AccountLockedException, MustChangePasswordException, AuthenticationException
    {
        if ( authenticators == null || authenticators.size() == 0 )
        {
            return( new AuthenticationResult( false, null, new AuthenticationException("no valid authenticators, can't authenticate") ) );                    
        }

        for( Iterator i = authenticators.iterator(); i.hasNext(); )
        {
            Authenticator authenticator = (Authenticator) i.next();

            if ( authenticator.supportsDataSource( source ) )
            {
                AuthenticationResult authResult = authenticator.authenticate( source );

                if ( authResult.isAuthenticated() )
                {
                    return authResult;
                }
            }
        }

        return( new AuthenticationResult( false, null, new AuthenticationException("authentication failed on authenticators: " + knownAuthenticators() ) ) );
    }


    public List getAuthenticators()
    {
        return authenticators;
    }

    private String knownAuthenticators()
    {
        StringBuffer strbuf = new StringBuffer();

        strbuf.append("[");
        for( Iterator i = authenticators.iterator(); i.hasNext(); )
        {
            Authenticator authenticator = (Authenticator) i.next();

            strbuf.append( authenticator.getClass().getName() );
        }
        strbuf.append("]");

        return strbuf.toString();
    }
}