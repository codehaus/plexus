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
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;

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

    public SecuritySession authenticate( AuthenticationDataSource source )
        throws AuthenticationException, UserNotFoundException
    {
        AuthenticationResult result = authenticator.authenticate( source );

        User user = userManager.findUser( result.getPrincipal() );

        return new DefaultSecuritySession( result, user );
    }

    public boolean isAuthenticated( AuthenticationDataSource source )
        throws AuthenticationException, UserNotFoundException
    {
        return authenticate( source ).getAuthenticationResult().isAuthenticated();
    }

    public String getAuthenticatorId()
    {
        return "authenticator info 1.0";
    }

    // ----------------------------------------------------------------------------
    // Authorization: delegate to the authorizer
    // ----------------------------------------------------------------------------

    public AuthorizationResult authorize( SecuritySession session, Object permission )
        throws AuthorizationException
    {
        AuthorizationDataSource source = new AuthorizationDataSource( session.getUser().getPrincipal(), session.getUser(), permission );

        return authorizer.isAuthorized( source );
    }

    public boolean isAuthorized( SecuritySession session, Object permission )
        throws AuthorizationException
    {

        return authorize( session, permission).isAuthorized();
    }

    public AuthorizationResult authorize( SecuritySession session, Object permission, Object resource )
        throws AuthorizationException
    {
        AuthorizationDataSource source = new AuthorizationDataSource( session.getUser().getPrincipal(), session.getUser(), permission, resource );

        return authorizer.isAuthorized( source );
    }

    public boolean isAuthorized( SecuritySession session, Object permission, Object resource )
        throws AuthorizationException
    {
        return authorize( session, permission, resource).isAuthorized();
    }

    public String getAuthorizerId()
    {
        return "authorizer info 1.0";
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
        return "user management info 1.0";
    }
}
