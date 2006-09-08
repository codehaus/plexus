package org.codehaus.plexus.security.rbac;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * AbstractRoles useful for common logic that implementors can use.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractRoles
    implements Roles
{
    public boolean hasRoles()
    {
        return ( getRoles() != null ) && ( !getRoles().isEmpty() );
    }

    public boolean isEmpty()
    {
        return ( getRoles() == null ) || ( getRoles().isEmpty() );
    }

    public Iterator iterator()
    {
        if ( getRoles() == null )
        {
            return Collections.EMPTY_LIST.iterator();
        }

        return getRoles().iterator();
    }

    public Role getRole( int roleId )
        throws RbacObjectNotFoundException
    {
        Iterator it = iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            if ( role.getId() == roleId )
            {
                return role;
            }
        }

        // If we got here, we didn't find it.
        throw new RbacObjectNotFoundException( "Role " + roleId + " not found." );
    }

    public List flattenRoleHierarchy()
    {
        List ret = new ArrayList();

        recursiveAddRole( ret, this );

        return ret;
    }

    private void recursiveAddRole( List ret, Roles roles )
    {
        Iterator it = roles.iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            if ( !ret.contains( role ) )
            {
                ret.add( role );
            }

            if ( role.hasChildRoles() )
            {
                recursiveAddRole( ret, role.getChildRoles() );
            }
        }
    }

    protected void assertNoCyclicRoleHierarchy()
    {
        // TODO: Write plexus-utils/DAG cyclic prevention code.
    }
}
