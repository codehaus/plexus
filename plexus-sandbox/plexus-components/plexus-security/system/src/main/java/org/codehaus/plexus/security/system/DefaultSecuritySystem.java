package org.codehaus.plexus.security.system;

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
import org.codehaus.plexus.security.authorization.AuthorizationException;
import org.codehaus.plexus.security.authorization.Authorizer;
import org.codehaus.plexus.security.authorization.AuthorizationDataSource;
import org.codehaus.plexus.security.authorization.AuthorizationResult;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.policy.MustChangePasswordException;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

/**
 * DefaultSecuritySystem:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.system.SecuritySystem"
 *   role-hint="default"
 *
 * @todo allow for multiple authentication providers i.e. using window and radius
 */
public class DefaultSecuritySystem
    extends AbstractLogEnabled
    implements SecuritySystem
{
    /**
     * @plexus.requirement
     */
    private Authenticator authenticator;

    /**
     * @plexus.requirement
     */
    private Authorizer authorizer;

    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    // ----------------------------------------------------------------------------
    // Authentication: delegate to the authenticator
    // ----------------------------------------------------------------------------

    /**
     * delegate to the authentication system for boolean authentication checks,
     * if the result is authentic then pull the user object from the user
     * manager and add it to the session.  If the result is false return the result in
     * an authenticated session and a null user object.
     *
     * in the event of a successful authentication and a lack of corresponding user in the
     * usermanager return a null user as well
     *
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
        throws AuthenticationException, UserNotFoundException, AccountLockedException, MustChangePasswordException
    {
        boolean hasDefaultPrincipal = StringUtils.isNotEmpty( source.getDefaultPrincipal() );
        
        // Empty Username ?
        if ( StringUtils.isEmpty( source.getUsername() ) )
        {
            AuthenticationResult result = new AuthenticationResult( hasDefaultPrincipal, source.getDefaultPrincipal(),
                                                                    null );
            return new DefaultSecuritySession( result, findDefaultUser( source ) );
        }
        
        // Perform Authentication.
        AuthenticationResult result = authenticator.authenticate( source );

        getLogger().debug( "authenticator.authenticate() result: " + result );

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
                return new DefaultSecuritySession( result, findDefaultUser( source ) );
            }
        }
        else
        {
            getLogger().debug( "User '" + result.getPrincipal() + "' IS NOT authenticated." );
            return new DefaultSecuritySession( result, findDefaultUser( source ) );
        }
    }
    
    private User findDefaultUser( AuthenticationDataSource source )
        throws UserNotFoundException
    {
        if ( StringUtils.isNotEmpty( source.getDefaultPrincipal() ) )
        {
            return userManager.findUser( source.getDefaultPrincipal() );
        }
        return null;
    }

    public boolean isAuthenticated( AuthenticationDataSource source )
        throws AuthenticationException, UserNotFoundException, AccountLockedException, MustChangePasswordException
    {
        return authenticate( source ).getAuthenticationResult().isAuthenticated();
    }

    public String getAuthenticatorId()
    {
        if ( authenticator == null )
        {
            return "<null>";
        }
        return authenticator.getId();
    }

    // ----------------------------------------------------------------------------
    // Authorization: delegate to the authorizer
    // ----------------------------------------------------------------------------

    public AuthorizationResult authorize( SecuritySession session, Object permission )
        throws AuthorizationException
    {
        AuthorizationDataSource source = new AuthorizationDataSource( session.getUser().getPrincipal(), session
            .getUser(), permission );

        return authorizer.isAuthorized( source );
    }

    public boolean isAuthorized( SecuritySession session, Object permission )
        throws AuthorizationException
    {

        return authorize( session, permission ).isAuthorized();
    }

    public AuthorizationResult authorize( SecuritySession session, Object permission, Object resource )
        throws AuthorizationException
    {
        AuthorizationDataSource source = new AuthorizationDataSource( session.getUser().getPrincipal(), session
            .getUser(), permission, resource );

        return authorizer.isAuthorized( source );
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
}
