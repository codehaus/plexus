package org.codehaus.plexus.security.ui.web.action;

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

import org.codehaus.plexus.security.ui.web.model.EditUserCredentials;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

/**
 * AccountAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-account"
 *                   instantiation-strategy="per-lookup"
 */
public class AccountAction
    extends AbstractUserCredentialsAction
{
    private static final String ACCOUNT_SUCCESS = "security-account-success";
    private static final String ACCOUNT_CANCEL = "security-account-cancel";
    
    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private boolean cancelButton;
    
    private String username;
    
    private EditUserCredentials user;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
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
        
        internalUser = user;

        try
        {
            User u = manager.findUser( username );
            if ( u == null )
            {
                addActionError( "Unable to operate on null user." );
                return ERROR;
            }

            user = new EditUserCredentials( u );
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
        if ( cancelButton )
        {
            return ACCOUNT_CANCEL;
        }
        
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
        
        internalUser = user;

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

        return ACCOUNT_SUCCESS;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public EditUserCredentials getUser()
    {
        return user;
    }

    public void setUser( EditUserCredentials user )
    {
        this.user = user;
    }

    public boolean isCancelButton()
    {
        return cancelButton;
    }

    public void setCancelButton( boolean cancelButton )
    {
        this.cancelButton = cancelButton;
    }
}
