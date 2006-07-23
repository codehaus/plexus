package org.codehaus.plexus.security;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.security.exception.AuthenticationException;
import org.codehaus.plexus.security.exception.AuthorizationException;
import org.codehaus.plexus.security.exception.NotAuthenticatedException;
import org.codehaus.plexus.security.exception.PlexusSecurityRealmException;

import java.util.Map;
/*
 * Copyright 2005 The Codehaus.
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

/**
 * DefaultPlexusSecurityRealm:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.PlexusSecurityRealm"
 *   role-hint="default"
 */
public class DefaultPlexusSecurityRealm
    extends AbstractLogEnabled
    implements PlexusSecurityRealm, Contextualizable
{
    /**
     * @plexus.requirement
     */
    private Authenticator authenticator;

    /**
     * @plexus.requirement
     */
    private Authorizer authorizer;

    PlexusContainer container;

    private Map securityRealms;

    public PlexusSecurityRealm createSecurityRealm( String id, String authenticator, String authorizer )
        throws PlexusSecurityRealmException
    {
        PlexusSecurityRealm psr = new DefaultPlexusSecurityRealm();

        try
        {
            if ( "global".equals( authenticator ) )
            {
                psr.setAuthenticator( this.authenticator );
            }
            else
            {
                psr.setAuthenticator( (Authenticator) container.lookup( "org.codehaus.plexus.security.Authenticator", authenticator ) );
            }
            
            psr.setAuthorizer( (Authorizer) container.lookup( "org.codehaus.plexus.security.Authorizer", authorizer ) );

            securityRealms.put( id, psr );

            return psr;
        }
        catch ( ComponentLookupException e)
        {
            throw new PlexusSecurityRealmException( "unable to create security realm", e);
        }

    }


    public PlexusSecurityRealm getSecurityRealm( String id )
        throws PlexusSecurityRealmException
    {
        if ( securityRealms.containsKey( id ) )
        {
            return (PlexusSecurityRealm) securityRealms.get( id );
        }
        else
        {
            throw new PlexusSecurityRealmException( "security realm does not exist: " + id );
        }
    }

    public boolean isAuthentic( Map tokens )
        throws AuthenticationException
    {
        return authenticator.isAuthentic( tokens );
    }

    public boolean isAuthorized( PlexusSecuritySession session, Map tokens )
        throws AuthorizationException
    {
        return authorizer.isAuthorized( session, tokens );
    }

    /**
     * attempts to authenticate based on the tokens passed in, if authentication fails then a NotAuthenticatedException
     * is thrown so that unauthenticated sessions are never created
     *
     * TODO: do we pass back unauthenticated sessions and their AuthenticationResults?
     *
     * @param tokens
     * @return
     * @throws NotAuthenticatedException
     * @throws AuthenticationException
     */
    public PlexusSecuritySession authenticate( Map tokens )
        throws NotAuthenticatedException, AuthenticationException
    {
        AuthenticationResult authenticationResult = authenticator.authenticate( tokens );

        PlexusSecuritySession session = new DefaultPlexusSecuritySession();

        if ( authenticationResult.isAuthenticated() )
        {
            session.setAuthentic( true );
        }
        
        session.setAuthenticationResult( authenticationResult );

        return session;
    }

    public void setAuthenticator( Authenticator authenticator )
    {
        this.authenticator = authenticator;
    }

    public void setAuthorizer( Authorizer authorizer )
    {
        this.authorizer = authorizer;
    }

    // ----------------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
