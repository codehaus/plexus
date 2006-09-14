package org.codehaus.plexus.security.policy;

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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.policy.rules.MustHavePasswordRule;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User Security Policy. 
 * 
 * @plexus.component role="org.codehaus.plexus.security.policy.UserSecurityPolicy"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DefaultUserSecurityPolicy
    implements UserSecurityPolicy, Initializable
{
    /**
     * @plexus.configuration default-value="6"
     */
    private int previousPasswordsCount;

    /**
     * @plexus.configuration default-value="3"
     */
    private int loginAttemptCount;

    /**
     * @plexus.requirement role-hint="sha256"
     */
    private PasswordEncoder passwordEncoder;

    /**
     * The List of {@link PasswordRule} objects.
     * 
     * @plexus.requirement role="org.apache.maven.user.model.PasswordRule" role-hint="must-have"
     */
    private List rules = new ArrayList();

    public int getPreviousPasswordsCount()
    {
        return previousPasswordsCount;
    }

    /**
     * Sets the count of previous passwords that should be tracked.
     * 
     * @param previousPasswordsCount the count of previous passwords to track.
     */
    public void setPreviousPasswordsCount( int previousPasswordsCount )
    {
        this.previousPasswordsCount = previousPasswordsCount;
    }

    public int getLoginAttemptCount()
    {
        return loginAttemptCount;
    }

    public void setLoginAttemptCount( int loginAttemptCount )
    {
        this.loginAttemptCount = loginAttemptCount;
    }
    
    /**
     * Get the password encoder to be used for password operations
     * 
     * @return the encoder
     */
    public PasswordEncoder getPasswordEncoder()
    {
        return passwordEncoder;
    }

    /**
     * Add a Specific Rule to the Password Rules List.
     * 
     * @param rule the rule to add. 
     */
    public void addPasswordRule( PasswordRule rule )
    {
        // TODO: check for duplicates? if so, check should only be based on Rule class name.

        rule.setUserSecurityPolicy( this );
        this.rules.add( rule );
    }

    /**
     * Get the Password Rules List.
     * 
     * @return the list of {@link PasswordRule} objects.
     */
    public List getPasswordRules()
    {
        return this.rules;
    }

    /**
     * Set the Password Rules List.
     * 
     * @param newRules the list of {@link PasswordRule} objects.
     */
    public void setPasswordRules( List newRules )
    {
        this.rules.clear();

        if ( newRules == null )
        {
            return;
        }

        // Intentionally iterating to ensure policy settings in provided rules.

        Iterator it = newRules.iterator();
        while ( it.hasNext() )
        {
            PasswordRule rule = (PasswordRule) it.next();
            rule.setUserSecurityPolicy( this );
            this.rules.add( rule );
        }
    }

    public void changeUserPassword( User user )
        throws PasswordRuleViolationException
    {
        validatePassword( user );

        // set the current encoded password.
        user.setEncodedPassword( getPasswordEncoder().encodePassword( user.getPassword() ) );
        user.setPassword( null );

        // push new password onto list of previous password.
        List previousPasswords = new ArrayList();
        previousPasswords.add( user.getEncodedPassword() );

        if ( !user.getPreviousEncodedPasswords().isEmpty() )
        {
            int oldCount = Math.min( getPreviousPasswordsCount() - 1, user.getPreviousEncodedPasswords().size() );
            //modified sublist start index as the previous value results to nothing being added to the list. 
            List sublist = user.getPreviousEncodedPasswords().subList( 0, oldCount );
            previousPasswords.addAll( sublist );
        }

        user.setPreviousEncodedPasswords( previousPasswords );

        // Update timestamp for password change.
        user.setLastPasswordChange( new Date() );
    }

    public void validatePassword( User user )
        throws PasswordRuleViolationException
    {
        // Trim password.
        user.setPassword( StringUtils.trim( user.getPassword() ) );

        PasswordRuleViolations violations = new PasswordRuleViolations();

        Iterator it = getPasswordRules().iterator();
        while ( it.hasNext() )
        {
            PasswordRule rule = (PasswordRule) it.next();
            rule.testPassword( violations, user );
        }

        if ( violations.hasViolations() )
        {
            PasswordRuleViolationException exception = new PasswordRuleViolationException();
            exception.setViolations( violations );
            throw exception;
        }
        
        // If you got this far, then ensure that the password is never null.
        if( user.getPassword() == null )
        {
            user.setPassword( "" );
        }
    }

    public void initialize()
        throws InitializationException
    {
        if ( rules != null )
        {
            Iterator it = rules.iterator();
            while ( it.hasNext() )
            {
                PasswordRule rule = (PasswordRule) it.next();
                rule.setUserSecurityPolicy( this );
            }
        }
        else
        {
            rules = new ArrayList();
            addPasswordRule( new MustHavePasswordRule() );
        }
    }

}
