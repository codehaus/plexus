package org.codehaus.plexus.security.authentication;

/*
 * Copyright 2001-2006 The Codehaus.
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
 * TokenBasedAuthenticationDataSource 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.authentication.AuthenticationDataSource"
 *                   role-hint="token"
 *                   instantiation-strategy="per-lookup"
 */
public class TokenBasedAuthenticationDataSource
    implements AuthenticationDataSource
{
    private String token;

    private String principal;


    public TokenBasedAuthenticationDataSource( String principal )
    {
        this.principal = principal;
    }

    public TokenBasedAuthenticationDataSource()
    {
    }

    public String getPrincipal()
    {
        return principal;
    }

    public String getToken()
    {
        return token;
    }

    public void setPrincipal( String principal )
    {
        this.principal = principal;
    }

    public void setToken( String token )
    {
        this.token = token;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "TokenBasedAuthenticationDataSource[" );
        sb.append( "principal=" ).append( principal );
        sb.append( ",token=" ).append( token );
        sb.append( ']' );
        return sb.toString();
    }
}
