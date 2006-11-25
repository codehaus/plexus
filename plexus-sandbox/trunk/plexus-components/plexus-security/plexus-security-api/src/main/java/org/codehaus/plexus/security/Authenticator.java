package org.codehaus.plexus.security;

import org.codehaus.plexus.security.exception.AuthenticationException;
import org.codehaus.plexus.security.exception.UnauthorizedException;
import org.codehaus.plexus.security.exception.UnknownEntityException;

import java.util.Map;

/**
 * Entity authentication functions.
 *
 * @author Dan Diephouse
 * @since Nov 20, 2002
 */
public interface Authenticator
{
    String ROLE = Authenticator.class.getName();

    String SELECTOR_ROLE = ROLE + "Selector";

    /**
     * Return an Entity based on the Entity name and password.
     *
     * @return Entity
     */
    Authentication authenticate( Map tokens )
        throws UnknownEntityException, AuthenticationException, UnauthorizedException;

    /**
     * Return an anonymous entity that can be used to interact with
     * the system.
     *
     * @return Entity
     */
    Authentication getAnonymousEntity();
}
