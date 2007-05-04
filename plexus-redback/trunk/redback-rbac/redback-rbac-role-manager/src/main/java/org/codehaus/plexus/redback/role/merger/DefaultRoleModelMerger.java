package org.codehaus.plexus.redback.role.merger;

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

import org.codehaus.plexus.redback.role.RoleManagerException;
import org.codehaus.plexus.redback.role.model.ModelOperation;
import org.codehaus.plexus.redback.role.model.ModelResource;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.ModelTemplate;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.util.RoleModelUtils;

/**
 * DefaultRoleModelValidator: validates completeness of the model
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * 
 * @plexus.component role="org.codehaus.plexus.redback.role.merger.RoleModelMerger"
 *   role-hint="default"
 */
public class DefaultRoleModelMerger implements RoleModelMerger
{
    private RedbackRoleModel mergedModel = new RedbackRoleModel();
    
    private List mergeErrors;
    
    /**
     * merges the first and the second model and returns the merged model
     */
    public RedbackRoleModel merge( RedbackRoleModel originalModel, RedbackRoleModel newModel )
        throws RoleManagerException
    {
        // clear merge errors
        mergeErrors = null;
        
        mergeResources( originalModel, newModel );
        mergeOperations( originalModel, newModel );
        mergeRoles( originalModel, newModel );
        mergeTemplates( originalModel, newModel );

        return mergedModel;
    }
    
    /**
     * reports if the last merge had errors or not, this is reset for each merge operation
     */
    public boolean hasMergeErrors()
    {
        return !( mergeErrors == null );
    }
    
    public List getMergeErrors()
    {
        return mergeErrors;
    }
    
    private void addMergeError( String error )
    {
        if ( mergeErrors == null )
        {
            mergeErrors = new ArrayList();
        }
        
        mergeErrors.add( error );
    }

    private void mergeOperations( RedbackRoleModel originalModel, RedbackRoleModel newModel )
    {
        List operationIdList = RoleModelUtils.getOperationIdList( originalModel );
        
        mergedModel.setOperations( originalModel.getOperations() );
        
        for ( Iterator i = newModel.getOperations().iterator(); i.hasNext(); )
        {
            ModelOperation operation = (ModelOperation)i.next();
            
            if ( operationIdList.contains( operation.getId() ) )
            {
                addMergeError( "duplicate operation id detected: " + operation.getId() );
            }
            else
            {
                mergedModel.addOperation( operation );
            }
        }
    }

    private void mergeResources( RedbackRoleModel originalModel, RedbackRoleModel newModel )
    {
        List resourceIdList = RoleModelUtils.getResourceIdList( originalModel );
        
        mergedModel.setResources( originalModel.getResources() );
        
        for ( Iterator i = newModel.getResources().iterator(); i.hasNext(); )
        {
            ModelResource resource = (ModelResource)i.next();
            
            if ( resourceIdList.contains( resource.getId() ) )
            {
                addMergeError( "duplicate resource id detected: " + resource.getId() );
            }
            else
            {
                mergedModel.addResource( resource );
            }
        } 
    }
    
    private void mergeRoles( RedbackRoleModel originalModel, RedbackRoleModel newModel )
    {
        List roleIdList = RoleModelUtils.getRoleIdList( originalModel );
        
        mergedModel.setRoles( originalModel.getRoles() );
        
        for ( Iterator i = newModel.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole)i.next();
            
            if ( roleIdList.contains( role.getId() ) )
            {
                addMergeError( "duplicate role id detected: " + role.getId() );
            }
            else
            {
                mergedModel.addRole( role );
            }
        } 
    }
    
    private void mergeTemplates( RedbackRoleModel originalModel, RedbackRoleModel newModel )
    {
        List templateIdList = RoleModelUtils.getTemplateIdList( originalModel );
        
        mergedModel.setTemplates( originalModel.getTemplates() );
        
        for ( Iterator i = newModel.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate)i.next();
            
            if ( templateIdList.contains( template.getId() ) )
            {
                addMergeError( "duplicate template id detected: " + template.getId() );
            }
            else
            {
                mergedModel.addTemplate( template );
            }
        } 
    }

}
