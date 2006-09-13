package org.codehaus.plexus.security.authentication;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.util.StringUtils;

/**
 * @author Jason van Zyl
 *
 * todo which this back to an interface and use the mojo style expression evaluation to populate the particular required fields
 */
public class AuthenticationDataSource
{
    public String ROLE = AuthenticationDataSource.class.getName();

    private String username;

    private String password;
    
    private String defaultPrincipal;

    public AuthenticationDataSource( String login, String password )
    {
        this.username = login;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getDefaultPrincipal()
    {
        return defaultPrincipal;
    }
    
    public void setDefaultPrincipal( String principal )
    {
        defaultPrincipal = principal;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "AuthenticationDataSource[" );
        sb.append( "username=" ).append( username );
        sb.append( ",password=" );
        if ( StringUtils.isNotEmpty( password ) )
        {
            // Intentionally not showing real password 
            sb.append( "***" );
        }
        sb.append( ",defaultPrincipal=" ).append( defaultPrincipal );
        sb.append( "]" );
        return sb.toString();
    }
}
