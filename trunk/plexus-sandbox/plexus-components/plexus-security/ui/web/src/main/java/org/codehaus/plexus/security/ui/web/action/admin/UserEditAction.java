package org.codehaus.plexus.security.ui.web.action.admin;

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

import org.codehaus.plexus.security.ui.web.action.AbstractUserCredentialsAction;
import org.codehaus.plexus.security.ui.web.model.UserCredentials;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * UserEditAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-admin-user-edit"
 *                   instantiation-strategy="per-lookup"
 */
public class UserEditAction
    extends AbstractUserCredentialsAction
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    private String username;

    public String input()
    {
        if ( username == null )
        {
            addActionError( "Unable to edit user with null username." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( username ) )
        {
            addActionError( "Unable to edit user with empty username." );
            return ERROR;
        }

        if ( !manager.userExists( username ) )
        {
            // Means that the role name doesn't exist.
            // We need to fail fast and return to the previous page.
            addActionError( "User '" + username + "' does not exist." );
            return ERROR;
        }

        try
        {
            User u = manager.findUser( username );
            if ( u == null )
            {
                addActionError( "Unable to operate on null user." );
                return ERROR;
            }

            user = new UserCredentials();
            user.setUsername( username );
            user.setFullName( u.getFullName() );
            user.setEmail( u.getEmail() );
            user.setPassword( "" );
            user.setConfirmPassword( "" );
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to get User '" + username + "': " + e.getMessage() );
            return ERROR;
        }

        return INPUT;
    }

    public String submit()
    {
        if ( username == null )
        {
            addActionError( "Unable to edit user with null username." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( username ) )
        {
            addActionError( "Unable to edit user with empty username." );
            return ERROR;
        }

        if ( user == null )
        {
            addActionError( "Unable to edit user with null user credentials." );
            return ERROR;
        }

        if ( !manager.userExists( username ) )
        {
            // Means that the role name doesn't exist.
            // We need to fail fast and return to the previous page.
            addActionError( "User '" + username + "' does not exist." );
            return ERROR;
        }

        try
        {
            User u = manager.findUser( username );
            if ( u == null )
            {
                addActionError( "Unable to operate on null user." );
                return ERROR;
            }

            u.setFullName( user.getFullName() );
            u.setEmail( user.getEmail() );
            u.setPassword( user.getPassword() );

            manager.updateUser( u );
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to find User '" + username + "': " + e.getMessage() );
            return ERROR;
        }

        return SUCCESS;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public UserCredentials getUser()
    {
        return user;
    }

    public void setUser( UserCredentials user )
    {
        this.user = user;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }
}
