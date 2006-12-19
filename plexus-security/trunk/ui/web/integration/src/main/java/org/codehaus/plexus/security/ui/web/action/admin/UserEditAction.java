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

import org.codehaus.plexus.security.policy.PasswordRuleViolationException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.system.DefaultSecuritySession;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.model.AdminEditUserCredentials;
import org.codehaus.plexus.security.ui.web.role.profile.RoleConstants;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

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
    extends AbstractAdminUserCredentialsAction
{
    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private AdminEditUserCredentials user;

    private String cancelButton;

    private String updateButton;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String edit()
    {

        if ( isCancelButton() )
        {
           return "CANCEL";
        }

        if ( getUsername() == null )
        {
            addActionError( "Unable to edit user with null username." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( getUsername() ) )
        {
            addActionError( "Unable to edit user with empty username." );
            return ERROR;
        }

        UserManager manager = super.securitySystem.getUserManager();

        if ( !manager.userExists( getUsername() ) )
        {
            // Means that the role name doesn't exist.
            // We need to fail fast and return to the previous page.
            addActionError( "User '" + getUsername() + "' does not exist." );
            return ERROR;
        }

        try
        {
            User u = manager.findUser( getUsername() );

            if ( u == null )
            {
                addActionError( "Unable to operate on null user." );
                return ERROR;
            }

            user = new AdminEditUserCredentials( u );
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to get User '" + getUsername() + "': " + e.getMessage() );
            return ERROR;
        }

        return INPUT;
    }

    public String submit()
    {
        if ( getUsername() == null )
        {
            addActionError( "Unable to edit user with null username." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( getUsername() ) )
        {
            addActionError( "Unable to edit user with empty username." );
            return ERROR;
        }

        if ( user == null )
        {
            addActionError( "Unable to edit user with null user credentials." );
            return ERROR;
        }

        internalUser = user;

        validateCredentialsLoose();

        UserManager manager = super.securitySystem.getUserManager();

        if ( !manager.userExists( getUsername() ) )
        {
            // Means that the role name doesn't exist.
            // We need to fail fast and return to the previous page.
            addActionError( "User '" + getUsername() + "' does not exist." );
            return ERROR;
        }

        try
        {
            User u = manager.findUser( getUsername() );
            if ( u == null )
            {
                addActionError( "Unable to operate on null user." );
                return ERROR;
            }

            u.setFullName( user.getFullName() );
            u.setEmail( user.getEmail() );
            u.setPassword( user.getPassword() );
            u.setLocked( user.isLocked() );
            u.setPasswordChangeRequired( user.isPasswordChangeRequired() );

            manager.updateUser( u );

            //check if current user then update the session
            if ( getSecuritySession().getUser().getUsername().equals( u.getUsername() ) )
            {
                SecuritySession securitySession = new DefaultSecuritySession(
                    getSecuritySession().getAuthenticationResult(), u );

                session.put( SecuritySystemConstants.SECURITY_SESSION_KEY, securitySession );

                setSession( session );
            }
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to find User '" + getUsername() + "': " + e.getMessage() );
            return ERROR;
        }
        catch ( PasswordRuleViolationException pe )
        {
            processPasswordRuleViolations( pe );
            return ERROR;
        }

        return SUCCESS;
    }

    public boolean isCancelButton()
    {
    	return "Cancel".equals( cancelButton );
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------


    public String getCancelButton()
    {
        return cancelButton;
    }

    public void setCancelButton( String cancelButton )
    {
        this.cancelButton = cancelButton;
    }

    public String getUpdateButton()
    {
        return updateButton;
    }

    public void setUpdateButton( String updateButton )
    {
        this.updateButton = updateButton;
    }

    public AdminEditUserCredentials getUser()
    {
        return user;
    }

    public void setUser( AdminEditUserCredentials user )
    {
        this.user = user;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_USER_EDIT_OPERATION, Resource.GLOBAL );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_USER_EDIT_OPERATION, getUsername() );

        return bundle;
    }
}
