package org.codehaus.plexus.security.authorization.memory;


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


import org.codehaus.plexus.security.authorization.AuthorizationStore;
import org.codehaus.plexus.security.authorization.AuthorizationResult;
import org.codehaus.plexus.security.authorization.AuthorizationException;
import org.codehaus.plexus.security.authorization.AuthorizationDataSource;
import org.codehaus.plexus.security.system.SecuritySession;

import java.util.Set;

/**
 * SimpleAuthorizationStore:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.AuthorizationStore"
 *   role-hint="memory"
 */
public class SimpleAuthorizationStore
    implements AuthorizationStore
{
    public Set getRoles( Object principal )
    {
        return null;
    }

    public AuthorizationResult authorize( AuthorizationDataSource source )
        throws AuthorizationException
    {
        return null;
    }

    public AuthorizationResult authorize( SecuritySession session )
        throws AuthorizationException
    {
        return null;
    }
}
