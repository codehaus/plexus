package org.codehaus.plexus.security;

import org.codehaus.plexus.security.exception.AuthenticationException;
import org.codehaus.plexus.security.exception.NotAuthenticatedException;

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
 * DefaultAuthenticator:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.Authenticator"
 *   role-hint="default"
 */
public class DefaultAuthenticator
    implements Authenticator
{

    /**
     * @plexus.requirement
     */
    private AuthenticationStore authStore;


    public boolean isAuthentic( Map tokens )
        throws AuthenticationException
    {
        return authStore.isAuthentic( tokens );
    }

    public PlexusSecuritySession authenticate( Map tokens )
        throws NotAuthenticatedException, AuthenticationException
    {
        return authStore.authenticate( tokens );
    }

}
