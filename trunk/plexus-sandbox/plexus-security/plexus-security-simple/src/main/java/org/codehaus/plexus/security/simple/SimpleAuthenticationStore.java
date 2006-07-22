package org.codehaus.plexus.security.simple;

import org.codehaus.plexus.security.AuthenticationStore;
import org.codehaus.plexus.security.PlexusSecuritySession;
import org.codehaus.plexus.security.User;
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
 * SimpleAuthenticationStore:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.AuthenticationStore"
 *   role-hint="simple"
 */
public class SimpleAuthenticationStore
    implements AuthenticationStore
{


    public boolean isAuthentic( Map tokens )
        throws AuthenticationException
    {
        if ( tokens.containsKey ("username") && tokens.containsKey( "password") )
        {
            return true;
        }
        else
        {
            throw new AuthenticationException( "missing username or password" );
        }
    }

    public PlexusSecuritySession authenticate( Map tokens )
        throws NotAuthenticatedException, AuthenticationException
    {
        User user = new SimpleUser();

        user.setUsername( (String)tokens.get( "username" ) );
        user.setPassword( (String)tokens.get( "password" ) );

        PlexusSecuritySession credentials = new SimplePlexusSecuritySession( );

        credentials.setUser( user );

        return credentials;
    }

}
