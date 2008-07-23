package org.codehaus.jasf;

import org.codehaus.jasf.exception.AuthenticationException;
import org.codehaus.jasf.exception.UnauthorizedException;
import org.codehaus.jasf.exception.UnknownEntityException;

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
    public Object authenticate( String entityname, String password )
        throws UnknownEntityException, AuthenticationException, UnauthorizedException;
    
    /**
     * Return an anonymous entity that can be used to interact with
     * the system.
     * 
     * @return Entity
     */
    public Object getAnonymousEntity();
}
