package org.codehaus.plexus.security.acegi;

import org.codehaus.plexus.security.AuthenticationStore;
import org.codehaus.plexus.security.AuthenticationResult;
import org.codehaus.plexus.security.exception.NotAuthenticatedException;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;

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
 * AcegiAuthenticationStore:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 * @plexus.component role="org.codehaus.plexus.security.AuthenticationStore"
 * role-hint="acegi"
 */
public class AcegiAuthenticationStore
    implements AuthenticationStore
{
    /**
     * @plexus.requirement
     */
    private ProviderManager manager;

    /**
     * @plexus.requirement
     */
    private AuthenticationTokenFactory tokenFactory;

    public AuthenticationResult authenticate( Map tokens )
        throws org.codehaus.plexus.security.exception.AuthenticationException
    {
        Authentication authenticationToken = tokenFactory.getAuthenticationToken( tokens );

        try
        {
            Authentication response = manager.doAuthentication( authenticationToken );

            AuthenticationResult authResult = new AuthenticationResult( AcegiAuthenticationStore.class.getName() );

            authResult.setAuthenticated( true );

            authResult.setPrincipal( response.getPrincipal().toString() );

            return authResult;
        }
        catch ( AuthenticationException e )
        {
            throw new org.codehaus.plexus.security.exception.AuthenticationException( "Authentication Exception", e );
        }

    }
}
