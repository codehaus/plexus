package org.codehaus.plexus.security.authorization;

import org.codehaus.plexus.security.authorization.AuthorizationException;
import org.codehaus.plexus.security.authorization.AuthorizationResult;
import org.codehaus.plexus.security.authorization.AuthorizationStore;
import org.codehaus.plexus.security.authorization.Authorizer;
import org.codehaus.plexus.security.PlexusSecuritySession;

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
 * DefaultAuthorizer: the default authorizer optionally uses the store
 * <p/>
 * While the store is a requirement in the DefaultAuthenticator, it is optional in the Authorizer, if it is setup
 * then it will be used, otherwise it defaults to whether or not the session is authenticated.
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 * @plexus.component rolorg.codehaus.plexus.security.authorization.Authorizerzer"
 * role-hint="default"
 */
public class DefaultAuthorizer
    implements Authorizer
{
    /**
     *
     */
    private AuthorizationStore store;

    public AuthorizationResult authorize( PlexusSecuritySession session, Map tokens )
        throws AuthorizationException
    {
        if ( store == null )
        {
            AuthorizationResult authResult = new AuthorizationResult( DefaultAuthorizer.class.getName() );
            
            authResult.setAuthorized( session.isAuthentic() );

            return authResult;
        }
        else
        {
            return store.authorize( session, tokens );
        }
    }
}

