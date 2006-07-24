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
 * AuthorizationStore: interface that the implementation of an authorization system must provide so it can be wired
 * into the PlexusSecurityRealm
 *
 * TODO: add a method for returning an AuthorizationResult instead of throwing the NotAuthorizedException, possibly not even returning the AuthenticationException
 *
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public interface AuthorizationStore
{
    public static String ROLE = AuthorizationStore.class.getName();

    /**
     * Check if the session and the map of tokens contain the necessary information to determine if the usage would
     * be authorized or not.  If not enough information is present then the method should throw an AuthenticationException
     *
     * @param session
     * @param tokens
     * @return
     * @throws AuthorizationException
     */
    public boolean isAuthorized( PlexusSecuritySession session, Map tokens )
        throws AuthorizationException;

    /**
     * Check if the session and the map of tokens contain the necessary information to determine if the usage would
     * be authorized or not.  If the usage would result in an unauthorized activity then throw the NotAuthorizedException
     * If not enough information is present then the method should throw an AuthenticationException.  If the activity is
     * authorized then create an AuthorizationResult and return it, this method should only return successful authorization
     * or throw an exception.
     *
     * @param session
     * @param tokens
     * @return
     * @throws NotAuthorizedException
     * @throws AuthorizationException
     */
    public AuthorizationResult authorize( PlexusSecuritySession session, Map tokens )
        throws NotAuthorizedException, AuthorizationException;

}
