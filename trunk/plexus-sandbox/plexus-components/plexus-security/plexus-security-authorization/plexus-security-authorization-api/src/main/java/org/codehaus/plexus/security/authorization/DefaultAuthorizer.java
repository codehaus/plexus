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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.user.User;

import java.util.Set;

/**
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * @plexus.component role="org.codehaus.plexus.security.authorization.Authorizer"
 * role-hint="default"
 */
public class DefaultAuthorizer
    extends AbstractLogEnabled
    implements Authorizer
{

    /**
     * @ plexus.requirement
     */
   // private AuthorizationStore store;

    public AuthorizationResult isAuthorized( AuthorizationDataSource source )
        throws AuthorizationException
    {
        Object principal = source.getPrincipal();

        User user = source.getUser();

        return new AuthorizationResult( true, principal, null );
    }

    public Set getRoles( Object principal )
    {
        return null;//store.getRoles( principal );
    }
}

