package org.codehaus.plexus.security.system;

import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.user.User;

/**
 * @author Jason van Zyl
 */
public interface SecuritySession
{
    AuthenticationResult getAuthenticationResult();

    User getUser();
}
