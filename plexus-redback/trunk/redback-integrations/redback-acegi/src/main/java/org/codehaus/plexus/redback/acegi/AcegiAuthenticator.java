package org.codehaus.plexus.redback.acegi;

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

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.ProviderManager;
import org.codehaus.plexus.redback.authentication.AuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.AuthenticationResult;
import org.codehaus.plexus.redback.authentication.Authenticator;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.Map;

/**
 * {@link Authenticator} implementation that uses the appropriate Acegi 
 * authentication provider implementation.<p>
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 * @plexus.component role="org.codehaus.plexus.redback.Authenticator" role-hint="acegi"
 */
public class AcegiAuthenticator
    extends AbstractLogEnabled
    implements Authenticator
{
    public String getId()
    {
        return "Acegi Authenticator";
    }

    /**
     * Token factory creates an Acegi authentication wrapper around credentials
     * that is used by the Acegi authentication provider.  
     * @plexus.requirement
     */
    private AuthenticationTokenFactory tokenFactory;

    /**
     * ProviderManager is the actual Acegi entity here that does the authentication 
     * after the {@link #tokenFactory} has return an Acegi {@link Authentication} 
     * request object.
     * @plexus.requirement
     */
    private ProviderManager manager;

    /**
     * @see org.codehaus.plexus.redback.authentication.Authenticator#authenticate(org.codehaus.plexus.redback.authentication.AuthenticationDataSource)
     */
    public AuthenticationResult authenticate( AuthenticationDataSource source )
        throws org.codehaus.plexus.redback.authentication.AuthenticationException
    {
        AcegiAuthenticationDataSource authsource = (AcegiAuthenticationDataSource) source;

        Map tokens = authsource.getTokenMap();
        Authentication authenticationToken = tokenFactory.getAuthenticationToken( tokens );

        try
        {
            Authentication response = manager.doAuthentication( authenticationToken );

            AuthenticationResult authResult = new AuthenticationResult( response.isAuthenticated(), response
                .getPrincipal().toString(), null );

            return authResult;
        }
        catch ( org.acegisecurity.AuthenticationException e )
        {
            getLogger().debug( e.getMessage(), e );
            return new AuthenticationResult( false, authsource.getUsername(), e );
        }
    }

    public boolean supportsDataSource( AuthenticationDataSource source )
    {
        return ( source instanceof AcegiAuthenticationDataSource );
    }
}
