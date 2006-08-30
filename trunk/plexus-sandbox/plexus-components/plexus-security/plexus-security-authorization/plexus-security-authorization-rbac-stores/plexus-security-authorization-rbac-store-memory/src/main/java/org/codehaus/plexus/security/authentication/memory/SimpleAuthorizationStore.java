package org.codehaus.plexus.security.authentication.memory;

import org.codehaus.plexus.security.AuthorizationResult;
import org.codehaus.plexus.security.AuthorizationStore;
import org.codehaus.plexus.security.PlexusSecuritySession;
import org.codehaus.plexus.security.exception.AuthorizationException;

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
 * SimpleAuthorizationStore:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 * @plexus.component role="org.codehaus.plexus.security.AuthorizationStore"
 * role-hint="memory"
 */
public class SimpleAuthorizationStore
    implements AuthorizationStore
{
    public AuthorizationResult authorize( PlexusSecuritySession session, Map tokens )
        throws AuthorizationException
    {
        AuthorizationResult authResult = new AuthorizationResult( SimpleAuthorizationStore.class.getName() );

        authResult.setAuthorized( session.isAuthentic() );
        authResult.setPrincipal( session.getPrincipal() );

        return authResult;
    }
}
