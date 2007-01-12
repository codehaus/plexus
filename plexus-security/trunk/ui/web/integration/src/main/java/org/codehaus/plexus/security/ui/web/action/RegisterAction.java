package org.codehaus.plexus.security.ui.web.action;

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

import org.codehaus.plexus.rbac.profile.RoleProfileException;
import org.codehaus.plexus.rbac.profile.RoleProfileManager;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.mail.Mailer;
import org.codehaus.plexus.security.ui.web.model.CreateUserCredentials;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * RegisterAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="pss-register"
 * instantiation-strategy="per-lookup"
 */
public class RegisterAction
    extends AbstractUserCredentialsAction
    implements CancellableAction
{
    protected static final String REGISTER_SUCCESS = "security-register-success";

    private static final String VALIDATION_NOTE = "validation-note";

    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private Mailer mailer;

    /**
     * @plexus.requirement
     */
    private RBACManager rbacManager;

    /**
     * @plexus.requirement
     */
    private RoleProfileManager roleManager;

    private CreateUserCredentials user;

    private boolean emailValidationRequired;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        if ( user == null )
        {
            user = new CreateUserCredentials();
        }

        emailValidationRequired = securitySystem.getPolicy().getUserValidationSettings().isEmailValidationRequired();

        return INPUT;
    }

    public String register()
    {
        if ( user == null )
        {
            user = new CreateUserCredentials();
            addActionError( "Invalid user credentials." );
            return ERROR;
        }

        UserSecurityPolicy securityPolicy = securitySystem.getPolicy();

        emailValidationRequired = securityPolicy.getUserValidationSettings().isEmailValidationRequired();

        internalUser = user;

        if ( securityPolicy.getUserValidationSettings().isEmailValidationRequired() )
        {
            validateCredentialsLoose();
        }
        else
        {
            validateCredentialsStrict();
        }

        // NOTE: Do not perform Password Rules Validation Here.
        UserManager manager = super.securitySystem.getUserManager();

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
        u.setValidated( false );
        u.setLocked( false );

        try
        {
            // assign the base role for all users
            UserAssignment ua = rbacManager.createUserAssignment( u.getPrincipal().toString() );
            List roles = new ArrayList();
            roles.add( roleManager.getRole( "registered-user" ).getName() );
            ua.setRoleNames( roles );
            rbacManager.saveUserAssignment( ua );
        }
        catch ( RoleProfileException rpe )
        {
            addActionError( "Unable to assign core register user role to new user" );
            getLogger().error( "RoleProfile Error: " + rpe.getMessage(), rpe );
            return ERROR;
        }
        catch ( RbacManagerException e )
        {
            addActionError( "Unable to assign core register user role to new user" );
            getLogger().error( "System error:", e );
            return ERROR;
        }

        if ( securityPolicy.getUserValidationSettings().isEmailValidationRequired() )
        {
            u.setLocked( true );

            try
            {
                AuthenticationKey authkey = securitySystem.getKeyManager().createKey( u.getPrincipal().toString(),
                                                                                      "New User Email Validation",
                                                                                      securityPolicy.getUserValidationSettings().getEmailValidationTimeout() );

                List recipients = new ArrayList();
                recipients.add( u.getEmail() );

                mailer.sendAccountValidationEmail( recipients, authkey );

                securityPolicy.setEnabled( false );
                manager.addUser( u );

                return VALIDATION_NOTE;
            }
            catch ( KeyManagerException e )
            {
                addActionError( "Unable to process new user registration request." );
                getLogger().error( "Unable to register a new user.", e );
                return ERROR;
            }
            finally
            {
                securityPolicy.setEnabled( true );
            }
        }
        else
        {
            manager.addUser( u );
        }

        return REGISTER_SUCCESS;
    }
    
    public String cancel()
    {
		return CANCEL;
	}

	// ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public CreateUserCredentials getUser()
    {
        return user;
    }

    public void setUser( CreateUserCredentials user )
    {
        this.user = user;
    }

    public boolean isEmailValidationRequired()
    {
        return emailValidationRequired;
    }

    public void setEmailValidationRequired( boolean emailValidationRequired )
    {
        this.emailValidationRequired = emailValidationRequired;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        return SecureActionBundle.OPEN;
    }
}
