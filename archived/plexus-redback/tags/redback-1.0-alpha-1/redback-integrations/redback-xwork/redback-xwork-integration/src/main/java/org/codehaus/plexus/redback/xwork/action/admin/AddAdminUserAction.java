package org.codehaus.plexus.redback.xwork.action.admin;

/*
 * Copyright 2005-2006 The Codehaus.
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

import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.role.RoleManager;
import org.codehaus.plexus.redback.role.RoleManagerException;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionBundle;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionException;
import org.codehaus.plexus.redback.xwork.model.EditUserCredentials;
import org.codehaus.plexus.redback.xwork.role.RoleConstants;

/**
 * AddAdminUserAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id: AddAdminUserAction.java 448077 2006-09-20 05:42:22Z joakime $
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="redback-admin-account"
 * instantiation-strategy="per-lookup"
 */
public class AddAdminUserAction
    extends AbstractAdminUserCredentialsAction
{
    /**
     * @plexus.requirement
     */
    private RoleManager roleManager;

    /**
     * @plexus.requirement role-hint="cached"
     */
    private RBACManager rbacManager;

    private EditUserCredentials user;

    public String show()
    {
        if ( user == null )
        {
            user = new EditUserCredentials( RoleConstants.ADMINISTRATOR_ACCOUNT_NAME );
        }

        return INPUT;
    }

    public String submit()
    {
        if ( user == null )
        {
            user = new EditUserCredentials( RoleConstants.ADMINISTRATOR_ACCOUNT_NAME );
            addActionError( "Invalid admin credentials, try again." );
            return ERROR;
        }

        getLogger().info( "user = " + user );

        internalUser = user;

        validateCredentialsStrict();

        UserManager userManager = super.securitySystem.getUserManager();

        if ( userManager.userExists( RoleConstants.ADMINISTRATOR_ACCOUNT_NAME ) )
        {
            // Means that the role name exist already.
            // We need to fail fast and return to the previous page.
            addActionError( "Admin User exists in database (someone else probably created the user before you)." );
            return ERROR;
        }

        if ( hasActionErrors() || hasFieldErrors() )
        {
            return ERROR;
        }

        User u =
            userManager.createUser( RoleConstants.ADMINISTRATOR_ACCOUNT_NAME, user.getFullName(), user.getEmail() );
        if ( u == null )
        {
            addActionError( "Unable to operate on null user." );
            return ERROR;
        }

        u.setPassword( user.getPassword() );
        u.setLocked( false );
        u.setPasswordChangeRequired( false );
        u.setPermanent( true );

        userManager.addUser( u );

        try
        {
            roleManager.assignRole( "system-administrator", u.getPrincipal().toString() );
        }
        catch ( RoleManagerException rpe )
        {
            addActionError( "Unable to assign system administrator role" );
            return ERROR;
        }

        return "security-admin-user-created";
    }

    public EditUserCredentials getUser()
    {
        return user;
    }

    public void setUser( EditUserCredentials user )
    {
        this.user = user;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        return SecureActionBundle.OPEN;
    }
}