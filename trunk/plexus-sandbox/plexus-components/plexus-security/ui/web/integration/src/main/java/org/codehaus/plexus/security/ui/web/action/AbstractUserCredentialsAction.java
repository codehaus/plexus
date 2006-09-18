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

import org.codehaus.plexus.security.policy.PasswordRuleViolationException;
import org.codehaus.plexus.security.policy.PasswordRuleViolations;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.ui.web.model.UserCredentials;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.Iterator;

/**
 * AbstractUserCredentialsAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractUserCredentialsAction
    extends PlexusActionSupport
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------
    
    /**
     * @plexus.requirement
     */
    protected UserManager manager;

    /**
     * @plexus.requirement
     */
    protected UserSecurityPolicy securityPolicy;
    
    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    protected UserCredentials user;
    
    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public void validateCredentialsLoose()
    {
        System.out.println("user.username        = [" + user.getUsername() + "]" );
        System.out.println("user.fullName        = [" + user.getFullName() + "]" );
        System.out.println("user.email           = [" + user.getEmail() + "]" );
        System.out.println("user.password        = [" + user.getPassword() + "]" );
        System.out.println("user.confirmPassword = [" + user.getConfirmPassword() + "]" );
        
        if ( StringUtils.isEmpty( user.getUsername() ) )
        {
            addFieldError( "user.username", "User Name is required." );
        }

        if ( StringUtils.isEmpty( user.getFullName() ) )
        {
            addFieldError( "user.fullname", "Full Name is required." );
        }

        if ( StringUtils.isEmpty( user.getEmail() ) )
        {
            addFieldError( "user.email", "Email Address is required." );
        }

        if ( !StringUtils.equals( user.getPassword(), user.getConfirmPassword() ) )
        {
            addFieldError( "user.confirmPassword", "Passwords do not match." );
        }
    }

    public void validateCredentialsStrict()
    {
        validateCredentialsLoose();

        // TODO: Figure out email validation.
        // EmailValidator emailvalidator = new EmailValidator();
        // emailvalidator.setFieldName( "user.email" );
        // emailvalidator.validate( user.getEmail() );

        User tmpuser = user.createUser( manager );

        try
        {
            securityPolicy.validatePassword( tmpuser );
        }
        catch ( PasswordRuleViolationException e )
        {
            processPasswordRuleViolations( e );
        }

        if ( ( StringUtils.isEmpty( user.getPassword() ) ) )
        {
            addFieldError( "user.password", "Password is required." );
        }
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

    // ------------------------------------------------------------------
    // Internal Support Methods
    // ------------------------------------------------------------------

    private void processPasswordRuleViolations( PasswordRuleViolationException e )
    {
        PasswordRuleViolations violations = e.getViolations();

        if ( violations != null )
        {
            Iterator it = violations.getLocalizedViolations().iterator();
            while ( it.hasNext() )
            {
                String violation = (String) it.next();
                addFieldError( "user.password", violation );
            }
        }
    }

}
