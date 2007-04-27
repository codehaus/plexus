package org.codehaus.plexus.redback.role.util;

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
import org.codehaus.plexus.redback.role.model.ModelResource;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.ModelTemplate;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;

/**
 * RoleModelUtils:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 */
public class RoleModelUtils
{
    
    public static List getOperationIdList( RedbackRoleModel model )
    {
        List operationsIdList = new ArrayList();
        
        for ( Iterator i = model.getOperations().iterator(); i.hasNext(); )
        {
            ModelOperation operation = (ModelOperation)i.next();
            operationsIdList.add( operation.getId() );
        }
        
        return operationsIdList;
    }
    
    public static List getResourceIdList( RedbackRoleModel model )
    {
        List resourceIdList = new ArrayList();
        
        for ( Iterator i = model.getResources().iterator(); i.hasNext(); )
        {
            ModelResource resource = (ModelResource)i.next();
            resourceIdList.add( resource.getId() );
        }
        
        return resourceIdList;
    }

    public static List getRoleIdList( RedbackRoleModel model )
    {
        List roleIdList = new ArrayList();
        
        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole)i.next();
            roleIdList.add( role.getId() );
        }
        
        return roleIdList;
    }

    public static List getTemplateIdList( RedbackRoleModel model )
    {
        List templateIdList = new ArrayList();
        
        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate)i.next();
            templateIdList.add( template.getId() );
        }
        
        return templateIdList;
        
    }

    
    
}
