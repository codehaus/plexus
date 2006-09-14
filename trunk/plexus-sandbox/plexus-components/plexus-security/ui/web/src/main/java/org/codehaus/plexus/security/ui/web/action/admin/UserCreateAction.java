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

import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.ui.web.model.UserCredentials;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * UserCreateAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-admin-user-create"
 *                   instantiation-strategy="per-lookup"
 */
public class UserCreateAction
    extends PlexusActionSupport
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private UserManager manager;

    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy securityPolicy;

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private UserCredentials user;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String input()
    {
        if ( user == null )
        {
            user = new UserCredentials();
        }

        return INPUT;
    }

    public String submit()
    {
        if ( user == null )
        {
            user = new UserCredentials();
            addActionError( "Invalid user credentials." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( user.getUsername() ) )
        {
            addFieldError( "user.username", "Username is required." );
        }

        if ( StringUtils.isEmpty( user.getFullName() ) )
        {
            addFieldError( "user.fullName", "Full name is required." );
        }

        if ( StringUtils.isEmpty( user.getEmail() ) )
        {
            addFieldError( "user.email", "Email address is required." );
        }

        if ( StringUtils.equals( user.getPassword(), user.getConfirmPassword() ) )
        {
            addFieldError( "user.password", "Password do not match." );
        }

        // NOTE: Do not perform Password Rules Validation Here.

        if ( manager.userExists( user.getUsername() ) )
        {
            // Means that the role name doesn't exist.
            // We need to fail fast and return to the previous page.
            addActionError( "User '" + user.getUsername() + "' already exists." );
        }

        if ( hasActionErrors() )
        {
            return ERROR;
        }

        User u = manager.createUser( user.getUsername(), user.getFullName(), user.getEmail() );
        u.setPassword( user.getPassword() );

        securityPolicy.setEnabled( false );
        manager.addUser( u );
        securityPolicy.setEnabled( true );

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
}
