package org.codehaus.plexus.security.authorization;

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

    public AuthorizationResult authorize( AuthorizationDataSource source )
        throws AuthorizationException;
}
