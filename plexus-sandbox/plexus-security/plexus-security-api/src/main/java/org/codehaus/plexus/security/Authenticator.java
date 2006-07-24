package org.codehaus.plexus.security;

import org.codehaus.plexus.security.exception.AuthenticationException;
import org.codehaus.plexus.security.exception.NotAuthenticatedException;

import java.util.Map;

/**
 * Authenticator
 *
 * @author Jesse McConnell
 * @version $ID:$
 */
public interface Authenticator
{
    String ROLE = Authenticator.class.getName();

    public boolean isAuthenticated( Map tokens )
        throws AuthenticationException;

    public AuthenticationResult authenticate( Map tokens )
        throws NotAuthenticatedException, AuthenticationException;

}
