package org.codehaus.plexus.security.example.web.action;

import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.security.user.memory.SimpleUser;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;
/*
 * Copyright 2005 The Apache Software Foundation.
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

/**
 * PlexusSecuritySystemAction:
 *
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="session"
 */
public class SessionAction
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    private String username;

    private String password;

    private String email;

    public String login()
    {
        if ( username == null || password == null )
        {
            return ERROR;
        }

        UserManager um = securitySystem.getUserManager();

        User user = new SimpleUser( username, password, email );

        um.addUser( user );


        AuthenticationDataSource source = new AuthenticationDataSource( username, password );

        try
        {
            SecuritySession securitySession = securitySystem.authenticate( source );

            if ( securitySession.getAuthenticationResult().isAuthenticated() )
            {
                session.put( SecuritySession.ROLE, securitySession );
                session.put( "authStatus", "true" );
                return SUCCESS;
            }
            else
            {
                addActionError( "authentication failed" );
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
    }

    public String logout()
    {
        session.clear();

        return SUCCESS;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }
}
