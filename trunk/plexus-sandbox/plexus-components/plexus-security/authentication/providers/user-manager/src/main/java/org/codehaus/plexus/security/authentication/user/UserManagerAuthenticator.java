package org.codehaus.plexus.security.authentication.user;

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
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;

/**
 * {@link Authenticator} implementation that uses a wrapped {@link UserManager} to authenticate.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a> 
 * @version $Id$
 * @plexus.component
 *   role="org.codehaus.plexus.security.authentication.Authenticator"
 *   role-hint="user-manager"
 */
public class UserManagerAuthenticator
    extends AbstractLogEnabled
    implements Authenticator
{

    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    /**
     * @see org.codehaus.plexus.security.authentication.Authenticator#authenticate(org.codehaus.plexus.security.authentication.AuthenticationDataSource)
     */
    public AuthenticationResult authenticate( AuthenticationDataSource ds )
        throws AuthenticationException
    {

        try
        {
            User user = userManager.findUser( ds.getUsername() );
            boolean isPasswordValid = userManager.getUserSecurityPolicy().getPasswordEncoder()
                .isPasswordValid( user.getEncodedPassword(), ds.getPassword() );
            if ( isPasswordValid )
            {
                return new AuthenticationResult( true, ds.getUsername(), null );
            }
            return new AuthenticationResult( false, null, null );
        }
        catch ( UserNotFoundException e )
        {
            return new AuthenticationResult( false, null, e );
        }

    }

    /**
     * Returns the wrapped {@link UserManager} used by this {@link Authenticator} 
     * implementation for authentication.
     * @return the userManager
     */
    public UserManager getUserManager()
    {
        return userManager;
    }

    /**
     * Sets a {@link UserManager} to be used by this {@link Authenticator} 
     * implementation for authentication.
     * @param userManager the userManager to set
     */
    public void setUserManager( UserManager userManager )
    {
        this.userManager = userManager;
    }

}
