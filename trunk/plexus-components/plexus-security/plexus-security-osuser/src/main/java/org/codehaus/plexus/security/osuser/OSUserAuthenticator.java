package org.codehaus.plexus.security.osuser;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.Authenticator;
import org.codehaus.plexus.security.exception.AuthenticationException;
import org.codehaus.plexus.security.exception.UnauthorizedException;
import org.codehaus.plexus.security.exception.UnknownEntityException;

import com.opensymphony.user.UserManager;
import com.opensymphony.user.EntityNotFoundException;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class OSUserAuthenticator
    extends AbstractLogEnabled
    implements Authenticator
{
    private UserManager userManager;

    public Object authenticate( Map tokens )
        throws UnknownEntityException, AuthenticationException, UnauthorizedException
    {
        String username = (String) tokens.get( "username" );
        String password = (String) tokens.get( "password" );
        HttpServletRequest request = (HttpServletRequest) tokens.get( "request" );

        getLogger().debug( "username = " + username );

        try
        {
            if ( userManager.getAuthenticator().login( username, password, request ) )
            {
                return userManager.getUser( username );
            }
            else
            {
                throw new AuthenticationException( "Invalid password." );
            }
        }
        catch ( Exception e )
        {
            throw new AuthenticationException( "Authentication failed.", e );
        }
    }

    public Object getAnonymousEntity()
    {
        try
        {
            return userManager.getUser( getAnonymousUsername() );
        }
        catch ( EntityNotFoundException e )
        {
            return null;
        }
    }

    public String getAnonymousUsername()
    {
        return "guest";
    }

    protected UserManager getUserManager()
    {
        return userManager;
    }
}
