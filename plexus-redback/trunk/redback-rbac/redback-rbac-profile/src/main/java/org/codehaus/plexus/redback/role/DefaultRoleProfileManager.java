package org.codehaus.plexus.redback.role;

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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.codehaus.plexus.redback.rbac.Operation;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.Resource;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.role.model.ModelOperation;
import org.codehaus.plexus.redback.role.model.ModelPermission;
import org.codehaus.plexus.redback.role.model.ModelResource;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.model.io.stax.RedbackRoleModelStaxReader;

/**
 * RoleProfileManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * 
 * @plexus.component role="org.codehaus.plexus.redback.role.RoleProfileManager"
 *   role-hint="default"
 */
public class DefaultRoleProfileManager implements RoleProfileManager {

    /**
     * @plexus.requirement role-hint="cached"
     */
    private RBACManager rbacManager;
	
    /**
     * the merged model that can be validated as complete
     */
    private RedbackRoleModel mergedModel;
    
	public void loadRoleModel( String resource ) throws RoleProfileException 
    {
        RedbackRoleModelStaxReader reader = new RedbackRoleModelStaxReader();
        
        try
        {
            RedbackRoleModel roleProfiles = reader.read( resource );
            
            loadRoleModel( roleProfiles );
        }
        catch ( MalformedURLException e )
        {
            throw new RoleProfileException( "error locating redback profile", e );
        }
        catch ( IOException e )
        {
            throw new RoleProfileException( "error reading redback profile", e );
        }
        catch ( XMLStreamException e )
        {
            throw new RoleProfileException( "error parsing redback profile", e );
        }
    }
 
    public void loadRoleModel( RedbackRoleModel model ) throws RoleProfileException 
    {
        if ( mergedModel == null )
        {
            mergedModel = model;
        }
        else
        {
            combineModels( model );
            validate();
        }
        
        processResources( model );
        processOperations( model );
        processRoles( model );
    }
	
    /**
     * create a role for the given roleName using the resource passed in for resolving the
     * ${resource} expression
     * 
     */
    public void createRole( String roleName, String resource ) throws RoleProfileException
    {
        
    }

    /**
     * remove the role corresponding to the role using the resource passed in for resolving the
     * ${resource} expression
     * 
     */
    public void removeRole( String roleId, String resource ) throws RoleProfileException
    {
        
    }
    
    private void processResources( RedbackRoleModel model ) throws RoleProfileException
    {
        for ( Iterator i = model.getResources().iterator(); i.hasNext(); )
        {
            ModelResource profileResource = (ModelResource)i.next();
            
            if ( !rbacManager.resourceExists( profileResource.getName() ) )
            {
                try
                {
                    Resource resource = rbacManager.createResource( profileResource.getName() );
                    resource.setPermanent( profileResource.isPermanent() );
                    rbacManager.saveResource( resource );
                }
                catch ( RbacManagerException e )
                {
                    throw new RoleProfileException ( "error creating resource '" + profileResource.getName() + "'", e );
                }
            }
        }
    }
    
    private void processOperations( RedbackRoleModel model ) throws RoleProfileException
    {
        for ( Iterator i = model.getOperations().iterator(); i.hasNext(); )
        {
            ModelOperation profileOperation = (ModelOperation)i.next();
            
            if ( !rbacManager.operationExists( profileOperation.getName() ) )
            {
                try
                {
                    Operation operation = rbacManager.createOperation( profileOperation.getName() );
                    operation.setPermanent( profileOperation.isPermanent() );
                    operation.setDescription( profileOperation.getDescription() );
                    rbacManager.saveOperation( operation );
                    
                }
                catch ( RbacManagerException e )
                {
                    throw new RoleProfileException ( "error creating resource '" + profileOperation.getName() + "'", e );
                }
            }
        }
    }
    
    private void processRoles( RedbackRoleModel model ) throws RoleProfileException
    {
        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole roleProfile = (ModelRole)i.next();
            
            processPermissions( roleProfile.getPermissions() );
            
            if ( !rbacManager.roleExists( roleProfile.getName() ) )
            {
                try
                {
                    Role role = rbacManager.createRole( roleProfile.getName() );
                    role.setDescription( roleProfile.getDescription() );
                    role.setPermanent( roleProfile.isPermanent() );
                    role.setAssignable( roleProfile.isAssignable() );
                    rbacManager.saveRole( role );
                }
                catch ( RbacManagerException e )
                {
                    throw new RoleProfileException ( "error creating resource '" + roleProfile.getName() + "'", e );
                }
            }
        }
    }
    
    private void processPermissions( List permissions ) throws RoleProfileException
    {
        for ( Iterator i = permissions.iterator(); i.hasNext(); )
        {
            ModelPermission profilePermission = (ModelPermission)i.next();
            
            if ( !rbacManager.permissionExists( profilePermission.getName() ) )
            {

            }
        }
    }
    
    /**
     * process permissions using the passed in resource for resolving ${resource} expressions
     * 
     * @param permissions
     * @param resource
     * @throws RoleProfileException
     */
    private void processPermissions( List permissions, String resource ) throws RoleProfileException
    {
        for ( Iterator i = permissions.iterator(); i.hasNext(); )
        {
            ModelPermission profilePermission = (ModelPermission)i.next();
            
            if ( rbacManager.permissionExists( profilePermission.getName() ) )
            {
                
            }
        }
    }
    
    /**
     * merges the model passed in with the existing rbac model
     * 
     * @param newModel
     * @throws RoleProfileException
     */
    private void combineModels( RedbackRoleModel newModel ) throws RoleProfileException
    {
        
    }
    
    /**
     * validates the rbac merged model as complete
     * 
     * @throws RoleProfileException
     */
    private void validate() throws RoleProfileException
    {
        
    }
}
