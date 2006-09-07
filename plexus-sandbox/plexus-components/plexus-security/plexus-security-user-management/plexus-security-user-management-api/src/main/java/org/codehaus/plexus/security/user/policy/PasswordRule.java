package org.codehaus.plexus.security.user.policy;

import org.codehaus.plexus.security.user.User;


/**
 * A Password Rule
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface PasswordRule
{
    /**
     * Provide the Plexus Component Role.
     */
    public static final String ROLE = PasswordRule.class.getName();
    
    /**
     * Sets the User Security Policy to use.
     * 
     * The policy is set once per instance of a PasswordRule object.
     * 
     * @param policy the policy to use.
     */
    void setUserSecurityPolicy(UserSecurityPolicy policy);

    /**
     * Tests the {@link User#getPassword()} for a valid password, based on rule.
     * 
     * @param violations the place to add any password rule violations that this rule has discovered.
     * @param user the User to test.    
     */
    void testPassword( PasswordRuleViolations violations, User user );
}
