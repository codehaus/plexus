package org.codehaus.plexus.security.osuser;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.Authentication;
import org.codehaus.plexus.security.Authenticator;
import org.codehaus.plexus.security.DefaultAuthentication;
import org.codehaus.plexus.security.DefaultUser;
import org.codehaus.plexus.security.User;
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

    public Authentication authenticate( Map tokens )
        throws UnknownEntityException, AuthenticationException, UnauthorizedException
    {
        String username = (String) tokens.get( "username" );
        String password = (String) tokens.get( "password" );
        HttpServletRequest request = (HttpServletRequest) tokens.get( "request" );

        getLogger().debug( "username = " + username );

        try
        {
            boolean authenticated;

            if ( request == null )
            {
                authenticated = userManager.getAuthenticator().login( username, password );
            }
            else
            {
                authenticated = userManager.getAuthenticator().login( username, password, request );
            }

            if ( authenticated )
            {
                Authentication auth = new DefaultAuthentication();

                auth .setAuthenticated( true );

                auth.setUser( getUser( userManager.getUser( username ) ) );

                return auth;
            }
            else
            {
                throw new AuthenticationException( "Invalid login/password." );
            }
        }
        catch ( Exception e )
        {
            throw new AuthenticationException( "Authentication failed.", e );
        }
    }

    public Authentication getAnonymousEntity()
    {
        try
        {
            Authentication auth = new DefaultAuthentication();

            auth .setAuthenticated( false );

            auth.setUser( getUser( userManager.getUser( getAnonymousUsername() ) ) );

            return auth;
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

    public User getUser( com.opensymphony.user.User osuser )
    {
        DefaultUser user = new DefaultUser();
        user.setUsername( osuser.getName() );
        user.setDetails( osuser );
        user.setEnabled( true );
        user.setAccountNonExpired( true );
        user.setAccountNonLocked( true );
        user.setPasswordNonExpired( true );
        return user;
    }

    protected UserManager getUserManager()
    {
        return userManager;
    }
}
