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
import org.codehaus.plexus.redback.role.merger.RoleModelMerger;
import org.codehaus.plexus.redback.role.model.ModelOperation;
import org.codehaus.plexus.redback.role.model.ModelPermission;
import org.codehaus.plexus.redback.role.model.ModelResource;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.model.io.stax.RedbackRoleModelStaxReader;
import org.codehaus.plexus.redback.role.processor.RoleModelProcessor;
import org.codehaus.plexus.redback.role.template.RoleTemplateProcessor;
import org.codehaus.plexus.redback.role.validator.RoleModelValidator;

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
     * the blessed model that has been validated as complete
     */
    private RedbackRoleModel blessedModel;
   
    /**
     * the merged model that can be validated as complete
     */
    private RedbackRoleModel mergedModel;
    
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
            mergedModel = modelMerger.merge( model );
            if ( modelValidator.validate( mergedModel ) )
            {
                blessedModel = mergedModel;
            }
        }
        
        modelProcessor.process( model );
    }
	
    /**
     * create a role for the given roleName using the resource passed in for resolving the
     * ${resource} expression
     * 
     */
    public void createRole( String templateId, String resource ) throws RoleProfileException
    {
        templateProcessor.create( blessedModel, templateId, resource );
    }

    /**
     * remove the role corresponding to the role using the resource passed in for resolving the
     * ${resource} expression
     * 
     */
    public void removeRole( String templateId, String resource ) throws RoleProfileException
    {
        templateProcessor.remove( blessedModel, templateId, resource );
    }   
}
