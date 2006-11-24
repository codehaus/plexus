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

import java.util.Iterator;

/**
 * Password Rule, Checks supplied password found at {@link User#getPassword()} against
 * the {@link User#getPreviousEncodedPasswords()} to ensure that a password is not reused.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.policy.PasswordRule" role-hint="reuse"
 */
public class ReusePasswordRule
    extends AbstractPasswordRule
{
    public static final String REUSE = PASSWORD_RULE_CONFIGKEY + ".reuse";

    public static final String REUSE_VIOLATION = "user.password.violation.reuse";

    private UserSecurityPolicy securityPolicy;

    public void setUserSecurityPolicy( UserSecurityPolicy policy )
    {
        this.securityPolicy = policy;
    }

    public int getPreviousPasswordCount()
    {
        return securityPolicy.getPreviousPasswordsCount();
    }

    private boolean hasReusedPassword( User user, String password )
    {
        if ( this.securityPolicy == null )
        {
            throw new IllegalStateException( "The security policy has not yet been set." );
        }

        if ( StringUtils.isEmpty( password ) )
        {
            return false;
        }

        String encodedPassword = securityPolicy.getPasswordEncoder().encodePassword( password );

        int checkCount = getPreviousPasswordCount();

        Iterator it = user.getPreviousEncodedPasswords().iterator();

        while ( it.hasNext() && checkCount >= 0 )
        {
            String prevEncodedPassword = (String) it.next();
            if ( encodedPassword.equals( prevEncodedPassword ) )
            {
                return true;
            }
            checkCount--;
        }

        return false;
    }

    public void setPreviousPasswordCount( int previousPasswordCount )
    {
        securityPolicy.setPreviousPasswordsCount( previousPasswordCount );
    }

    public void testPassword( PasswordRuleViolations violations, User user )
    {
        String password = user.getPassword();

        if ( hasReusedPassword( user, password ) )
        {
            violations.addViolation( REUSE_VIOLATION,
                                     new Object[]{new Integer( getPreviousPasswordCount() )} ); //$NON-NLS-1$
        }
    }

    public void initialize()
        throws InitializationException
    {
        super.configure( REUSE );
    }
}
