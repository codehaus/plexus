package org.codehaus.plexus.security.policy;

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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.policy.rules.MustHavePasswordRule;
import org.codehaus.plexus.security.user.User;
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
    implements UserSecurityPolicy, Initializable, Contextualizable
{
    private static final String ENABLEMENT_KEY = DefaultUserSecurityPolicy.ROLE + ":ENABLED";

    /**
     * @plexus.requirement role-hint="sha256"
     */
    private PasswordEncoder passwordEncoder;
    
    /**
     * @plexus.configuration default-value="6"
     */
    private int previousPasswordsCount;

    /**
     * @plexus.configuration default-value="3"
     */
    private int loginAttemptCount;

    /**
     * @plexus.configuration default-value="90"
     */
    private int passwordExpirationDays;
    
    /**
     * @plexus.requirement
     */
    private UserValidationSettings userValidationSettings;
    
    /**
     * The List of {@link PasswordRule} objects.
     * 
     * @plexus.requirement role="org.codehaus.plexus.security.policy.PasswordRule"
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
    
    public boolean isEnabled()
    {
        Boolean bool = (Boolean) PolicyContext.getContext().get( ENABLEMENT_KEY );
        if(bool == null)
        {
            // no key? assume true. (default is true)
            return true;
        }
        
        return bool.booleanValue();
    }

    public void setEnabled( boolean enabled )
    {
        PolicyContext.getContext().put( ENABLEMENT_KEY, new Boolean( enabled ) );
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
    
    public void extensionPasswordExpiration( User user )
        throws MustChangePasswordException
    {
        
    }
    
    public void extensionExcessiveLoginAttempts( User user )
        throws AccountLockedException
    {
        int attempt = user.getCountFailedLoginAttempts();
        attempt++;
        user.setCountFailedLoginAttempts( attempt );
        
        if( attempt >= getLoginAttemptCount() )
        {
            throw new AccountLockedException();
        }
    }

    public void extensionChangePassword( User user )
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
        if ( isEnabled() )
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
        }

        // If you got this far, then ensure that the password is never null.
        if ( user.getPassword() == null )
        {
            user.setPassword( "" );
        }
    }

    public void initialize()
        throws InitializationException
    {
        rules = new ArrayList();

        try
        {
            rules.add( (PasswordRule) plexus.lookup( PasswordRule.ROLE, "must-have" ) );
            rules.add( (PasswordRule) plexus.lookup( PasswordRule.ROLE, "character-length" ) );
            rules.add( (PasswordRule) plexus.lookup( PasswordRule.ROLE, "alpha-count" ) );
            rules.add( (PasswordRule) plexus.lookup( PasswordRule.ROLE, "numerical-count" ) );
            rules.add( (PasswordRule) plexus.lookup( PasswordRule.ROLE, "reuse" ) );
        }
        catch ( ComponentLookupException e )
        {
            throw new InitializationException( "Unable to lookup password rules.", e );
        }

        if ( rules != null )
        {
            Iterator it = rules.listIterator();
            while ( it.hasNext() )
            {
                PasswordRule rule = (PasswordRule) it.next();
                if ( !rule.isEnabled() )
                {
                    it.remove();
                }
                else
                {
                    rule.setUserSecurityPolicy( this );
                }
            }
        }
        else
        {
            rules = new ArrayList();
            addPasswordRule( new MustHavePasswordRule() );
        }
    }

    private PlexusContainer plexus;

    public void contextualize( Context context )
        throws ContextException
    {
        plexus = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public int getPasswordExpirationDays()
    {
        return passwordExpirationDays;
    }

    public void setPasswordExpirationDays( int passwordExpirationDays )
    {
        this.passwordExpirationDays = passwordExpirationDays;
    }

    public UserValidationSettings getUserValidationSettings()
    {
        return userValidationSettings;
    }

    public void setUserValidationSettings( UserValidationSettings userValidationSettings )
    {
        this.userValidationSettings = userValidationSettings;
    }
}
