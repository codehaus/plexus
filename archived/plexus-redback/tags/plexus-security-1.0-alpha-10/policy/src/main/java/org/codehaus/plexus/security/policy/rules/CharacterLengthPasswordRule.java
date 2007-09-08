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
 * Basic Password Rule, Checks for non-empty passwords that have between {@link #setMinimumCharacters(int)} and
 * {@link #setMaximumCharacters(int)} characters in length.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.policy.PasswordRule" role-hint="character-length"
 */
public class CharacterLengthPasswordRule
    extends AbstractPasswordRule
{
    public static final String CHARACTER_LENGTH_MIN = "security.policy.password.rule.characterlength.minimum";

    public static final String CHARACTER_LENGTH_MAX = "security.policy.password.rule.characterlength.maximum";

    public static final String CHARACTER_LENGTH_MISCONFIGURED_VIOLATION =
        "user.password.violation.length.misconfigured";

    public static final String CHARACTER_LENGTH_VIOLATION = "user.password.violation.length";

    public static final int DEFAULT_CHARACTER_LENGTH_MAX = 8;

    private int minimumCharacters;

    private int maximumCharacters;

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
            violations.addViolation( CHARACTER_LENGTH_MISCONFIGURED_VIOLATION, new Object[]{
                String.valueOf( minimumCharacters ), String.valueOf( maximumCharacters )} ); //$NON-NLS-1$
        }

        String password = user.getPassword();

        if ( StringUtils.isEmpty( password ) || password.length() < minimumCharacters ||
            password.length() > maximumCharacters )
        {
            violations.addViolation( CHARACTER_LENGTH_VIOLATION, new Object[]{String.valueOf( minimumCharacters ),
                String.valueOf( maximumCharacters )} ); //$NON-NLS-1$
        }
    }

    public void initialize()
        throws InitializationException
    {
        enabled = config.getBoolean( "security.policy.password.rule.characterlength.enabled" );
        this.minimumCharacters = config.getInt( CHARACTER_LENGTH_MIN );
        this.maximumCharacters = config.getInt( CHARACTER_LENGTH_MAX );
    }
}
