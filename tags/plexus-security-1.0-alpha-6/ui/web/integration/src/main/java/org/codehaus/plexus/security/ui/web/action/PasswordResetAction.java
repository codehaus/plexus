package org.codehaus.plexus.security.ui.web.action;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManager;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.mail.Mailer;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * PasswordResetAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-password-reset"
 *                   instantiation-strategy="per-lookup"
 */
public class PasswordResetAction
    extends AbstractSecurityAction
{
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
    private SecuritySystem securitySystem;

    private String username;

    private String cancelbutton;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        return INPUT;
    }

    public String reset()
    {
        if ( isCancelButton() )
        {
            return NONE;
        }

        if ( StringUtils.isEmpty( username ) )
        {
            addFieldError( "username", "Username cannot be empty." );
            return INPUT;
        }

        UserManager userManager = securitySystem.getUserManager();
        KeyManager keyManager = securitySystem.getKeyManager();
        UserSecurityPolicy policy = securitySystem.getPolicy();

        try
        {
            User user = userManager.findUser( username );

            AuthenticationKey authkey = keyManager.createKey( username, "Password Reset Request", policy
                .getUserValidationSettings().getEmailValidationTimeout() );

            List recipients = new ArrayList();
            recipients.add( user.getEmail() );

            mailer.sendAccountValidationEmail( recipients, authkey );

            addActionMessage( "Password reset email has been sent." );
        }
        catch ( UserNotFoundException e )
        {
            // Intentionally misdirect user.
            // This is done to prevent a malicious user from attempting to ascertain the
            // validity of usernames.
            addActionMessage( "Password reset email has been sent." );

            getLogger().info( "Password Reset on non-existant user [" + username + "]." );
        }
        catch ( KeyManagerException e )
        {
            addActionError( "Internal system error prevented generation of Password reset email." );
            getLogger().info( "Unable to issue password reset.", e );
        }

        return INPUT;
    }

    // ------------------------------------------------------------------
    // Security Specification
    // ------------------------------------------------------------------

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        return SecureActionBundle.OPEN;
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

   public boolean isCancelButton()
   {
       return "Cancel".equals( cancelbutton );
   }

   public void setCancelbutton( String cancelbutton )
   {
       this.cancelbutton = cancelbutton;
   }

}
