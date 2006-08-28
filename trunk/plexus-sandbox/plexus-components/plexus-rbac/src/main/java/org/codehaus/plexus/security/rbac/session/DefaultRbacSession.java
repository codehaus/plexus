package org.codehaus.plexus.security.rbac.session;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.codehaus.plexus.security.rbac.role.Role;
import org.codehaus.plexus.security.rbac.user.RbacUser;

import java.util.HashSet;
import java.util.Set;

/**
 * A default implementation of the IRbacSession interface.
 */
public class DefaultRbacSession
    extends AbstractRbacSession
{
    /**
     * The user of this session.
     */
    protected RbacUser user;
    /**
     * The active role set of this session.
     */
    protected Set roleSet = new HashSet();

    /**
     * Constructs with the given user.
     */
    public DefaultRbacSession( RbacUser user )
    {
        this.user = user;
    }

    /**
     * Adds the given role to the current active role set.
     */
    public boolean addActiveRole( Role r )
    {
        synchronized ( roleSet )
        {
            return roleSet.add( r );
        }
    }

    /**
     * Drops the given role from the current active role set.
     */
    public boolean dropActiveRole( Role r )
    {
        synchronized ( roleSet )
        {
            return roleSet.remove( r );
        }
    }

    /**
     * Returns the user of this session.
     */
    public RbacUser getUser()
    {
        return user;
    }

    /**
     * Returns the current active role set.
     */
    public Set getActiveRoles()
    {
        synchronized ( roleSet )
        {
            return roleSet;
        }
    }
}
