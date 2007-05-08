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
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.rbac.UserAssignment;
import org.codehaus.plexus.redback.role.merger.RoleModelMerger;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.ModelTemplate;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.model.io.stax.RedbackRoleModelStaxReader;
import org.codehaus.plexus.redback.role.processor.RoleModelProcessor;
import org.codehaus.plexus.redback.role.template.RoleTemplateProcessor;
import org.codehaus.plexus.redback.role.util.RoleModelUtils;
import org.codehaus.plexus.redback.role.validator.RoleModelValidator;

/**
 * RoleProfileManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * 
 * @plexus.component role="org.codehaus.plexus.redback.role.RoleManager"
 *   role-hint="default"
 *   instantiation-strategy="singleton"
 */
public class DefaultRoleManager extends AbstractLogEnabled implements RoleManager, Initializable {

    /**
     * the blessed model that has been validated as complete
     */
    private RedbackRoleModel blessedModel;
   
    /**
     * the merged model that can be validated as complete
     */
    private RedbackRoleModel mergedModel;
    
    /**
     * a map of the resources, and the model that they loaded
     */
    private Map knownResources = new HashMap();
    
    /**
     * @plexus.requirement role-hint="default"
     */
    private RoleModelMerger modelMerger;
    
    /**
     * @plexus.requirement role-hint="default"
     */
    private RoleModelValidator modelValidator;
    
    /**
     * @plexus.requirement role-hint="default"
     */
    private RoleModelProcessor modelProcessor;
    
    /**
     * @plexus.requirement role-hint="default"
     */
    private RoleTemplateProcessor templateProcessor;
    
    /**
     * @plexus.requirement role-hint="cached"
     */
    private RBACManager rbacManager;
    
    /**
     * @plexus.requirement
     */
    private PlexusContainer container;
    
	public void loadRoleModel( URL resource ) throws RoleManagerException 
    {
        RedbackRoleModelStaxReader reader = new RedbackRoleModelStaxReader();
        
        try
        {
            RedbackRoleModel roleModel = reader.read( new InputStreamReader( resource.openStream() ) );
            
            boolean loaded = false; 
            
            for ( Iterator i = knownResources.keySet().iterator(); i.hasNext(); )
            {
                String applicationName = (String)i.next();
                
                if ( applicationName.equals( roleModel.getApplication() ) )
                {
                    loaded = true;
                }
            }
            
            if ( !loaded )
            {
                knownResources.put( roleModel.getApplication(), roleModel );
            
                loadRoleModel( roleModel );
            }
        }
        catch ( MalformedURLException e )
        {
            throw new RoleManagerException( "error locating redback profile", e );
        }
        catch ( IOException e )
        {
            throw new RoleManagerException( "error reading redback profile", e );
        }
        catch ( XMLStreamException e )
        {
            throw new RoleManagerException( "error parsing redback profile", e );
        }
    }
 
    public void loadRoleModel( RedbackRoleModel model ) throws RoleManagerException 
    {
        if ( mergedModel == null )
        {
            mergedModel = model;
            
            if ( modelValidator.validate( mergedModel ) )
            {
                blessedModel = mergedModel;
            }
            else
            {
                List validationErrors = modelValidator.getValidationErrors();

                getLogger().error( "Validation Error: " + model.getApplication() );
                
                for ( Iterator i = validationErrors.iterator(); i.hasNext(); )
                {                                  
                    getLogger().error( (String)i.next() );
                }
                
                throw new RoleManagerException( "Validation Error: " + mergedModel.getApplication() );
            }
        }
        else
        {
            mergedModel = modelMerger.merge( blessedModel, model );
            
            if ( modelMerger.hasMergeErrors() )
            {
                List mergeErrors = modelMerger.getMergeErrors();
                
                getLogger().error( "Merge Error: " + mergedModel.getApplication() );
                
                for ( Iterator i = mergeErrors.iterator(); i.hasNext(); )
                {                                  
                    getLogger().error( (String)i.next() );
                }
                
                throw new RoleManagerException( "Merge Error: " + mergedModel.getApplication() );
            }
            
            if ( modelValidator.validate( mergedModel ) )
            {
                blessedModel = mergedModel;
            }
            else
            {
                List validationErrors = modelValidator.getValidationErrors();
                
                getLogger().error( "Validation Error: " + mergedModel.getApplication() );
                
                for ( Iterator i = validationErrors.iterator(); i.hasNext(); )
                {                                  
                    getLogger().error( (String)i.next() );
                }
                
                throw new RoleManagerException( "Validation Error: " + mergedModel.getApplication() );
            }
        }
        
        modelProcessor.process( blessedModel );
    }
	
    /**
     * create a role for the given roleName using the resource passed in for resolving the
     * ${resource} expression
     * 
     */
    public void createTemplatedRole( String templateId, String resource ) throws RoleManagerException
    {
        templateProcessor.create( blessedModel, templateId, resource );
    }

    /**
     * remove the role corresponding to the role using the resource passed in for resolving the
     * ${resource} expression
     * 
     */
    public void removeTemplatedRole( String templateId, String resource ) throws RoleManagerException
    {
        ModelTemplate template = RoleModelUtils.getModelTemplate( blessedModel, templateId );
    
        String roleName = template.getNamePrefix() + template.getDelimiter() + resource;

        try
        {
            Role role = rbacManager.getRole( roleName );

            // remove the user assignments
            List rolesList = new ArrayList();
            rolesList.add( role );

            List userAssignments = rbacManager.getUserAssignmentsForRoles( rolesList );

            for ( Iterator i = userAssignments.iterator(); i.hasNext(); )
            {
                UserAssignment assignment = (UserAssignment) i.next();
                assignment.removeRoleName( role );
                rbacManager.saveUserAssignment( assignment );
            }

        }
        catch ( RbacManagerException e )
        {
            throw new RoleManagerException( "unable to remove role", e );
        }

        templateProcessor.remove( blessedModel, templateId, resource );
    }

