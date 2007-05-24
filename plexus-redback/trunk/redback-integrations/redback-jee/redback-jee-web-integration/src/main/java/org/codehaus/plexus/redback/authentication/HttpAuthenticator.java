package org.codehaus.plexus.redback.authentication;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.policy.AccountLockedException;
import org.codehaus.plexus.redback.policy.MustChangePasswordException;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HttpAuthenticator
 *
 * @author Andrew Williams
 * @version $Id$
 */
public interface HttpAuthenticator
{
    public static final String ROLE = HttpAuthenticator.class.getName();

    /**
     * Entry point for a Filter.
     *
     * @param request
     * @param response
     * @throws AuthenticationException
     */
    public void authenticate( HttpServletRequest request, HttpServletResponse response )
        throws AuthenticationException;

    /**
     * Issue a Challenge Response back to the HTTP Client.
     *
     * @param request
     * @param response
     * @param realmName
     * @param exception
     * @throws java.io.IOException
     */
    public void challenge( HttpServletRequest request, HttpServletResponse response, String realmName,
                           AuthenticationException exception )
        throws IOException;

    /**
     * Parse the incoming request and return an AuthenticationResult.
     *
     * @param request
     * @param response
     * @return null if no http auth credentials, or the actual authentication result based on the credentials.
     * @throws AuthenticationException
     * @throws org.codehaus.plexus.redback.policy.MustChangePasswordException
     * @throws org.codehaus.plexus.redback.policy.AccountLockedException
     */
    public AuthenticationResult getAuthenticationResult( HttpServletRequest request,
                                                         HttpServletResponse response )
        throws AuthenticationException, AccountLockedException, MustChangePasswordException;

}
