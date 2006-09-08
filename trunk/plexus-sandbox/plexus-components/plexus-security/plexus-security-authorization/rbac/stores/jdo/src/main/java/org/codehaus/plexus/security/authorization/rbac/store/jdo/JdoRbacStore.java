package org.codehaus.plexus.security.authorization.rbac.store.jdo;

import org.codehaus.plexus.security.authorization.rbac.jdo.JdoOperation;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoPermission;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoResource;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoRole;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStore;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStoreException;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.jdo.PlexusJdoUtils;
import org.codehaus.plexus.jdo.PlexusStoreException;
import org.codehaus.plexus.jdo.PlexusObjectNotFoundException;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Copyright 2005 The Apache Software Foundation.
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
 * JdoRbacStore:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.authorization.rbac.store.RbacStore"
 *   role-hint="jdo"
 */
public class JdoRbacStore
    implements RbacStore, Initializable
{

    /**
     * @plexus.requirement
     */
    private JdoFactory jdoFactory;

    private PersistenceManagerFactory pmf;

    // ----------------------------------------------------------------------
    // Role methods
    // ----------------------------------------------------------------------
    public Role addRole( Role role )
        throws RbacStoreException
    {
        return (Role) addObject( role );
    }

    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------

    public Permission addPermission( int roleId, Permission permission )
        throws RbacStoreException
    {
        Role role = getRole( roleId );
        role.addPermission( permission );
        return (Permission) updateObject( role );
    }

    public List getPermissions( int roleId )
        throws RbacStoreException
    {
        Role role = getRole( roleId );
        return role.getPermissions();
    }

    public List getAllPermissions()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoPermission.class );
    }

    public Permission removePermissionFromRole( int roleId, int permissionId )
        throws RbacStoreException
    {
        Role role = getRole( roleId );
        Permission permission = getPermission( permissionId );
        role.removePermission( permission );
        return permission;
    }

    public Permission getPermission( int permissionId )
        throws RbacStoreException
    {
        return (Permission) getObjectById( JdoPermission.class, permissionId );
    }

    public Permission removePermission( int permissionId )
        throws RbacStoreException
    {
        Permission permission = getPermission( permissionId );
        return (Permission) removeObject( permission );
    }

    // ----------------------------------------------------------------------
    // Operation methods
    // ----------------------------------------------------------------------
    public List getAllOperations()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoOperation.class );
    }

    public Operation getOperation( int operationId )
        throws RbacStoreException
    {
        return (Operation) getObjectById( JdoOperation.class, operationId );
    }

    public Operation removeOperation( int operationId )
        throws RbacStoreException
    {
        Operation operation = getOperation( operationId );
        return (Operation) removeObject( operation );
    }

    // ----------------------------------------------------------------------
    // Resource methods
    // ----------------------------------------------------------------------
    public List getAllResources()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoResource.class );
    }

    public Resource getResource( int resourceId )
        throws RbacStoreException
    {
        return (Resource) getObjectById( JdoResource.class, resourceId );
    }

    public Resource removeResource( int resourceId )
        throws RbacStoreException
    {
        Resource resource = getResource( resourceId );
        return (Resource) removeObject( resource );
    }

    // ----------------------------------------------------------------------
    // User Assignment methods
    // ----------------------------------------------------------------------

    public List getRoleAssignments( String principal )
        throws RbacStoreException
    {
        UserAssignment ua = (UserAssignment) getObjectById( UserAssignment.class, principal );

        return ua.getRoles();
    }

    public Role addRoleAssignment( String principal, int roleId )
        throws RbacStoreException
    {
        UserAssignment ua = (UserAssignment) getObjectById( UserAssignment.class, principal );
        Role role = (Role) getObjectById( JdoRole.class, roleId );

        ua.addRole( role );
        return role;
    }

    public Role removeRoleAssignment( String principal, int roleId )
        throws RbacStoreException
    {
        UserAssignment ua = (UserAssignment) getObjectById( UserAssignment.class, principal );
        Role role = (Role) getObjectById( JdoRole.class, roleId );

        ua.removeRole( role );
        return role;
    }

    public List getAllRoles()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoRole.class );
    }

    public List getAssignableRoles()
        throws RbacStoreException
    {
        List allRoles = getAllObjectsDetached( JdoRole.class );
        List assignableRoles = new ArrayList();

        Iterator it = allRoles.iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            if ( role.isAssignable() )
            {
                assignableRoles.add( role );
            }
        }

        return assignableRoles;
    }

    public Role getRole( int roleId )
        throws RbacStoreException
    {
        return (Role) getObjectById( JdoRole.class, roleId );
    }

    public Role removeRole( int roleId )
        throws RbacStoreException
    {
        Role role = (Role) getObjectById( JdoRole.class, roleId );
        return removeRole( role );
    }

    public Role removeRole( Role role )
        throws RbacStoreException
    {
        return (Role) removeObject( role );
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        pmf = jdoFactory.getPersistenceManagerFactory();
    }

    // ----------------------------------------------------------------------
    // jdo utility methods
    // ----------------------------------------------------------------------

    private Object addObject( Object object )
    {
        return PlexusJdoUtils.addObject( getPersistenceManager(), object );
    }

    private Object getObjectById( Class clazz, Object id )
        throws RbacStoreException
    {
        // TODO: Update PlexusJdoUtils to support String or Object identity.
        if ( !( id instanceof Integer ) )
        {
            throw new RbacStoreException( "Object By Id only works with Integer IDs so far." );
        }

        Integer iid = (Integer) id;
        return getObjectById( clazz, iid.intValue() );
    }

    private Object getObjectById( Class clazz, int id )
        throws RbacStoreException
    {
        return getObjectById( clazz, id, null );
    }

    private Object getObjectById( Class clazz, int id, String fetchGroup )
        throws RbacStoreException
    {
        try
        {
            return PlexusJdoUtils.getObjectById( getPersistenceManager(), clazz, id, fetchGroup );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            // TODO make PlexusObjectNotFoundException runtime or change plexus not to wrap jdo exceptions
            throw new RuntimeException( e.getMessage() );
        }
        catch ( PlexusStoreException e )
        {
            throw new RbacStoreException( "Unable to get object '" + clazz.getName() + "', id '" + id
                + "', fetch-group '" + fetchGroup + "' from jdo store." );
        }
    }

    private List getAllObjectsDetached( Class clazz )
    {
        return getAllObjectsDetached( clazz, null );
    }

    private List getAllObjectsDetached( Class clazz, String fetchGroup )
    {
        return getAllObjectsDetached( clazz, null, fetchGroup );
    }

    private List getAllObjectsDetached( Class clazz, String ordering, String fetchGroup )
    {
        return PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), clazz, ordering, fetchGroup );
    }

    private Object removeObject( Object o )
    {
        PlexusJdoUtils.removeObject( getPersistenceManager(), o );
        return o;
    }

    private Object updateObject( Object object )
        throws RbacStoreException
    {
        try
        {
            return PlexusJdoUtils.updateObject( getPersistenceManager(), object );
        }
        catch ( PlexusStoreException e )
        {
            throw new RbacStoreException( "Unable to update the '" + object.getClass().getName()
                + "' object in the jdo database.", e );
        }
    }

    private PersistenceManager getPersistenceManager()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        pm.getFetchPlan().setMaxFetchDepth( -1 );

        return pm;
    }

    private void rollback( Transaction tx )
    {
        PlexusJdoUtils.rollbackIfActive( tx );
    }

}
