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

import org.codehaus.plexus.security.authentication.PasswordBasedAuthenticationDataSource;
import org.codehaus.plexus.security.authentication.TokenBasedAuthenticationDataSource;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.keys.KeyNotFoundException;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

/**
 * LoginAction
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-login"
 *                   instantiation-strategy="per-lookup"
 */
public class LoginAction
    extends AbstractAuthenticationAction
{
    
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;
    
    private String username;

    private String password;
    
    private boolean cancelButton;
    
    private String validateMe;
    
    private String resetPassword;
    
    private boolean rememberMe;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------
    
    public String show()   
    {
        return INPUT;
    }
    
    public String login()
    {
        if ( cancelButton )
        {
            return LOGIN_CANCEL;
        }
        
        if ( StringUtils.isNotEmpty( validateMe ))
        {
            // Process a login / validate request.
            return validated();
        }
        
        if ( StringUtils.isNotEmpty(resetPassword))
        {
            // Process a login / reset password request.
            return resetPassword();
        }
        
        if ( StringUtils.isEmpty(username)  )
        {
            addFieldError( "username", "Username cannot be empty." );
            return ERROR;
        }
        
        PasswordBasedAuthenticationDataSource authdatasource = new PasswordBasedAuthenticationDataSource();
        authdatasource.setPrincipal( username );
        authdatasource.setPassword( password );
        
        return webLogin( securitySystem, authdatasource, rememberMe );
    }
    
    public String resetPassword()
    {
        if ( StringUtils.isEmpty( resetPassword ) )
        {
            addActionError( "Reset Password key missing." );
            return ERROR;
        }

        try
        {
            AuthenticationKey authkey = securitySystem.getKeyManager().findKey( validateMe );

            User user = securitySystem.getUserManager().findUser( authkey.getForPrincipal() );
            
            user.setPasswordChangeRequired( true );
            user.setEncodedPassword( "" );
            
            TokenBasedAuthenticationDataSource authsource = new TokenBasedAuthenticationDataSource( );
            authsource.setPrincipal( user.getPrincipal().toString() );
            authsource.setToken( authkey.getKey() );
            
            securitySystem.getUserManager().updateUser( user );
            
            return webLogin( securitySystem, authsource, false );
        }
        catch ( KeyNotFoundException e )
        {
            addActionError( "Unable to find the key." );
            return ERROR;
        }
        catch ( KeyManagerException e )
        {
            addActionError( "Unable to process key at this time.  Please try again later." );
            getLogger().warn( "Key Manager error: ", e );
            return ERROR;
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to find user." );
            return ERROR;
        }
    }
    
    public String validated()
    {
        if ( StringUtils.isEmpty( validateMe ) )
        {
            addActionError( "Validation failure key missing." );
            return ERROR;
        }

        try
        {
            AuthenticationKey authkey = securitySystem.getKeyManager().findKey( validateMe );

            User user = securitySystem.getUserManager().findUser( authkey.getForPrincipal() );
            
            user.setValidated( true );
            user.setLocked( false );
            user.setPasswordChangeRequired( true );
            user.setEncodedPassword( "" );
            
            TokenBasedAuthenticationDataSource authsource = new TokenBasedAuthenticationDataSource( );
            authsource.setPrincipal( user.getPrincipal().toString() );
            authsource.setToken( authkey.getKey() );
            
            securitySystem.getUserManager().updateUser( user );
            
            return webLogin( securitySystem, authsource, false );
        }
        catch ( KeyNotFoundException e )
        {
            addActionError( "Unable to find the key." );
            return ERROR;
        }
        catch ( KeyManagerException e )
        {
            addActionError( "Unable to process key at this time.  Please try again later." );
            return ERROR;
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to find user." );
            return ERROR;
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

    public boolean isCancelButton()
    {
        return cancelButton;
    }

    public void setCancelButton( boolean cancelButton )
    {
        this.cancelButton = cancelButton;
    }

    public String getValidateMe()
    {
        return validateMe;
    }

    public void setValidateMe( String validateMe )
    {
        this.validateMe = validateMe;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        return SecureActionBundle.OPEN;
    }

    public String getResetPassword()
    {
        return resetPassword;
    }

    public void setResetPassword( String resetPassword )
    {
        this.resetPassword = resetPassword;
    }

    public boolean isRememberMe()
    {
        return rememberMe;
    }

    public void setRememberMe( boolean rememberMe )
    {
        this.rememberMe = rememberMe;
    }
}
