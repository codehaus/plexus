package org.codehaus.plexus.security;

import java.util.Map;

import org.codehaus.plexus.security.exception.AuthenticationException;
import org.codehaus.plexus.security.exception.UnauthorizedException;
import org.codehaus.plexus.security.exception.UnknownEntityException;

/**
 * Entity authentication functions.
 * 
 * @author Dan Diephouse
 * @since Nov 20, 2002
 */
public interface Authenticator
{
    public final static String ROLE = Authenticator.class.getName();
    
    public final static String SELECTOR_ROLE = ROLE + "Selector";
    
    /**
     * Return an Entity based on the Entity name and password.
     * 
     * @param entityname the name of the Entity
     * @param password the password for the Entity
     * @return Entity
     */
    public Object authenticate(Map tokens)
        throws UnknownEntityException, AuthenticationException, UnauthorizedException;
    
    /**
     * Return an anonymous entity that can be used to interact with
     * the system.
     * 
     * @return Entity
     */
    public Object getAnonymousEntity();
}
