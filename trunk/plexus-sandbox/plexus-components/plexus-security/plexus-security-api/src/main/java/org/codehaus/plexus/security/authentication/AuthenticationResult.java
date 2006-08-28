package org.codehaus.plexus.security.authentication;
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

import java.io.Serializable;

/**
 * AuthenticationResult: wrapper object for information that comes back from the authentication system
 *
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $ID:$
 */
public class AuthenticationResult
    implements Serializable
{
    /**
     * the name of the authentication store that generated this AuthenticationResult
     */
    private String authenticationStore;

    /**
     * boolean representing authentication status
     */
    private boolean isAuthenticated = false;

    /**
     * the main actor or principal agent in the system, most commonly the username corresponding to the authentication
     * request
     *
     * Note: the toString() of the principle Object should always represent the unique agent
     */
    private Object principal;

    /**
     * optional message the authentication store can set
     */
    private String message;

    /**
     * optional exception the authentication store can set
     */
    private Exception exception;

    /**
     * optional authentication payload object
     */
    private Object payload;

    private AuthenticationResult(){}    

    public AuthenticationResult( String authenticationStore )
    {
        this.authenticationStore = authenticationStore;
    }

    public void setAuthenticated( boolean isAuthenticated )
    {
        this.isAuthenticated = isAuthenticated;
    }

    public boolean isAuthenticated()
    {
        return isAuthenticated;
    }

    public Object getPrincipal()
    {
        return principal;
    }

    public void setPrincipal( Object principal )
    {
        this.principal = principal;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public Exception getException()
    {
        return exception;
    }

    public void setException( Exception exception )
    {
        this.exception = exception;
    }

    public Object getPayload()
    {
        return payload;
    }

    public void setPayload( Object payload )
    {
        this.payload = payload;
    }

    public String getAuthenticationStore()
    {
        return authenticationStore;
    }

    public void setAuthenticationStore( String authenticationStore )
    {
        this.authenticationStore = authenticationStore;
    }
}