    /**
     * update the role from templateId from oldResource to newResource
     * 
     * NOTE: this requires removal and creation of the role since the jdo store does not tolerate renaming
     * because of the use of the name as an identifier
     * 
     */
    public void updateRole( String templateId, String oldResource, String newResource ) throws RoleManagerException
    {
        // make the new role
        templateProcessor.create( blessedModel, templateId, newResource );
        
        ModelTemplate template = RoleModelUtils.getModelTemplate( blessedModel, templateId );
        
        String oldRoleName = template.getNamePrefix() + template.getDelimiter() + oldResource;
        String newRoleName = template.getNamePrefix() + template.getDelimiter() + newResource;
        
        try
        {
            Role role = rbacManager.getRole( oldRoleName );

            // remove the user assignments
            List rolesList = new ArrayList();
            rolesList.add( role );

            List userAssignments = rbacManager.getUserAssignmentsForRoles( rolesList );

            for ( Iterator i = userAssignments.iterator(); i.hasNext(); )
            {
                UserAssignment assignment = (UserAssignment) i.next();
                assignment.removeRoleName( role );
                assignment.addRoleName( newRoleName );
                rbacManager.saveUserAssignment( assignment );
            }

        }
        catch ( RbacManagerException e )
        {
            throw new RoleManagerException( "unable to update role", e );
        }
        
        templateProcessor.remove( blessedModel, templateId, oldResource );
    }

    
    public void assignRole( String roleId, String principal ) throws RoleManagerException
    {
        ModelRole modelRole = RoleModelUtils.getModelRole( blessedModel, roleId );

        if ( modelRole == null )
        {
            throw new RoleManagerException( "Unable to assign role: " + roleId + " does not exist." );
        }

        try
        {
            UserAssignment userAssignment;

            if ( rbacManager.userAssignmentExists( principal ) )
            {
                userAssignment = rbacManager.getUserAssignment( principal );
            }
            else
            {
                userAssignment = rbacManager.createUserAssignment( principal );
            }
            
            userAssignment.addRoleName( modelRole.getName() );
            rbacManager.saveUserAssignment( userAssignment );
        }
        catch ( RbacManagerException e )
        {
            throw new RoleManagerException( "Unable to assign role: unable to manage user assignment", e );
        }
    }
    
    

    public void unassignRole( String roleId, String principal ) throws RoleManagerException
    {
        ModelRole modelRole = RoleModelUtils.getModelRole( blessedModel, roleId );

        if ( modelRole == null )
        {
            throw new RoleManagerException( "Unable to assign role: " + roleId + " does not exist." );
        }

        try
        {
            UserAssignment userAssignment;

            if ( rbacManager.userAssignmentExists( principal ) )
            {
                userAssignment = rbacManager.getUserAssignment( principal );
            }
            else
            {
                throw new RoleManagerException( "UserAssignment for principal " + principal + "does not exist, can't unassign role." );
            }
            
            userAssignment.removeRoleName( modelRole.getName() );
            rbacManager.saveUserAssignment( userAssignment );
        }
        catch ( RbacManagerException e )
        {
            throw new RoleManagerException( "Unable to assign role: unable to manage user assignment", e );
        }
    }

    public boolean roleExists( String roleId ) throws RoleManagerException
    {
        ModelRole modelRole = RoleModelUtils.getModelRole( blessedModel, roleId );

        if ( modelRole == null )
        {
            return false;
        }       
        else
        {
            if ( rbacManager.roleExists( modelRole.getName() ) )
            {
                return true;
            }
            else
            {     
                // perhaps try and reload the model here?
                throw new RoleManagerException( "breakdown in role management, role exists in configuration but was not created in underlying store" );
            }
        }           
    }
    
    public boolean templatedRoleExists( String templateId, String resource ) throws RoleManagerException
    {
        ModelTemplate modelTemplate = RoleModelUtils.getModelTemplate( blessedModel, templateId );

        // template not existing is valid to check, it will throw exception on trying to create
        if ( modelTemplate == null )
        {
            return false;
        }       
        else
        {
            if ( rbacManager.roleExists( modelTemplate.getNamePrefix() + modelTemplate.getDelimiter() + resource ) )
            {
                return true;
            }
            else
            {     
                return false;
            }
        }           
    }

    public void initialize() throws InitializationException
    {
        try
        {
            URL baseResource = RoleManager.class.getResource( "/META-INF/redback/redback-core.xml" );
         
            if ( baseResource == null )
            {
                throw new InitializationException( "unable to initialize role manager, missing redback-core.xml" );
            }        
            
            loadRoleModel( baseResource );
            
            Enumeration enumerator = RoleManager.class.getClassLoader().getResources( "META-INF/redback/redback.xml" );
            
            while ( enumerator.hasMoreElements() )
            {
                URL redbackResource = (URL)enumerator.nextElement();
                
                loadRoleModel( redbackResource );
            }
        }
        catch ( RoleManagerException e )
        {
            throw new InitializationException( "unable to initialize RoleManager", e );
        }
        catch ( IOException e )
        {
            throw new InitializationException( "unable to initialize RoleManager, problem with redback.xml loading", e );
        }        
    }

    public RedbackRoleModel getModel()
    {
        return blessedModel;
    } 
    
    
}
