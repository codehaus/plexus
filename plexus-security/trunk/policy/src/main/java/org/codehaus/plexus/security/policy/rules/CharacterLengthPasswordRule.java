package org.codehaus.plexus.security.policy.rules;

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
import org.codehaus.plexus.security.policy.PasswordRule;
import org.codehaus.plexus.security.policy.PasswordRuleViolations;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.util.StringUtils;

/**
 * Basic Password Rule, Checks for non-empty passwords that have between {@link #setMinimumCharacters(int)} and 
 * {@link #setMaximumCharacters(int)} characters in length. 
 * 
 * @plexus.component role="org.codehaus.plexus.security.policy.PasswordRule" role-hint="character-length"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class CharacterLengthPasswordRule
    extends AbstractPasswordRule
    implements PasswordRule, Initializable
{
    private int minimumCharacters;

    private int maximumCharacters;

    public CharacterLengthPasswordRule()
    {
        minimumCharacters = 1;
        maximumCharacters = 8;
    }

    public int getMaximumCharacters()
    {
        return maximumCharacters;
    }

    public int getMinimumCharacters()
    {
        return minimumCharacters;
    }

    public void setMaximumCharacters( int maximumCharacters )
    {
        this.maximumCharacters = maximumCharacters;
    }

    public void setMinimumCharacters( int minimumCharacters )
    {
        this.minimumCharacters = minimumCharacters;
    }

    public void setUserSecurityPolicy( UserSecurityPolicy policy )
    {
        // Ignore, policy not needed in this rule.
    }

    public void testPassword( PasswordRuleViolations violations, User user )
    {
        if ( minimumCharacters > maximumCharacters )
        {
            /* this should caught up front during the configuration of the component */
            // TODO: Throw runtime exception instead?
            violations.addViolation( "user.password.violation.length.misconfigured", new Object[] {
                new Integer( minimumCharacters ),
                new Integer( maximumCharacters ) } ); //$NON-NLS-1$
        }

        String password = user.getPassword();

        if ( StringUtils.isEmpty( password ) || password.length() < minimumCharacters
            || password.length() > maximumCharacters )
        {
            violations.addViolation( "user.password.violation.length", new Object[] {
                new Integer( minimumCharacters ),
                new Integer( maximumCharacters ) } ); //$NON-NLS-1$
        }
    }

    public void initialize()
        throws InitializationException
    {
        final String PREFIX = PASSWORD_RULE_CONFIGKEY + "characterlength";
        super.configure( config, PREFIX );
        this.minimumCharacters = config.getInt( PREFIX + ".minimum", 1 );
        this.maximumCharacters = config.getInt( PREFIX + ".maximum", 8 );
    }
}
