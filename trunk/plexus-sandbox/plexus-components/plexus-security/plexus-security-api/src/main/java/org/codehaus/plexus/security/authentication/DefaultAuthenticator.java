package org.codehaus.plexus.security.authentication;

import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.Authenticator;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.AuthenticationStore;

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
 * DefaultAuthenticator: a plain authenticator that just redirects the request straight to its authentication store.
 * This implementation should work for most self contained authentication systems but is provided as an interface
 * in case of paricular implementions having special requirements like multiple authentication stores that can't be
 * addressed by other means.
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   rolorg.codehaus.plexus.security.authentication.Authenticatortor"
 *   role-hint="default"
 */
public class DefaultAuthenticator
    implements Authenticator
{

    /**
     * @plexus.requirement
     */
    private AuthenticationStore authStore;

    public AuthenticationResult authenticate( Map tokens )
        throws AuthenticationException
    {
        return authStore.authenticate( tokens );
    }
}
