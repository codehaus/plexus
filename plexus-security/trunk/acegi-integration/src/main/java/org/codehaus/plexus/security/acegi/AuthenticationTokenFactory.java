package org.codehaus.plexus.security.acegi;

import org.acegisecurity.Authentication;
import org.codehaus.plexus.security.authentication.AuthenticationException;

import java.util.Map;

/**
 * AuthenticationTokenFactory:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public interface AuthenticationTokenFactory
{
    public static final String ROLE = AuthenticationTokenFactory.class.getName();

    Authentication getAuthenticationToken( Map tokenMap ) throws AuthenticationException;

}
