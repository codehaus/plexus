package org.codehaus.plexus.redback.xwork.filter.authentication;

/*
 * Copyright 2005-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.authentication.AuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.AuthenticationException;
import org.codehaus.plexus.redback.authentication.AuthenticationResult;
import org.codehaus.plexus.redback.policy.AccountLockedException;
import org.codehaus.plexus.redback.policy.MustChangePasswordException;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import com.opensymphony.xwork.ActionContext;

/**
 * HttpAuthenticator
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class HttpAuthenticator
    extends AbstractLogEnabled
{
    public static final String ROLE = HttpAuthenticator.class.getName();

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;

    /**
     * The Public Face of the Authenticator.
     *
     * @throws MustChangePasswordException
     * @throws AccountLockedException
     */
    public AuthenticationResult authenticate( AuthenticationDataSource ds )
        throws AuthenticationException, AccountLockedException, MustChangePasswordException
    {
        try
        {
            SecuritySession securitySession = securitySystem.authenticate( ds );

            setSecuritySession( securitySession );

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
     * @throws AuthenticationException
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

    /**
     * Issue a Challenge Response back to the HTTP Client.
     *
     * @param request
     * @param response
     * @param realmName
     * @param exception
     * @throws IOException
     */
    public abstract void challenge( HttpServletRequest request, HttpServletResponse response, String realmName,
                                    AuthenticationException exception )
        throws IOException;

    /**
     * Parse the incoming request and return an AuthenticationResult.
     *
     * @param request
     * @param response
     * @return null if no http auth credentials, or the actual authentication result based on the credentials.
     * @throws AuthenticationException
     * @throws MustChangePasswordException
     * @throws AccountLockedException
     */
    public abstract AuthenticationResult getAuthenticationResult( HttpServletRequest request,
                                                                  HttpServletResponse response )
        throws AuthenticationException, AccountLockedException, MustChangePasswordException;

    public Map getContextSession()
    {
        ActionContext context = ActionContext.getContext();
        Map sessionMap = context.getSession();
        if ( sessionMap == null )
        {
            sessionMap = new HashMap();
        }

        return sessionMap;
    }

    public User getSessionUser()
    {
        return (User) getContextSession().get( SecuritySession.USERKEY );
    }

    public boolean isAlreadyAuthenticated()
    {
        User user = getSessionUser();

        return ( ( user != null ) && !user.isLocked() );
    }

    public SecuritySession getSecuritySession()
    {
        return (SecuritySession) getContextSession().get( SecuritySession.ROLE );
    }

    public void setSecuritySession( SecuritySession session )
    {
        Map map = getContextSession();
        map.put( SecuritySession.ROLE, session );
        map.put( SecuritySession.USERKEY, session.getUser() );
        ActionContext.getContext().setSession( map );
    }

    public void setSessionUser( User user )
    {
        Map map = getContextSession();
        map.put( SecuritySession.ROLE, null );
        map.put( SecuritySession.USERKEY, user );
        ActionContext.getContext().setSession( map );
    }

    public String storeDefaultUser( String principal )
    {
        Map map = getContextSession();
        map.put( SecuritySession.ROLE, null );
        map.put( SecuritySession.USERKEY, null );
        ActionContext.getContext().setSession( map );

        if ( StringUtils.isEmpty( principal ) )
        {
            return null;
        }

        try
        {
            User user = securitySystem.getUserManager().findUser( principal );
            map.put( SecuritySession.USERKEY, user );
            ActionContext.getContext().setSession( map );

            return user.getPrincipal().toString();

        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Default User '" + principal + "' not found.", e );
            return null;
        }
    }
}
