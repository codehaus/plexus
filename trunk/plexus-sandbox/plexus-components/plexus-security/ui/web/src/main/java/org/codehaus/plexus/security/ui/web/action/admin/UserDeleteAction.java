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
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

/**
 * UserDeleteAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-admin-user-delete"
 *                   instantiation-strategy="per-lookup"
 */
public class UserDeleteAction
    extends AbstractUserCredentialsAction
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    private String username;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String input()
    {
        if ( username == null )
        {
            addActionError( "Unable to delete user based on null username." );
            return SUCCESS;
        }

        return INPUT;
    }

    public String submit()
    {
        if ( username == null )
        {
            addActionError( "Invalid user credentials." );
            return SUCCESS;
        }

        if ( StringUtils.isEmpty( username ) )
        {
            addActionError( "Unable to delete user based on empty username." );
            return SUCCESS;
        }

        try
        {
            manager.deleteUser( username );
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to delete non-existant user '" + username + "'" );
        }

        return SUCCESS;
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

}
