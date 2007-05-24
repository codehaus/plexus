package org.codehaus.plexus.redback.authentication;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.policy.AccountLockedException;
import org.codehaus.plexus.redback.policy.MustChangePasswordException;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * HttpAuthenticator is the workings of an authenticator for http with the session storage abstracted
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @author Andrew Williams
 * @version $Id$
 */
public abstract class AbstractHttpAuthenticator
    extends AbstractLogEnabled
    implements HttpAuthenticator
{
    public static final String ROLE = AbstractHttpAuthenticator.class.getName();

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;

    /**
     * The Public Face of the Authenticator.
     *
     * @throws org.codehaus.plexus.redback.policy.MustChangePasswordException
     * @throws org.codehaus.plexus.redback.policy.AccountLockedException
     */
    protected AuthenticationResult authenticate( AuthenticationDataSource ds, Object session )
        throws AuthenticationException, AccountLockedException, MustChangePasswordException
    {
        try
        {
            SecuritySession securitySession = securitySystem.authenticate( ds );

            setSecuritySession( securitySession, session );

            return securitySession.getAuthenticationResult();
        }
        catch ( AuthenticationException e )
        {
            String msg = "Unable to authenticate user: " + ds;
            getLogger().info( msg, e );
            throw new HttpAuthenticationException( msg, e );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().info( "Login attempt against unknown user: " + ds );
            throw new HttpAuthenticationException( "User name or password invalid." );
        }
    }

    /**
     * Entry point for a Filter.
     *
     * @param request
     * @param response
     * @throws org.codehaus.plexus.redback.authentication.AuthenticationException
     */
    public void authenticate( HttpServletRequest request, HttpServletResponse response )
        throws AuthenticationException
    {
        try
        {
            AuthenticationResult result = getAuthenticationResult( request, response );

            if ( ( result == null ) || ( !result.isAuthenticated() ) )
            {
                throw new HttpAuthenticationException( "You are not authenticated." );
            }
        }
        catch ( AccountLockedException e )
        {
            throw new HttpAuthenticationException( "Your account is locked." );
        }
        catch ( MustChangePasswordException e )
        {
            throw new HttpAuthenticationException( "You must change your password." );
        }

    }

    protected abstract Object getSessionValue( Object session, String key );

    protected abstract void setSessionValue( Object session, String key, Object value );

    protected User getSessionUser( Object session )
    {
        return (User) getSessionValue( session, SecuritySession.USERKEY );
    }

    protected boolean isAlreadyAuthenticated( Object session )
    {
        User user = getSessionUser( session );

        return ( ( user != null ) && !user.isLocked() );
    }

    protected SecuritySession getSecuritySession( Object session )
    {
        return (SecuritySession) getSessionValue( session, SecuritySession.ROLE );
    }

    protected void setSecuritySession( SecuritySession session, Object sessionObj )
    {
        setSessionValue( sessionObj, SecuritySession.ROLE, session );
        setSessionValue( sessionObj, SecuritySession.USERKEY, session.getUser() );
    }

    protected void setSessionUser( User user, Object session )
    {
        setSessionValue( session, SecuritySession.ROLE, null );
        setSessionValue( session, SecuritySession.USERKEY, user );
    }

    protected String storeDefaultUser( String principal, Object session )
    {
        setSessionValue( session, SecuritySession.ROLE, null );
        setSessionValue( session, SecuritySession.USERKEY, null );

        if ( StringUtils.isEmpty( principal ) )
        {
            return null;
        }

        try
        {
            User user = securitySystem.getUserManager().findUser( principal );
            setSessionValue( session, SecuritySession.USERKEY, user );

            return user.getPrincipal().toString();

        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Default User '" + principal + "' not found.", e );
            return null;
        }
    }
}
