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

import org.codehaus.plexus.util.StringUtils;

import java.util.Iterator;

/**
 * RBACObjectAssertions 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class RBACObjectAssertions
{
    public static void assertValid( Role role )
        throws RbacObjectInvalidException
    {
        assertValid( null, role );
    }

    public static void assertValid( String scope, Role role )
        throws RbacObjectInvalidException
    {
        if ( role == null )
        {
            throw new RbacObjectInvalidException( scope, "Null Role object is invalid." );
        }

        if ( StringUtils.isEmpty( role.getName() ) )
        {
            throw new RbacObjectInvalidException( scope, "Role.name must not be empty." );
        }

        if ( role.getPermissions() == null )
        {
            throw new RbacObjectInvalidException( scope, "Role.permissions cannot be null." );
        }

        if ( role.getPermissions().isEmpty() )
        {
            throw new RbacObjectInvalidException( scope, "Role.permissions cannot be empty." );
        }

        int i = 0;
        Iterator it = role.getPermissions().iterator();
        while ( it.hasNext() )
        {
            Permission perm = (Permission) it.next();
            assertValid( "Role.permissions[" + i + "]", perm );
            i++;
        }

        i = 0;
        it = role.getChildRoles().iterator();
        while ( it.hasNext() )
        {
            Role child = (Role) it.next();
            assertValid( "Role.childRoles[" + i + "]", child );
            i++;
        }
    }

    public static void assertValid( Permission permission )
        throws RbacObjectInvalidException
    {
        assertValid( null, permission );
    }

    public static void assertValid( String scope, Permission permission )
        throws RbacObjectInvalidException
    {
        if ( permission == null )
        {
            throw new RbacObjectInvalidException( scope, "Null Permission object is invalid." );
        }

        if ( StringUtils.isEmpty( permission.getName() ) )
        {
            throw new RbacObjectInvalidException( scope, "Permission.name must not be empty." );
        }

        assertValid( "Permission.operation", permission.getOperation() );
        assertValid( "Permission.resource", permission.getResource() );

    }

    public static void assertValid( Operation operation )
        throws RbacObjectInvalidException
    {
        assertValid( null, operation );
    }

    public static void assertValid( String scope, Operation operation )
        throws RbacObjectInvalidException
    {
        if ( operation == null )
        {
            throw new RbacObjectInvalidException( scope, "Null Operation object is invalid." );
        }

        if ( StringUtils.isEmpty( operation.getName() ) )
        {
            throw new RbacObjectInvalidException( scope, "Operation.name must not be empty." );
        }
    }

    public static void assertValid( Resource resource )
        throws RbacObjectInvalidException
    {
        assertValid( null, resource );
    }

    public static void assertValid( String scope, Resource resource )
        throws RbacObjectInvalidException
    {
        if ( resource == null )
        {
            throw new RbacObjectInvalidException( scope, "Null Resource object is invalid." );
        }

        if ( StringUtils.isEmpty( resource.getIdentifier() ) )
        {
            throw new RbacObjectInvalidException( scope, "Resource.identifier must not be empty." );
        }
    }

    public static void assertValid( UserAssignment assignment )
        throws RbacObjectInvalidException
    {
        assertValid( null, assignment );
    }

    public static void assertValid( String scope, UserAssignment assignment )
        throws RbacObjectInvalidException
    {
        if ( assignment == null )
        {
            throw new RbacObjectInvalidException( scope, "Null UserAssigment object is invalid." );
        }

        if ( StringUtils.isEmpty( assignment.getPrincipal() ) )
        {
            throw new RbacObjectInvalidException( scope, "UserAssigment.principal cannot be empty." );
        }

        if ( assignment.getRoles() == null )
        {
            throw new RbacObjectInvalidException( scope, "UserAssignment.roles cannot be null." );
        }

        if ( assignment.getRoles().isEmpty() )
        {
            throw new RbacObjectInvalidException( scope, "UserAssignment.roles cannot be empty." );
        }

        int i = 0;
        Iterator it = assignment.getRoles().values().iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            assertValid( "UserAssignment.roles[" + i + "]", role );
            i++;
        }
    }

}
