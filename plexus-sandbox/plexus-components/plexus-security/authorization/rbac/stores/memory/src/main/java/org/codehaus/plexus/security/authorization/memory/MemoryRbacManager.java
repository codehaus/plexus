package org.codehaus.plexus.security.authorization.memory;

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

import org.codehaus.plexus.security.rbac.AbstractRBACManager;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RBACObjectAssertions;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MemoryRbacManager: a very quick and dirty implementation of a rbac store
 *
 * WARNING: not for actual usage, its not sound - jesse
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.rbac.RBACManager"
 *   role-hint="memory"
 */
public class MemoryRbacManager
    extends AbstractRBACManager
    implements RBACManager
{
    private Map roles = new HashMap();

    private Map permissions = new HashMap();

    private Map operations = new HashMap();

    private Map resources = new HashMap();

    private Map userAssignments = new HashMap();

    // ----------------------------------------------------------------------
    // Role methods
    // ----------------------------------------------------------------------

    public Role addRole( Role role )
        throws RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Add Role", role );

        return (Role) roles.put( role.getName(), role );
    }

    private void assertRoleExists( String roleName )
        throws RbacObjectNotFoundException
    {
        if ( !roles.containsKey( roleName ) )
        {
            throw new RbacObjectNotFoundException( "Role '" + roleName + "' does not exist." );
        }
    }

    public Role getRole( String roleName )
        throws RbacObjectNotFoundException
    {
        assertRoleExists( roleName );

        return (Role) roles.get( roleName );
    }

    public void removeRole( Role role )
        throws RbacStoreException, RbacObjectNotFoundException
    {
        RBACObjectAssertions.assertValid( "Remove Role", role );

        assertRoleExists( role.getName() );

        roles.remove( role.getName() );
    }

    public Role updateRole( Role role )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Update Role", role );

        assertRoleExists( role.getName() );

        return (Role) roles.put( role.getName(), role );
    }

    public List getAllRoles()
        throws RbacStoreException
    {
        return Collections.unmodifiableList( new ArrayList( roles.values() ) );
    }

    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------

    public Operation addOperation( Operation operation )
        throws RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Add Operation", operation );

        return (Operation) operations.put( operation.getName(), operation );
    }

    public Permission addPermission( Permission permission )
        throws RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Add Permission", permission );

        return (Permission) permissions.put( permission.getName(), permission );
    }

    public Resource addResource( Resource resource )
        throws RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Add Resource", resource );

        return (Resource) resources.put( resource.getIdentifier(), resource );
    }

    public UserAssignment addUserAssignment( UserAssignment userAssignment )
        throws RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Add UserAssignment", userAssignment );

        return (UserAssignment) userAssignments.put( userAssignment.getPrincipal(), userAssignment );
    }

    public Operation createOperation( String name, String description )
    {
        Operation operation = new MemoryOperation();
        operation.setName( name );
        operation.setDescription( description );

        return operation;
    }

    public Permission createPermission( String name, String description )
    {
        Permission permission = new MemoryPermission();
        permission.setName( name );
        permission.setDescription( description );

        return permission;
    }

    public Permission createPermission( String name, String description, String operationName, String resourceIdentifier )
    {
        Permission permission = new MemoryPermission();
        permission.setName( name );
        permission.setDescription( description );

        Operation operation = new MemoryOperation();
        operation.setName( operationName );

        permission.setOperation( operation );

        Resource resource = new MemoryResource();
        resource.setIdentifier( resourceIdentifier );

        permission.setResource( resource );

        return permission;
    }

    public Resource createResource( String identifier )
    {
        Resource resource = new MemoryResource();
        resource.setIdentifier( identifier );

        return resource;
    }

    public Role createRole( String name, String description )
    {
        Role role = new MemoryRole();
        role.setName( name );
        role.setDescription( description );

        return role;
    }

    private void assertPermissionExists( String permissionName )
        throws RbacObjectNotFoundException
    {
        if ( !permissions.containsKey( permissionName ) )
        {
            throw new RbacObjectNotFoundException( "Permission '" + permissionName + "' does not exist." );
        }
    }

    public Permission getPermission( String permissionName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        assertPermissionExists( permissionName );

        return (Permission) permissions.get( permissionName );
    }

    public List getResources()
        throws RbacStoreException
    {
        return Collections.unmodifiableList( new ArrayList( resources.values() ) );
    }

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Remove Operation", operation );

        assertOpertionExists( operation.getName() );

        operations.remove( operation.getName() );
    }

    private void assertOpertionExists( String operationName )
        throws RbacObjectNotFoundException
    {
        if ( !operations.containsKey( operationName ) )
        {
            throw new RbacObjectNotFoundException( "Operation '" + operationName + "' not found." );
        }
    }

    public Operation updateOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Update Operation", operation );

        assertOpertionExists( operation.getName() );

        return (Operation) operations.put( operation.getName(), operation );
    }

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Remove Permission", permission );

        assertPermissionExists( permission.getName() );

        permissions.remove( permission.getName() );
    }

    public Permission updatePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Update Permission", permission );

        assertPermissionExists( permission.getName() );

        return (Permission) permissions.put( permission.getName(), permission );
    }

    public void removeResource( Resource resource )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Remove Resource", resource );

        assertResourceExists( resource.getIdentifier() );

        resources.remove( resource.getIdentifier() );
    }

    private void assertResourceExists( String resourceIdentifier )
        throws RbacObjectNotFoundException
    {
        if ( resources.containsKey( resourceIdentifier ) )
        {
            throw new RbacObjectNotFoundException( "Resource '" + resourceIdentifier + "' not found." );
        }
    }

    public Resource updateResource( Resource resource )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Update Resource", resource );

        assertResourceExists( resource.getIdentifier() );

        return (Resource) resources.put( resource.getIdentifier(), resource );
    }

    private void assertUserAssignmentExists( String principal )
        throws RbacObjectNotFoundException
    {
        if ( userAssignments.containsKey( principal ) )
        {
            throw new RbacObjectNotFoundException( "UserAssignment '" + principal + "' not found." );
        }
    }

    public void removeUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Remove User Assignment", userAssignment );

        assertUserAssignmentExists( userAssignment.getPrincipal() );

        userAssignments.remove( userAssignment.getPrincipal() );
    }

    public UserAssignment updateUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( "Remove User Assignment", userAssignment );

        assertUserAssignmentExists( userAssignment.getPrincipal() );

        return (UserAssignment) userAssignments.put( userAssignment.getPrincipal(), userAssignment );
    }

    public UserAssignment createUserAssignment( String principal )
    {
        UserAssignment ua = new MemoryUserAssignment();
        ua.setPrincipal( principal );

        return ua;
    }

    public List getAllOperations()
        throws RbacStoreException
    {
        return Collections.unmodifiableList( new ArrayList( operations.values() ) );
    }

    public List getAllPermissions()
        throws RbacStoreException
    {
        return Collections.unmodifiableList( new ArrayList( permissions.values() ) );
    }

    public List getAllResources()
        throws RbacStoreException
    {
        return Collections.unmodifiableList( new ArrayList( resources.values() ) );
    }

    public List getAllUserAssignments()
        throws RbacStoreException
    {
        return Collections.unmodifiableList( new ArrayList( userAssignments.values() ) );
    }

    public UserAssignment getUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        assertUserAssignmentExists( principal );

        return (UserAssignment) userAssignments.get( principal );
    }

    public Operation getOperation( String operationName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        assertOpertionExists( operationName );

        return (Operation) operations.get( operationName );
    }

    public Resource getResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        assertResourceExists( resourceIdentifier );

        return (Resource) resources.get( resourceIdentifier );
    }
}
