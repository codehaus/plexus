package org.codehaus.plexus.security.ui.web.action;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.policy.MustChangePasswordException;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * AbstractAuthenticationAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AbstractAuthenticationAction
    extends PlexusActionSupport
{
    static final String LOGIN_SUCCESS = "security-login-success";
    static final String LOGIN_CANCEL = "security-login-cancel";
    static final String PASSWORD_CHANGE = "must-change-password";
    static final String ACCOUNT_LOCKED = "security-login-locked";
    
    protected void setAuthTokens( SecuritySession securitySession )
    {
        session.put( SecuritySystemConstants.SECURITY_SESSION_KEY, securitySession );
        this.setSession( session );
    }

    protected String webLogin( SecuritySystem securitySystem, AuthenticationDataSource authdatasource )
    {
        // An attempt should log out your authentication tokens first!
        setAuthTokens( null );
    
        clearErrorsAndMessages();
    
        try
        {
            SecuritySession securitySession = securitySystem.authenticate( authdatasource );
    
            if ( securitySession.getAuthenticationResult().isAuthenticated() )
            {
                // Success!  Create tokens.
                setAuthTokens( securitySession );
                return LOGIN_SUCCESS;
            }
            else
            {
                getLogger().debug( "Login Action failed against principal : "
                                       + securitySession.getAuthenticationResult().getPrincipal(),
                                   securitySession.getAuthenticationResult().getException() );
                addActionError( "Authentication failed" );
                return ERROR;
            }
        }
        catch ( AuthenticationException ae )
        {
            addActionError( ae.getMessage() );
            return ERROR;
        }
        catch ( UserNotFoundException ue )
        {
            addActionError( ue.getMessage() );
            return ERROR;
        }
        catch ( AccountLockedException e )
        {
            addActionError( "Your Account is Locked." );
            return ACCOUNT_LOCKED;
        }
        catch ( MustChangePasswordException e )
        {
            addActionError( "You must change your password." );
            return PASSWORD_CHANGE;
        }
    }
}
