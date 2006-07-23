package org.codehaus.plexus.security;

import org.codehaus.plexus.security.exception.AuthorizationException;
import org.codehaus.plexus.security.exception.NotAuthorizedException;

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
 * DefaultAuthorizer:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.Authorizer"
 *   role-hint="default"
 */
public class DefaultAuthorizer
    implements Authorizer
{

    /**
     * @plexus.requirement
     */
    private AuthorizationStore store;


    public boolean isAuthorized( PlexusSecuritySession session, Map tokens )
        throws AuthorizationException
    {
       return store.isAuthorized( session, tokens );
    }

    public AuthorizationResult authorize( PlexusSecuritySession session, Map tokens )
        throws NotAuthorizedException, AuthorizationException
    {
        return store.authorize( session, tokens );
    }
}

