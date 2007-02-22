package org.codehaus.plexus.redback.system;

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
import org.codehaus.plexus.redback.authentication.AuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.AuthenticationException;
import org.codehaus.plexus.redback.authentication.AuthenticationManager;
import org.codehaus.plexus.redback.authentication.AuthenticationResult;
import org.codehaus.plexus.redback.authorization.AuthorizationDataSource;
import org.codehaus.plexus.redback.authorization.AuthorizationException;
import org.codehaus.plexus.redback.authorization.AuthorizationResult;
import org.codehaus.plexus.redback.authorization.Authorizer;
import org.codehaus.plexus.redback.keys.KeyManager;
import org.codehaus.plexus.redback.policy.AccountLockedException;
import org.codehaus.plexus.redback.policy.MustChangePasswordException;
import org.codehaus.plexus.redback.policy.UserSecurityPolicy;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.UserNotFoundException;

/**
 * DefaultSecuritySystem:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id$
 * @plexus.component role="org.codehaus.plexus.redback.system.SecuritySystem" role-hint="default"
 */
public class DefaultSecuritySystem
    extends AbstractLogEnabled
    implements SecuritySystem
{
    /**
     * @plexus.requirement
     */
    private AuthenticationManager authnManager;

    /**
     * @plexus.requirement
     */
    private Authorizer authorizer;

    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    /**
     * @plexus.requirement
     */
    private KeyManager keyManager;

    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy policy;

    // ----------------------------------------------------------------------------
    // Authentication: delegate to the authnManager
    // ----------------------------------------------------------------------------

    /**
     * delegate to the authentication system for boolean authentication checks,
     * if the result is authentic then pull the user object from the user
     * manager and add it to the session.  If the result is false return the result in
     * an authenticated session and a null user object.
     * <p/>
     * in the event of a successful authentication and a lack of corresponding user in the
     * usermanager return a null user as well
     * <p/>
     * //todo should this last case create a user in the usermanager?
     *
     * @param source
     * @return
     * @throws AuthenticationException
     * @throws UserNotFoundException
     * @throws MustChangePasswordException
     * @throws AccountLockedException
     */
    public SecuritySession authenticate( AuthenticationDataSource source )
        throws AuthenticationException, UserNotFoundException, AccountLockedException
    {
        // Perform Authentication.
        AuthenticationResult result = authnManager.authenticate( source );

        getLogger().debug( "authnManager.authenticate() result: " + result );

        // Process Results.
        if ( result.isAuthenticated() )
        {
            getLogger().debug( "User '" + result.getPrincipal() + "' authenticated." );
            if ( userManager.userExists( result.getPrincipal() ) )
            {
                getLogger().debug( "User '" + result.getPrincipal() + "' exists." );
                User user = userManager.findUser( result.getPrincipal() );
                getLogger().debug( "User: " + user );

                return new DefaultSecuritySession( result, user );
            }
            else
            {
                getLogger().debug( "User '" + result.getPrincipal() + "' DOES NOT exist." );
                return new DefaultSecuritySession( result );
            }
        }
        else
        {
            getLogger().debug( "User '" + result.getPrincipal() + "' IS NOT authenticated." );
            return new DefaultSecuritySession( result );
        }
    }

    public boolean isAuthenticated( AuthenticationDataSource source )
        throws AuthenticationException, UserNotFoundException, AccountLockedException
    {
        return authenticate( source ).getAuthenticationResult().isAuthenticated();
    }

    public String getAuthenticatorId()
    {
        if ( authnManager == null )
        {
            return "<null>";
        }
        return authnManager.getId();
    }

    // ----------------------------------------------------------------------------
    // Authorization: delegate to the authorizer
    // ----------------------------------------------------------------------------

    public AuthorizationResult authorize( SecuritySession session, Object permission )
        throws AuthorizationException
    {
        return authorize( session, permission, null );
    }

    public AuthorizationResult authorize( SecuritySession session, Object permission, Object resource )
        throws AuthorizationException
    {
        AuthorizationDataSource source = null;

        if ( session != null )
        {
            User user = session.getUser();
            if ( user != null )
            {
                source = new AuthorizationDataSource( user.getPrincipal(), user, permission, resource );
            }
        }

        if ( source == null )
        {
            source = new AuthorizationDataSource( null, null, permission, resource );
        }

        return authorizer.isAuthorized( source );
    }

    public boolean isAuthorized( SecuritySession session, Object permission )
        throws AuthorizationException
    {
        return isAuthorized( session, permission, null );
    }

    public boolean isAuthorized( SecuritySession session, Object permission, Object resource )
        throws AuthorizationException
    {
        return authorize( session, permission, resource ).isAuthorized();
    }

    public String getAuthorizerId()
    {
        if ( authorizer == null )
        {
            return "<null>";
        }
        return authorizer.getId();
    }

    // ----------------------------------------------------------------------------
    // User Management: delegate to the user manager
    // ----------------------------------------------------------------------------

    public UserManager getUserManager()
    {
        return userManager;
    }

    public String getUserManagementId()
    {
        if ( userManager == null )
        {
            return "<null>";
        }
        return userManager.getId();
    }

    public KeyManager getKeyManager()
    {
        return keyManager;
    }

    public String getKeyManagementId()
    {
        if ( keyManager == null )
        {
            return "<null>";
        }
        return keyManager.getId();
    }

    public UserSecurityPolicy getPolicy()
    {
        return policy;
    }

    public String getPolicyId()
    {
        if ( policy == null )
        {
            return "<null>";
        }
        return policy.getId();
    }

    public AuthenticationManager getAuthenticationManager()
    {
        return authnManager;
    }

    public Authorizer getAuthorizer()
    {
        return authorizer;
    }
}
