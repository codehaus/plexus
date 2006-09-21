package org.codehaus.plexus.security.authentication.memory;

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

import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.Authenticator;
import org.codehaus.plexus.security.authentication.PasswordBasedAuthenticationDataSource;

/**
 * MemoryAuthenticator:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.authentication.Authenticator"
 *   role-hint="memory"
 */
public class MemoryAuthenticator
    implements Authenticator
{
    private String login;

    private String password;

    public String getId()
    {
        return "MemoryAuthenticator";
    }

    public AuthenticationResult authenticate( AuthenticationDataSource s )
        throws AuthenticationException
    {
        PasswordBasedAuthenticationDataSource source = (PasswordBasedAuthenticationDataSource) s;

        login = source.getUsername();
        password = source.getPassword();

        if ( source.getPassword().equals( password ) )
        {
            return new AuthenticationResult( true, login, null );
        }

        return new AuthenticationResult( false, null, null );
    }

    public boolean supportsDataSource( AuthenticationDataSource source )
    {
        return ( source instanceof PasswordBasedAuthenticationDataSource );
    }
}
