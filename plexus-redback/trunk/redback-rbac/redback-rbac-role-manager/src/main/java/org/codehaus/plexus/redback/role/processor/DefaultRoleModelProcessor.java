package org.codehaus.plexus.redback.role.processor;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.redback.rbac.Operation;
import org.codehaus.plexus.redback.rbac.Permission;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.Resource;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.role.RoleProfileException;
import org.codehaus.plexus.redback.role.model.ModelOperation;
import org.codehaus.plexus.redback.role.model.ModelPermission;
import org.codehaus.plexus.redback.role.model.ModelResource;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.util.RoleModelUtils;
import org.codehaus.plexus.util.dag.CycleDetectedException;

/**
 * DefaultRoleModelProcessor: inserts the components of the model that can be populated into the rbac manager
 * 
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * 
 * @plexus.component role="org.codehaus.plexus.redback.role.processor.RoleModelProcessor" role-hint="default"
 */
public class DefaultRoleModelProcessor implements RoleModelProcessor
{
    /**
     * @plexus.requirement role-hint="cached"
     */
    private RBACManager rbacManager;

    private Map resourceMap = new HashMap();

    private Map operationMap = new HashMap();

    public void process( RedbackRoleModel model ) throws RoleProfileException
    {
        // must process resources and operations first, they are required for the
        // permissions in the roles to add in correctly
        processResources( model );
        processOperations( model );
        
        processRoles( model );
    }

    private void processResources( RedbackRoleModel model ) throws RoleProfileException
    {
        for ( Iterator i = model.getResources().iterator(); i.hasNext(); )
        {
            ModelResource profileResource = (ModelResource) i.next();

            try
            {
                if ( !rbacManager.resourceExists( profileResource.getName() ) )
                {

                    Resource resource = rbacManager.createResource( profileResource.getName() );
                    resource.setPermanent( profileResource.isPermanent() );
                    resource = rbacManager.saveResource( resource );

                    // store for use in permission creation
                    resourceMap.put( profileResource.getId(), resource );

                }
                else
                {
                    resourceMap.put( profileResource.getId(), rbacManager.getResource( profileResource.getName() ) );
                }
            }
            catch ( RbacManagerException e )
            {
                throw new RoleProfileException( "error creating resource '" + profileResource.getName() + "'", e );
            }
        }
    }

    private void processOperations( RedbackRoleModel model ) throws RoleProfileException
    {
        for ( Iterator i = model.getOperations().iterator(); i.hasNext(); )
        {
            ModelOperation profileOperation = (ModelOperation) i.next();

            try
            {
                if ( !rbacManager.operationExists( profileOperation.getName() ) )
                {

                    Operation operation = rbacManager.createOperation( profileOperation.getName() );
                    operation.setPermanent( profileOperation.isPermanent() );
                    operation.setDescription( profileOperation.getDescription() );
                    operation = rbacManager.saveOperation( operation );

                    // store for use in permission creation
                    operationMap.put( profileOperation.getId(), operation );

                }
                else
                {
                    operationMap.put( profileOperation.getId(), rbacManager.getOperation( profileOperation.getName() ) );
                }
            }
            catch ( RbacManagerException e )
            {
                throw new RoleProfileException( "error creating operation '" + profileOperation.getName() + "'", e );
            }
        }
    }

    private void processRoles( RedbackRoleModel model ) throws RoleProfileException
    {
        List sortedGraph;
        try
        {
            sortedGraph = RoleModelUtils.reverseTopologicalSortedRoleList( model );
        }
        catch ( CycleDetectedException e )
        {
            throw new RoleProfileException( "cycle detected: this should have been caught in validation", e );
        }
       
        for ( Iterator i = sortedGraph.iterator(); i.hasNext(); )
        {
            String roleId = (String) i.next();
            
            ModelRole roleProfile = RoleModelUtils.getModelRole( model, roleId );

            List permissions = processPermissions( roleProfile.getPermissions() );

            if ( !rbacManager.roleExists( roleProfile.getName() ) )
            {
                try
                {
                    Role role = rbacManager.createRole( roleProfile.getName() );
                    role.setDescription( roleProfile.getDescription() );
                    role.setPermanent( roleProfile.isPermanent() );
                    role.setAssignable( roleProfile.isAssignable() );

                    // add any permissions associated with this role
                    for ( Iterator j = permissions.iterator(); j.hasNext(); )
                    {
                        Permission permission = (Permission) j.next();

                        role.addPermission( permission );
                    }
                    
                    // add child roles to this role
                    if ( roleProfile.getChildRoles() != null )
                    {
                        for ( Iterator j = roleProfile.getChildRoles().iterator(); j.hasNext(); )
                        {
                            String childRoleId = (String)j.next();
                            ModelRole childRoleProfile = RoleModelUtils.getModelRole( model, childRoleId );
                            role.addChildRoleName( childRoleProfile.getName() );
                        }
                    }                    
                    
                    rbacManager.saveRole( role );
                    
                    // add link from parent roles to this new role
                    if ( roleProfile.getParentRoles() != null )
                    {
                        for ( Iterator j = roleProfile.getParentRoles().iterator(); j.hasNext(); )
                        {
                            String parentRoleId = (String)j.next();
                            ModelRole parentModelRole = RoleModelUtils.getModelRole( model, parentRoleId );
                            Role parentRole = rbacManager.getRole( parentModelRole.getName() );
                            parentRole.addChildRoleName( role.getName() );
                            rbacManager.saveRole( parentRole );                                                    
                        } 
                    }
                  
                        
                }
                catch ( RbacManagerException e )
                {
                    throw new RoleProfileException( "error creating role '" + roleProfile.getName() + "'", e );
                }
            }
        }
    }
    
    

    private List processPermissions( List permissions ) throws RoleProfileException
    {
        List rbacPermissions = new ArrayList();

        for ( Iterator i = permissions.iterator(); i.hasNext(); )
        {
            ModelPermission profilePermission = (ModelPermission) i.next();

            try
            {
                if ( !rbacManager.permissionExists( profilePermission.getName() ) )
                {

                    Permission permission = rbacManager.createPermission( profilePermission.getName() );

                    // get the operation out of the map we stored it in when we created it _by_ the id in the model
                    Operation operation = (Operation) operationMap.get( profilePermission.getOperation() );
                    // same with resource
                    Resource resource = (Resource) resourceMap.get( profilePermission.getResource() );
                    
                    permission.setOperation( operation );
                    permission.setResource( resource );
                    permission.setPermanent( profilePermission.isPermanent() );
                    permission.setDescription( profilePermission.getDescription() );

                    permission = rbacManager.savePermission( permission );

                    rbacPermissions.add( permission );

                }
                else
                {
                    rbacPermissions.add( rbacManager.getPermission( profilePermission.getName() ) );
                }
            }
            catch ( RbacManagerException e )
            {
                throw new RoleProfileException( "error creating permission '" + profilePermission.getName() + "'", e );
            }
        }
        return rbacPermissions;
    }
}
