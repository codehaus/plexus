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
 * AuthenticationStore: interface that the implementation of an authentication system must provide so it can be wired
 * into the PlexusSecurityRealm
 *
 * TODO: add a method for returning an AuthenticationResult instead of throwing the NotAuthenticatedException, possibly not even returning the AuthenticationException
 *
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 */
public interface AuthenticationStore
{
    public static String ROLE = AuthenticationStore.class.getName();


    /**
     * A check to see if the map of tokens will authenticate or not
     *
     * @param tokens
     * @return
     * @throws AuthenticationException
     */
    public boolean isAuthenticated( Map tokens ) throws AuthenticationException;

    /**
     * Authenticate the map of tokens and return an authentication object or throw an
     * AuthenticationException.  If the tokens would not result in an AuthenticationResult then the implementation
     * should throw a NotAuthenticatedException.
     *
     * @param tokens
     * @return
     * @throws AuthenticationException
     * @throws NotAuthenticatedException
     */
    public AuthenticationResult authenticate( Map tokens ) throws NotAuthenticatedException, AuthenticationException ;

}
