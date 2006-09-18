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

import org.codehaus.plexus.security.ui.web.model.CreateUserCredentials;
import org.codehaus.plexus.security.user.User;

/**
 * RegisterAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-register"
 *                   instantiation-strategy="per-lookup"
 */
public class RegisterAction
    extends AbstractUserCredentialsAction
{
    private static final String REGISTER_SUCCESS = "security-register-success";
    private static final String REGISTER_CANCEL = "security-register-cancel";
    
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    private boolean cancelButton;
    
    private CreateUserCredentials user;
    
    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        if ( user == null )
        {
            user = new CreateUserCredentials();
        }

        return INPUT;
    }

    public String register()
    {
        if ( cancelButton )
        {
            return REGISTER_CANCEL;
        }
        
        if ( user == null )
        {
            user = new CreateUserCredentials();
            addActionError( "Invalid user credentials." );
            return ERROR;
        }
        
        internalUser = user;
        
        validateCredentialsStrict();

        // NOTE: Do not perform Password Rules Validation Here.

        if ( manager.userExists( user.getUsername() ) )
        {
            // Means that the role name doesn't exist.
            // We need to fail fast and return to the previous page.
            addActionError( "User '" + user.getUsername() + "' already exists." );
        }

        if ( hasActionErrors() || hasFieldErrors() )
        {
            return ERROR;
        }

        User u = manager.createUser( user.getUsername(), user.getFullName(), user.getEmail() );
        u.setPassword( user.getPassword() );

        manager.addUser( u );

        return REGISTER_SUCCESS;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public boolean isCancelButton()
    {
        return cancelButton;
    }

    public void setCancelButton( boolean cancelButton )
    {
        this.cancelButton = cancelButton;
    }

    public CreateUserCredentials getUser()
    {
        return user;
    }

    public void setUser( CreateUserCredentials user )
    {
        this.user = user;
    }
}
