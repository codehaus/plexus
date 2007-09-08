package org.codehaus.plexus.redback.xwork.action;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.plexus.redback.policy.PasswordEncoder;
import org.codehaus.plexus.redback.policy.PasswordRuleViolationException;
import org.codehaus.plexus.redback.policy.PasswordRuleViolations;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionBundle;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionException;
import org.codehaus.plexus.redback.xwork.util.AutoLoginCookies;
import org.codehaus.plexus.util.StringUtils;
import com.opensymphony.webwork.dispatcher.SessionMap;

/**
 * PasswordAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="redback-password"
 * instantiation-strategy="per-lookup"
 */
public class PasswordAction
    extends AbstractSecurityAction
    implements CancellableAction
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private String existingPassword;

    private String newPassword;

    private String newPasswordConfirm;

    private boolean provideExisting;

    /**
     * @plexus.requirement
     */
    private AutoLoginCookies autologinCookies;


    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        SecuritySession session = getSecuritySession();

        provideExisting = StringUtils.isNotEmpty( session.getUser().getEncodedPassword() );

        return INPUT;
    }

    public String submit()
    {
        SecuritySession session = getSecuritySession();

        provideExisting = StringUtils.isNotEmpty( session.getUser().getEncodedPassword() );

        if ( StringUtils.isEmpty( newPassword ) )
        {
            addFieldError( "newPassword", getText( "newPassword.cannot.be.empty" ) );
        }

        if ( !StringUtils.equals( newPassword, newPasswordConfirm ) )
        {
            addFieldError( "newPassword", getText( "password.confimation.failed" ) );
        }

        User user = session.getUser();

        // Test existing Password.
        PasswordEncoder encoder = securitySystem.getPolicy().getPasswordEncoder();

        if ( provideExisting )
        {
            if ( !encoder.isPasswordValid( user.getEncodedPassword(), existingPassword ) )
            {
                addFieldError( "existingPassword", getText( "password.provided.does.not.match.existing" ) );
            }
        }

        // Validate the Password.
        try
        {
            User tempUser = securitySystem.getUserManager().createUser( "temp", "temp", "temp" );
            tempUser.setPassword( newPassword );
            securitySystem.getPolicy().validatePassword( tempUser );
        }
        catch ( PasswordRuleViolationException e )
        {        	
            PasswordRuleViolations violations = e.getViolations();

            if ( violations != null )
            {
                Iterator it = violations.getLocalizedViolations().iterator();
                while ( it.hasNext() )
                {
                    String violation = (String) it.next();
                    addFieldError( "newPassword", violation );
                }
            }
        }

        // Toss error (if any exists)
        if ( hasActionErrors() || hasFieldErrors() || hasActionMessages() )
        {
            newPassword = "";
            newPasswordConfirm = "";
            existingPassword = "";
            return ERROR;
        }

        // We can save the new password.
        try
        {
            String encodedPassword = encoder.encodePassword( newPassword );
            user.setEncodedPassword( encodedPassword );
            user.setPassword( newPassword );
            // TODO: (address this) check once more for password policy, some policies may require additional information
            // only available in the actual user object, perhaps the thing to do is add a deep cloning mechanism
            // to user so we can validate this with a test user.  Its ok to just set and test it here before 
            // setting the updateUser, but logically its better to maintain a clear separation here
            securitySystem.getPolicy().validatePassword( user );
            securitySystem.getUserManager().updateUser( user );
        }
        catch ( UserNotFoundException e )
        {
            List list = new ArrayList();
            list.add( user.getUsername() );
            addActionError( getText( "cannot.update.user.not.found", list ) );
            addActionError( getText( "admin.deleted.account" ) );

            return ERROR;
        }
        catch ( PasswordRuleViolationException e )
        {        	
            PasswordRuleViolations violations = e.getViolations();

            if ( violations != null )
            {
                Iterator it = violations.getLocalizedViolations().iterator();
                while ( it.hasNext() )
                {
                    String violation = (String) it.next();
                    addFieldError( "newPassword", violation );
                }
            }
            return ERROR;
        }

        getLogger().info( "Password Change Request Success." );

        if ( !session.isAuthenticated() )
        {
            getLogger().debug( "User is not authenticated." );
            return REQUIRES_AUTHENTICATION;
        }

        /*
        *  If provide existing is true, then this was a normal password change flow, if it is
        * false then it is changing the password from the registration flow in which case direct to
         * external link
         */
        if ( !provideExisting )
        {                                              
            return RegisterAction.REGISTER_SUCCESS;
        }
        else
        {
            autologinCookies.removeRememberMeCookie();
            autologinCookies.removeSignonCookie();

            setAuthTokens( null );

            if ( super.session != null )
            {
                ( (SessionMap) super.session ).invalidate();
            }

            return SUCCESS;
        }
    }

    public String cancel()
    {
        return CANCEL;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getExistingPassword()
    {
        return existingPassword;
    }

    public void setExistingPassword( String existingPassword )
    {
        this.existingPassword = existingPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword( String newPassword )
    {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm()
    {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm( String newPasswordConfirm )
    {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public boolean isProvideExisting()
    {
        return provideExisting;
    }

    public void setProvideExisting( boolean provideExisting )
    {
        // Do nothing.
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        return SecureActionBundle.AUTHONLY;
    }
}
