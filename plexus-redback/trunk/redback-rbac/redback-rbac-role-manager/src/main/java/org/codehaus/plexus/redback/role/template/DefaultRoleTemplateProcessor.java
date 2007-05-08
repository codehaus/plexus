package org.codehaus.plexus.redback.role.template;

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
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.redback.rbac.Operation;
import org.codehaus.plexus.redback.rbac.Permission;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.Resource;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.role.RoleManagerException;
import org.codehaus.plexus.redback.role.model.ModelOperation;
import org.codehaus.plexus.redback.role.model.ModelPermission;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.ModelTemplate;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.util.RoleModelUtils;

/**
 * DefaultRoleTemplateProcessor: inserts the components of a template into the rbac manager
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * 
 * @plexus.component role="org.codehaus.plexus.redback.role.template.RoleTemplateProcessor"
 *   role-hint="default"
 */
public class DefaultRoleTemplateProcessor implements RoleTemplateProcessor
{
    /**
     * @plexus.requirement role-hint="cached"
     */
    private RBACManager rbacManager;
    
      
    public void create( RedbackRoleModel model, String templateId, String resource ) throws RoleManagerException
    {
        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate)i.next();
            
            if ( templateId.equals( template.getId() ) )
            {
                // resource can be special
                processResource( template, resource );
               
                // templates are roles that have yet to be paired with a resource for creation
                processTemplate( model, template, resource );
                
                return;
            }
        }
        
        throw new RoleManagerException( "unknown template '" + templateId + "'");
    }
    
    public void remove( RedbackRoleModel model, String templateId, String resource ) throws RoleManagerException
    {
        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate)i.next();
            
            if ( templateId.equals( template.getId() ) )
            {   
                removeTemplatedRole( model, template, resource );
                return;
            }
        }
        
        throw new RoleManagerException( "unknown template '" + templateId + "'");
    }
    
    
    private void removeTemplatedRole( RedbackRoleModel model, ModelTemplate template, String resource )
        throws RoleManagerException
    {
        String roleName = template.getNamePrefix() + template.getDelimiter() + resource;

        try
        {
            Role role = rbacManager.getRole( roleName );            
            
            if ( !role.isPermanent() )
            {                        
                // remove the role
                rbacManager.removeRole( role );
                
                // remove the permissions
                for ( Iterator i = template.getPermissions().iterator(); i.hasNext(); )
                {
                    ModelPermission permission = (ModelPermission)i.next();
                    if ( !permission.isPermanent() )
                    {
                        rbacManager.removePermission( permission.getName() + template.getDelimiter() + resource );
                    }
                }
                
                // check if we want to remove the resources
                Resource rbacResource = rbacManager.getResource( resource );
                
                if ( !rbacResource.isPermanent() )
                {
                    rbacManager.removeResource( rbacResource );
                }                             
            }
            else
            {
                throw new RoleManagerException( "unable to remove role, it is flagged permanent" );
            }
        }
        catch ( RbacManagerException e )
        {
            throw new RoleManagerException( "unable to remove role: " + roleName, e );
        }
    }
    
    
    private void processResource( ModelTemplate template, String resource ) throws RoleManagerException
    {
        if ( !rbacManager.resourceExists( resource ) )
        {
            try
            {
                Resource res = rbacManager.createResource( resource );
                res.setPermanent( template.isPermanentResource() );
                rbacManager.saveResource( res );
            }
            catch ( RbacManagerException e )
            {
                throw new RoleManagerException( "error creating resource '" + resource + "'", e );
            }
        }
    }
    
    
    private void processTemplate( RedbackRoleModel model, ModelTemplate template, String resource ) throws RoleManagerException
    {
        String templateName = template.getNamePrefix() + template.getDelimiter() + resource;
        
        List permissions = processPermissions( model, template, resource );

        if ( !rbacManager.roleExists( templateName ) )
        {
            try
            {
                Role role = rbacManager.createRole( templateName );
                role.setDescription( template.getDescription() );
                role.setPermanent( template.isPermanent() );
                role.setAssignable( template.isAssignable() );
                
                // add any permissions associated with this role
                for ( Iterator j = permissions.iterator(); j.hasNext(); )
                {
                    Permission permission = (Permission) j.next();

                    role.addPermission( permission );
                }
                
                // add child roles to this role
                if ( template.getChildRoles() != null )
                {
                    for ( Iterator j = template.getChildRoles().iterator(); j.hasNext(); )
                    {
                        String childRoleId = (String) j.next();
                        ModelRole childRoleProfile = RoleModelUtils.getModelRole( model, childRoleId );
                        role.addChildRoleName( childRoleProfile.getName() );
                    }
                }  
                
                // add child templates to this role, be nice and make them if they don't exist
                if ( template.getChildTemplates() != null )
                {
                    for ( Iterator j = template.getChildTemplates().iterator(); j.hasNext(); )
                    {
                        String childTemplateId = (String)j.next();
                        ModelTemplate childModelTemplate = RoleModelUtils.getModelTemplate( model, childTemplateId );
                        
                        if ( childModelTemplate == null )
                        {
                            throw new RoleManagerException( "error obtaining child template from model: template " + templateName + " # child template: " + childTemplateId );                           
                        }
                        
                        String childRoleName = childModelTemplate.getNamePrefix() + childModelTemplate.getDelimiter() + resource;
                        
                        // check if the role exists, if it does then add it as a child, otherwise make it and add it
                        // this should be safe since validation should protect us from template cycles
                        if ( rbacManager.roleExists( childRoleName ) )
                        {
                            role.addChildRoleName( childRoleName ); 
                        }
                        else
                        {
                            processTemplate( model, childModelTemplate, resource );
                            
                            role.addChildRoleName( childRoleName );
                        }                      
                    }
                }
                
                // this role needs to be saved since it now needs to be added as a child role by 
                // another role
                rbacManager.saveRole( role );
                
                // add link from parent roles to this new role
                if ( template.getParentRoles() != null )
                {
                    for ( Iterator j = template.getParentRoles().iterator(); j.hasNext(); )
                    {
                        String parentRoleId = (String)j.next();
                        ModelRole parentModelRole = RoleModelUtils.getModelRole( model, parentRoleId );
                        Role parentRole = rbacManager.getRole( parentModelRole.getName() );
                        parentRole.addChildRoleName( role.getName() );
                        rbacManager.saveRole( parentRole );                                                    
                    } 
                }
                
                // add child templates to this role, be nice and make them if they don't exist
                if ( template.getParentTemplates() != null )
                {
                    for ( Iterator j = template.getParentTemplates().iterator(); j.hasNext(); )
                    {
                        String parentTemplateId = (String)j.next();
                        ModelTemplate parentModelTemplate = RoleModelUtils.getModelTemplate( model, parentTemplateId );
                        
                        if ( parentModelTemplate == null )
                        {
                            throw new RoleManagerException( "error obtaining parent template from model: template " + templateName + " # child template: " + parentTemplateId );                           
                        }
                        
                        String parentRoleName = parentModelTemplate.getNamePrefix() + parentModelTemplate.getDelimiter() + resource;
                        
                        // check if the role exists, if it does then add it as a child, otherwise make it and add it
                        // this should be safe since validation should protect us from template cycles
                        if ( rbacManager.roleExists( parentRoleName ) )
                        {
                            Role parentRole = rbacManager.getRole( parentRoleName );
                            
                            parentRole.addChildRoleName( role.getName() );
                            rbacManager.saveRole( parentRole );
                        }
                        else
                        {
                            processTemplate( model, parentModelTemplate, resource );
                            
                            Role parentRole = rbacManager.getRole( parentRoleName );
                            
                            parentRole.addChildRoleName( role.getName() );
                            rbacManager.saveRole( parentRole );
                        }                      
                    }
                }
                
            }
            catch ( RbacManagerException e )
            {
                throw new RoleManagerException( "error creating role '" + templateName + "'", e );
            }
        }

    }
    
    private List processPermissions( RedbackRoleModel model, ModelTemplate template, String resource ) throws RoleManagerException
    {
        List rbacPermissions = new ArrayList();        
        
        if ( template.getPermissions() != null )
        {
            for ( Iterator i = template.getPermissions().iterator(); i.hasNext(); )
            {
                ModelPermission profilePermission = (ModelPermission) i.next();
                String permissionName = profilePermission.getName() + template.getDelimiter() + resource;

                if ( !rbacManager.permissionExists( permissionName ) )
                {

                    try
                    {
                        Permission permission = rbacManager.createPermission( permissionName );

                        ModelOperation modelOperation =
                            RoleModelUtils.getModelOperation( model, profilePermission.getOperation() );
                        Operation rbacOperation = rbacManager.getOperation( modelOperation.getName() );

                        Resource rbacResource = rbacManager.getResource( resource );

                        permission.setOperation( rbacOperation );
                        permission.setResource( rbacResource );
                        permission.setPermanent( profilePermission.isPermanent() );
                        permission.setDescription( profilePermission.getDescription() );

                        permission = rbacManager.savePermission( permission );

                        rbacPermissions.add( permission );

                    }
                    catch ( RbacManagerException e )
                    {
                        throw new RoleManagerException( "unable to create permission: " + permissionName );
                    }
                }
                else
                {
                    try
                    {
                        rbacPermissions.add( rbacManager.getPermission( permissionName ) );
                    }
                    catch ( RbacManagerException e )
                    {
                        throw new RoleManagerException( "unable to get permission: " + permissionName );
                    }
                }
            }
        }
        
        return rbacPermissions;
    }
}
