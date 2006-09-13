package org.codehaus.plexus.security.ui.web.filter.authentication;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import com.opensymphony.xwork.ActionContext;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.Authenticator;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpAuthenticator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class HttpAuthenticator
    extends AbstractLogEnabled
    implements Authenticator
{
    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;

    /**
     * The Public Face of the Authenticator.
     */
    public AuthenticationResult authenticate( AuthenticationDataSource source )
        throws AuthenticationException
    {
        try
        {
            SecuritySession securitySession = securitySystem.authenticate( source );

            setSecuritySession( securitySession );
            
            return securitySession.getAuthenticationResult();
        }
        catch ( AuthenticationException e )
        {
            String msg = "Unable to authenticate user '" + source.getUsername() + "'";
            getLogger().info( msg, e );
            throw new HttpAuthenticationException( msg, e );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().info( "Login attempt against unknown user '" + source.getUsername() + "'." );
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
        AuthenticationResult result = getAuthenticationResult( request, response, null );

        if ( ( result == null ) || ( !result.isAuthenticated() ) )
        {
            throw new HttpAuthenticationException( "You are not authenticated." );
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
     * @param defaultPrincipal
     * @return null if no http auth credentials, or the actual authentication result based on the credentials.
     * @throws AuthenticationException
     */
    public abstract AuthenticationResult getAuthenticationResult( HttpServletRequest request,
                                                                  HttpServletResponse response, String defaultPrincipal )
        throws AuthenticationException;

    public User getSessionUser()
    {
        ActionContext context = ActionContext.getContext();
        return (User) context.getSession().get( SecuritySession.USERKEY );
    }

    public boolean isAlreadyAuthenticated()
    {
        User user = getSessionUser();

        return ( ( user != null ) && !user.isLocked() );
    }

    public SecuritySession getSecuritySession()
    {
        ActionContext context = ActionContext.getContext();
        return (SecuritySession) context.getSession().get( SecuritySession.ROLE );
    }

    public void setSecuritySession( SecuritySession session )
    {
        ActionContext context = ActionContext.getContext();
        context.getSession().put( SecuritySession.ROLE, session );
        context.getSession().put( SecuritySession.USERKEY, session.getUser() );
    }

    public void setSessionUser( User user )
    {
        ActionContext context = ActionContext.getContext();
        context.getSession().put( SecuritySession.ROLE, null );
        context.getSession().put( SecuritySession.USERKEY, user );
    }

    
    public String storeDefaultUser( String principal )
    {
        ActionContext context = ActionContext.getContext();
        context.getSession().put( SecuritySession.ROLE, null );
        context.getSession().put( SecuritySession.USERKEY, null );

        if ( StringUtils.isEmpty( principal ) )
        {
            return null;
        }

        try
        {
            User user = securitySystem.getUserManager().findUser( principal );
            context.getSession().put( SecuritySession.USERKEY, user );

            return user.getPrincipal().toString();

        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Default User '" + principal + "' not found.", e );
            return null;
        }
    }
}
