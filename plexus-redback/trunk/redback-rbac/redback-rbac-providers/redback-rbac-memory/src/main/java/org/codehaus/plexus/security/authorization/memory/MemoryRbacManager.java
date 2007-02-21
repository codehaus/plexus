package org.codehaus.plexus.redback.authorization.memory;

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

import org.codehaus.plexus.redback.rbac.AbstractRBACManager;
import org.codehaus.plexus.redback.rbac.Operation;
import org.codehaus.plexus.redback.rbac.Permission;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RBACObjectAssertions;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.RbacObjectInvalidException;
import org.codehaus.plexus.redback.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.redback.rbac.RbacPermanentException;
import org.codehaus.plexus.redback.rbac.Resource;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.rbac.UserAssignment;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * MemoryRbacManager: a very quick and dirty implementation of a rbac store
 * <p/>
 * WARNING: not for actual usage, its not sound - jesse
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id:$
 * @plexus.component role="org.codehaus.plexus.redback.rbac.RBACManager"
 * role-hint="memory"
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

    public Role saveRole( Role role )
        throws RbacManagerException
    {
        RBACObjectAssertions.assertValid( "Save Role", role );

        triggerInit();

        roles.put( role.getName(), role );

        fireRbacRoleSaved( role );

        if ( role.getPermissions() != null )
        {
            Iterator it = role.getPermissions().iterator();
            while ( it.hasNext() )
            {
                savePermission( (Permission) it.next() );
            }
        }

        return role;
    }

    public void saveRoles( Collection roles )
        throws RbacObjectInvalidException, RbacManagerException
    {
        if ( roles == null )
        {
            // Nothing to do.
            return;
        }

        Iterator it = roles.iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            saveRole( role );
        }
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
        triggerInit();

        assertRoleExists( roleName );

        return (Role) roles.get( roleName );
    }

    public void removeRole( Role role )
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACObjectAssertions.assertValid( "Remove Role", role );

        if ( role.isPermanent() )
        {
            throw new RbacPermanentException( "Unable to delete permanent role [" + role.getName() + "]" );
        }

        assertRoleExists( role.getName() );

        fireRbacRoleRemoved( role );

        roles.remove( role.getName() );
    }

    public List getAllRoles()
        throws RbacManagerException
    {
        triggerInit();

        return Collections.unmodifiableList( new ArrayList( roles.values() ) );
    }

    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------

    public Operation saveOperation( Operation operation )
        throws RbacManagerException
    {
        triggerInit();

        RBACObjectAssertions.assertValid( "Save Operation", operation );

        operations.put( operation.getName(), operation );
        return operation;
    }

    public Permission savePermission( Permission permission )
        throws RbacManagerException
    {
        triggerInit();

        RBACObjectAssertions.assertValid( "Save Permission", permission );

        permissions.put( permission.getName(), permission );

        fireRbacPermissionSaved( permission );

        saveOperation( permission.getOperation() );
        saveResource( permission.getResource() );
        return permission;
    }

    public Resource saveResource( Resource resource )
        throws RbacManagerException
    {
        triggerInit();

        RBACObjectAssertions.assertValid( "Save Resource", resource );

        resources.put( resource.getIdentifier(), resource );
        return resource;
    }

    public UserAssignment saveUserAssignment( UserAssignment userAssignment )
        throws RbacManagerException
    {
        triggerInit();

        RBACObjectAssertions.assertValid( "Save UserAssignment", userAssignment );

        fireRbacUserAssignmentSaved( userAssignment );

        userAssignments.put( userAssignment.getPrincipal(), userAssignment );
        return userAssignment;
    }

    public Operation createOperation( String name )
        throws RbacManagerException
    {
        Operation operation;

        try
        {
            operation = getOperation( name );
        }
        catch ( RbacObjectNotFoundException e )
        {
            operation = new MemoryOperation();
            operation.setName( name );
        }

        return operation;
    }

    public Permission createPermission( String name )
        throws RbacManagerException
    {
        Permission permission;

        try
        {
            permission = getPermission( name );
        }
        catch ( RbacObjectNotFoundException e )
        {
            permission = new MemoryPermission();
            permission.setName( name );
        }

        return permission;
    }

    public Permission createPermission( String name, String operationName, String resourceIdentifier )
        throws RbacManagerException
    {
        Permission permission;

        try
        {
            permission = getPermission( name );

            if ( StringUtils.equals( operationName, permission.getOperation().getName() ) )
            {
                throw new RbacManagerException( "Attempted to create a permission named '" + name +
                    "' with an operation named '" + operationName + "', but that overides the existing '" + name +
                    "' permission with operation '" + permission.getOperation().getName() + "'" );
            }

        }
        catch ( RbacObjectNotFoundException e )
        {
            permission = new MemoryPermission();
            permission.setName( name );

            permission.setOperation( createOperation( operationName ) );
            permission.setResource( createResource( resourceIdentifier ) );
        }

        return permission;
    }

    public Resource createResource( String identifier )
        throws RbacManagerException
    {
        Resource resource;

        try
        {
            resource = getResource( identifier );
        }
        catch ( RbacObjectNotFoundException e )
        {
            resource = new MemoryResource();
            resource.setIdentifier( identifier );
        }

        return resource;
    }

    public Role createRole( String name )
    {
        Role role = new MemoryRole();
        role.setName( name );

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
        throws RbacObjectNotFoundException, RbacManagerException
    {
        triggerInit();

        assertPermissionExists( permissionName );

        return (Permission) permissions.get( permissionName );
    }

    public List getResources()
        throws RbacManagerException
    {
        triggerInit();

        return Collections.unmodifiableList( new ArrayList( resources.values() ) );
    }

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        RBACObjectAssertions.assertValid( "Remove Operation", operation );

        if ( operation.isPermanent() )
        {
            throw new RbacPermanentException( "Unable to delete permanent operation [" + operation.getName() + "]" );
        }

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

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        RBACObjectAssertions.assertValid( "Remove Permission", permission );

        if ( permission.isPermanent() )
        {
            throw new RbacPermanentException( "Unable to delete permanent permission [" + permission.getName() + "]" );
        }

        assertPermissionExists( permission.getName() );

        fireRbacPermissionRemoved( permission );

        permissions.remove( permission.getName() );
    }

    public void removeResource( Resource resource )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        RBACObjectAssertions.assertValid( "Remove Resource", resource );

        if ( resource.isPermanent() )
        {
            throw new RbacPermanentException(
                "Unable to delete permanent resource [" + resource.getIdentifier() + "]" );
        }

        assertResourceExists( resource.getIdentifier() );

        resources.remove( resource.getIdentifier() );
    }

    private void assertResourceExists( String resourceIdentifier )
        throws RbacObjectNotFoundException
    {
        if ( !resources.containsKey( resourceIdentifier ) )
        {
            throw new RbacObjectNotFoundException( "Resource '" + resourceIdentifier + "' not found." );
        }
    }

    private void assertUserAssignmentExists( String principal )
        throws RbacObjectNotFoundException
    {
        if ( !userAssignments.containsKey( principal ) )
        {
            throw new RbacObjectNotFoundException( "UserAssignment '" + principal + "' not found." );
        }
    }

    public void removeUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        RBACObjectAssertions.assertValid( "Remove User Assignment", userAssignment );

        if ( userAssignment.isPermanent() )
        {
            throw new RbacPermanentException(
                "Unable to delete permanent user assignment [" + userAssignment.getPrincipal() + "]" );
        }

        fireRbacUserAssignmentRemoved( userAssignment );

        assertUserAssignmentExists( userAssignment.getPrincipal() );

        userAssignments.remove( userAssignment.getPrincipal() );
    }

    public void eraseDatabase()
    {
        userAssignments.clear();
        resources.clear();
        operations.clear();
        permissions.clear();
        roles.clear();
    }

    public UserAssignment createUserAssignment( String principal )
        throws RbacManagerException
    {
        try
        {
            return getUserAssignment( principal );
        }
        catch ( RbacObjectNotFoundException e )
        {
            UserAssignment ua = new MemoryUserAssignment();
            ua.setPrincipal( principal );

            fireRbacUserAssignmentSaved( ua );

            return ua;
        }
    }

    public List getAllOperations()
        throws RbacManagerException
    {
        triggerInit();

        return Collections.unmodifiableList( new ArrayList( operations.values() ) );
    }

    public List getAllPermissions()
        throws RbacManagerException
    {
        triggerInit();

        return Collections.unmodifiableList( new ArrayList( permissions.values() ) );
    }

    public List getAllResources()
        throws RbacManagerException
    {
        triggerInit();

        return Collections.unmodifiableList( new ArrayList( resources.values() ) );
    }

    public List getAllUserAssignments()
        throws RbacManagerException
    {
        triggerInit();

        return Collections.unmodifiableList( new ArrayList( userAssignments.values() ) );
    }

    public List getUserAssignmentsForRoles( Collection roleNames )
        throws RbacManagerException
    {
        List allUA = getAllUserAssignments();
        List userAssignments = new ArrayList();

        for ( Iterator i = allUA.iterator(); i.hasNext(); )
        {
            UserAssignment ua = (UserAssignment) i.next();

            for ( Iterator j = roleNames.iterator(); j.hasNext(); )
            {
                String roleName = (String) j.next();

                if ( ua.getRoleNames().contains( roleName ) )
                {
                    userAssignments.add( ua );
                    break;
                }
            }
        }

        return userAssignments;
    }

    public UserAssignment getUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        triggerInit();

        assertUserAssignmentExists( principal );

        return (UserAssignment) userAssignments.get( principal );
    }

    public Operation getOperation( String operationName )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        triggerInit();

        assertOpertionExists( operationName );

        return (Operation) operations.get( operationName );
    }

    public Resource getResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        triggerInit();

        assertResourceExists( resourceIdentifier );

        return (Resource) resources.get( resourceIdentifier );
    }

    private boolean hasTriggeredInit = false;

    public void triggerInit()
    {
        if ( !hasTriggeredInit )
        {
            fireRbacInit( roles.isEmpty() );
            hasTriggeredInit = true;
        }
    }
}
