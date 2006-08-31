package org.codehaus.plexus.security.system;

import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.system.SecuritySession;

/**
 * @author Jason van Zyl
 */
public class DefaultSecuritySession
    implements SecuritySession
{
    private AuthenticationResult authenticationResult;

    private User user;

    public DefaultSecuritySession( AuthenticationResult authenticationResult,
                                   User user )
    {
        this.authenticationResult = authenticationResult;
        this.user = user;
    }

    public AuthenticationResult getAuthenticationResult()
    {
        return authenticationResult;
    }

    public User getUser()
    {
        return user;
    }
}
