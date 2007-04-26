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
import org.codehaus.plexus.redback.role.model.ProfileOperation;
import org.codehaus.plexus.redback.role.model.ProfilePermission;
import org.codehaus.plexus.redback.role.model.ProfileResource;
import org.codehaus.plexus.redback.role.model.RedbackRbac;
import org.codehaus.plexus.redback.role.model.RoleProfile;
import org.codehaus.plexus.redback.role.model.io.stax.RedbackRoleProfilesStaxReader;

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
    private RedbackRbac mergedModel;
    
	public void loadRoleProfiles( String resource ) throws RoleProfileException 
    {
        RedbackRoleProfilesStaxReader reader = new RedbackRoleProfilesStaxReader();
        
        try
        {
            RedbackRbac roleProfiles = reader.read( resource );
            
            loadRoleProfiles( roleProfiles );
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
 
    public void loadRoleProfiles( RedbackRbac model ) throws RoleProfileException 
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
    
    private void processResources( RedbackRbac model ) throws RoleProfileException
    {
        for ( Iterator i = model.getResources().iterator(); i.hasNext(); )
        {
            ProfileResource profileResource = (ProfileResource)i.next();
            
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
    
    private void processOperations( RedbackRbac model ) throws RoleProfileException
    {
        for ( Iterator i = model.getOperations().iterator(); i.hasNext(); )
        {
            ProfileOperation profileOperation = (ProfileOperation)i.next();
            
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
    
    private void processRoles( RedbackRbac model ) throws RoleProfileException
    {
        for ( Iterator i = model.getRoleProfiles().iterator(); i.hasNext(); )
        {
            RoleProfile roleProfile = (RoleProfile)i.next();
            
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
            ProfilePermission profilePermission = (ProfilePermission)i.next();
            
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
            ProfilePermission profilePermission = (ProfilePermission)i.next();
            
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
    private void combineModels( RedbackRbac newModel ) throws RoleProfileException
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
