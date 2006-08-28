package org.codehaus.plexus.security;

import org.codehaus.plexus.security.PlexusSecurityRealmException;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authorization.AuthorizationException;
import org.codehaus.plexus.security.authentication.NotAuthenticatedException;
import org.codehaus.plexus.security.authorization.Authorizer;
import org.codehaus.plexus.security.authentication.Authenticator;

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
 * PlexusSecurityRealm:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 */
public interface PlexusSecurityRealm
{
    public static String ROLE = PlexusSecurityRealm.class.getName();

    public PlexusSecurityRealm createSecurityRealm( String id, String authenticator, String authorizer )
        throws PlexusSecurityRealmException;

    public PlexusSecurityRealm getSecurityRealm( String id )
        throws PlexusSecurityRealmException;

    public boolean isAuthenticated( Map tokens ) throws AuthenticationException;

    public boolean isAuthorized( PlexusSecuritySession session,  Map tokens ) throws AuthorizationException;

    public PlexusSecuritySession authenticate( Map tokens ) throws NotAuthenticatedException, AuthenticationException;


    public void setAuthenticator( Authenticator authenticator );

    public void setAuthorizer( Authorizer authorizer );

}
