package org.codehaus.plexus.redback.role.validator;

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

import org.codehaus.plexus.redback.role.RoleProfileException;
import org.codehaus.plexus.redback.role.model.ModelOperation;
import org.codehaus.plexus.redback.role.model.ModelPermission;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.ModelTemplate;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;

/**
 * DefaultRoleModelValidator: validates completeness of the model
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * 
 * @plexus.component role="org.codehaus.plexus.redback.role.validator.RoleModelValidator"
 *   role-hint="default"
 */
public class DefaultRoleModelValidator implements RoleModelValidator
{

    public boolean validate( RedbackRoleModel model ) throws RoleProfileException
    {
        List validationErrors = new ArrayList();
        
        validateOperationClosure( validationErrors, model );
        
        return true;
    }
    
    /**
     * validate all operations in all declared permissions exist as declared in the operations section
     * 
     * @param validationErrors
     * @param model
     */
    private void validateOperationClosure( List validationErrors, RedbackRoleModel model )
    {
        List declaredOperations = new ArrayList();
        
        if ( model.getOperations() == null )
        {
            validationErrors.add( "no declared operations" );
            return;
        }
        
        for ( Iterator i = model.getOperations().iterator(); i.hasNext(); )
        {
            ModelOperation operation = (ModelOperation)i.next();
            declaredOperations.add( operation.getId() );
        }
        
        // check the operations in role permissions
        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); ) 
        {
            ModelRole role = (ModelRole)i.next();
            
            if ( role.getPermissions() != null )
            {
                for ( Iterator j = role.getPermissions().iterator(); j.hasNext(); )
                {
                    ModelPermission permission = (ModelPermission)j.next();
                    
                    if ( !declaredOperations.contains( permission.getOperation() ) )
                    {
                        validationErrors.add( "missing operation: " + permission.getOperation() + " in permission " + permission.getId()  );
                    }                    
                }
            }
        }
        
        // check the operations in template permissions
        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); ) 
        {
            ModelTemplate template = (ModelTemplate)i.next();
            
            if ( template.getPermissions() != null )
            {
                for ( Iterator j = template.getPermissions().iterator(); j.hasNext(); )
                {
                    ModelPermission permission = (ModelPermission)j.next();
                    
                    if ( !declaredOperations.contains( permission.getOperation() ) )
                    {
                        validationErrors.add( "missing operation: " + permission.getOperation() + " in permission " + permission.getId()  );
                    }                    
                }
            }
        }
        
    }
}
