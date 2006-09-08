package org.codehaus.plexus.security.ui.web.filter.authentication;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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


import com.opensymphony.xwork.ActionContext;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;

/**
 * AbstractHttpAuthentication 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AbstractHttpAuthentication extends AbstractLogEnabled
{
    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;
    
    protected boolean isAlreadyAuthenticated()
    {
        ActionContext context = ActionContext.getContext();
        SecuritySession securitySession = (SecuritySession) context.getSession().get( SecuritySession.ROLE );
        User user = securitySession.getUser();

        // TODO: Need more checks.   Session?  .isAuthenticated()?  

        return ( user != null );
    }
    
    protected void assertValidUser( String username, String password )
        throws HttpAuthenticationException
    {
        try
        {
            SecuritySession securitySession = securitySystem.authenticate( new AuthenticationDataSource( username,
                                                                                                         password ) );
            if ( securitySession.getAuthenticationResult().isAuthenticated() == false )
            {
                throw new HttpAuthenticationException( "You are not authenticated." );
            }
        }
        catch ( AuthenticationException e )
        {
            String msg = "Unable to authenticate user '" + username + "'";
            getLogger().info( msg, e );
            throw new HttpAuthenticationException( msg, e );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().info( "Login attempt against unknown user '" + username + "'." );
            throw new HttpAuthenticationException( "User name or password invalid." );
        }
    }
}
