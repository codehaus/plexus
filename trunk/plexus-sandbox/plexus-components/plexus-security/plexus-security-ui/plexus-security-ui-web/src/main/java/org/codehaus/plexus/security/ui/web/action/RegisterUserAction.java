package org.codehaus.plexus.security.ui.web.action;

import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
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
 * SessionAction:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="registerUser"
 */
public class RegisterUserAction
    extends PlexusActionSupport
{

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    private String username;

    private String password;

    private String email;

    private String fullName;


    public String createUser()
    {
        if ( username != null && fullName != null && password != null && email != null )
        {

            UserManager um = securitySystem.getUserManager();

            User user = um.createUser( username, fullName, email );

            user.setPassword( password );

            um.updateUser( user );

            return SUCCESS;
        }
        else
        {
            return INPUT;
        }
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

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }
}
