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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.policy.PasswordRuleViolations;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.util.StringUtils;

/**
 * Basic Password Rule, Checks for non-empty passwords that have at least {@link #setMinimumCount(int)} of
 * alpha characters contained within.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.policy.PasswordRule" role-hint="alpha-count"
 */
public class AlphaPasswordRule
    extends AbstractPasswordRule
{
    public static final String ALPHA_COUNT = PASSWORD_RULE_CONFIGKEY + ".alphacount";

    public static final String ALPHA_COUNT_MIN = ALPHA_COUNT + ".minimum";

    public static final String ALPHA_COUNT_VIOLATION = "user.password.violation.alpha";

    public static final int DEFAULT_MINIMUM = 1;

    private int minimumCount;

    public AlphaPasswordRule()
    {
        minimumCount = DEFAULT_MINIMUM;
    }

    private int countAlphaCharacters( String password )
    {
        int count = 0;

        if ( StringUtils.isEmpty( password ) )
        {
            return count;
        }

        /* TODO: Eventually upgrade to the JDK 1.5 Technique
         * 
         * // Doing this via iteration of code points to take in account localized passwords.
         * for ( int i = 0; i < password.length(); i++ )
         * {
         *     int codepoint = password.codePointAt( i );
         *     if ( Character.isLetter( codepoint ) )
         *     {
         *        count++;
         *     }
         * }
         */

        // JDK 1.4 Technique - NOT LOCALIZED.
        for ( int i = 0; i < password.length(); i++ )
        {
            char c = password.charAt( i );
            if ( Character.isLetter( c ) )
            {
                count++;
            }
        }

        return count;
    }

    public int getMinimumCount()
    {
        return minimumCount;
    }

    public void setMinimumCount( int minimumCount )
    {
        this.minimumCount = minimumCount;
    }

    public void setUserSecurityPolicy( UserSecurityPolicy policy )
    {
        // Ignore, policy not needed in this rule.
    }

    public void testPassword( PasswordRuleViolations violations, User user )
    {
        if ( countAlphaCharacters( user.getPassword() ) < this.minimumCount )
        {
            violations.addViolation( ALPHA_COUNT_VIOLATION, new Object[]{new Integer( minimumCount )} ); //$NON-NLS-1$
        }
    }

    public void initialize()
        throws InitializationException
    {
        super.configure( ALPHA_COUNT );
        this.minimumCount = config.getInt( ALPHA_COUNT_MIN, DEFAULT_MINIMUM );

    }
}
