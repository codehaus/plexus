package org.codehaus.plexus.security.acegi;

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.codehaus.plexus.security.exception.AuthenticationException;

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
 * DefaultAuthenticationTokenFactory:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.acegi.AuthenticationTokenFactory"
 */
public class DefaultAuthenticationTokenFactory
    implements AuthenticationTokenFactory
{
    public Authentication getAuthenticationToken( Map tokenMap )
        throws AuthenticationException
    {

        String tokenType = (String)tokenMap.get( "authTokenType" );

        if ( tokenType != null )
        {
            if ( UsernamePasswordAuthenticationToken.class.getName().equals( tokenType ) )
            {
                return getUsernamePasswordAuthenticationToken( tokenMap );
            }
            else
            {
                throw new AuthenticationException( "unsupported authentication token type " + tokenType );
            }
        }
        else
        {
            throw new AuthenticationException( "unable to discover authentication token type" );
        }

    }

    private Authentication getUsernamePasswordAuthenticationToken( Map tokenMap )
        throws AuthenticationException
    {
        Object username = tokenMap.get( "username" );
        Object password = tokenMap.get( "password" );

        if ( username == null )
        {
            throw new AuthenticationException( "unable to build authentication token, username missing" );
        }
        else if ( password == null )
        {
            throw new AuthenticationException( "unable to build authentication token, password missing" );
        }
        else
        {
            return new UsernamePasswordAuthenticationToken( username, password );
        }

    }

}
