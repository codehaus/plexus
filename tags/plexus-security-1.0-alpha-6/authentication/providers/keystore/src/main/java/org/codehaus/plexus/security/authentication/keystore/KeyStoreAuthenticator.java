package org.codehaus.plexus.security.authentication.keystore;

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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.Authenticator;
import org.codehaus.plexus.security.authentication.TokenBasedAuthenticationDataSource;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManager;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.keys.KeyNotFoundException;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;

/**
 * KeyStoreAuthenticator:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.authentication.Authenticator"
 *   role-hint="keystore"
 */
public class KeyStoreAuthenticator
    extends AbstractLogEnabled
    implements Authenticator
{
    /**
     * @plexus.requirement
     */
    private KeyManager keystore;
    
    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    public String getId()
    {
        return "$ID:$";
    }

    public AuthenticationResult authenticate( AuthenticationDataSource source )
        throws AccountLockedException, AuthenticationException
    {
        TokenBasedAuthenticationDataSource dataSource = (TokenBasedAuthenticationDataSource) source;

        String key = dataSource.getToken();
        try
        {
            AuthenticationKey authKey = keystore.findKey( key );

            // if we find a key (exception was probably thrown if not) then we should be authentic
            if ( authKey != null )
            {
                User user = userManager.findUser( dataSource.getPrincipal() );
                
                if (user.isLocked())
                {
                    throw new AccountLockedException( "Account " + source.getPrincipal() + " is locked.", user );
                }
                
                return new AuthenticationResult( true, dataSource.getPrincipal(), null );
            }
            else
            {
                return new AuthenticationResult( false, dataSource.getPrincipal(), new AuthenticationException("unable to find key") );
            }
        }
        catch ( KeyNotFoundException ne )
        {
            return new AuthenticationResult( false, null, ne );
        }
        catch ( KeyManagerException ke )
        {
            throw new AuthenticationException( "underlaying keymanager issue", ke );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Login for user " + source.getPrincipal() + " failed. user not found." );
            return new AuthenticationResult( false, null, e );
        }
    }

    public boolean supportsDataSource( AuthenticationDataSource source )
    {
        if ( source instanceof TokenBasedAuthenticationDataSource )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}