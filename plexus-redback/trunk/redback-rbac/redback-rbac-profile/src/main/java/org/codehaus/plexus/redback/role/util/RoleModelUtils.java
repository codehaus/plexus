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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.redback.role.RoleProfileException;
import org.codehaus.plexus.redback.role.model.ModelOperation;
import org.codehaus.plexus.redback.role.model.ModelResource;
import org.codehaus.plexus.redback.role.model.ModelRole;
import org.codehaus.plexus.redback.role.model.ModelTemplate;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.dag.DAG;
import org.codehaus.plexus.util.dag.TopologicalSorter;

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
            ModelOperation operation = (ModelOperation) i.next();
            operationsIdList.add( operation.getId() );
        }

        return operationsIdList;
    }

    public static List getResourceIdList( RedbackRoleModel model )
    {
        List resourceIdList = new ArrayList();

        for ( Iterator i = model.getResources().iterator(); i.hasNext(); )
        {
            ModelResource resource = (ModelResource) i.next();
            resourceIdList.add( resource.getId() );
        }

        return resourceIdList;
    }

    public static List getRoleIdList( RedbackRoleModel model )
    {
        List roleIdList = new ArrayList();

        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole) i.next();
            roleIdList.add( role.getId() );
        }

        return roleIdList;
    }

    public static List getTemplateIdList( RedbackRoleModel model )
    {
        List templateIdList = new ArrayList();

        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate) i.next();
            templateIdList.add( template.getId() );
        }

        return templateIdList;

    }
    
    /**
     * WARNING: can return null
     * 
     * @param model
     * @param roleId
     * @return
     */
    public static ModelRole getModelRole( RedbackRoleModel model, String roleId )
    {
        ModelRole mrole = null;

        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole) i.next();

            if ( roleId.equals( role.getId() ) )

            {
                mrole = role;
            }
        }
        return mrole;
    }

    public static DAG generateRoleGraph( RedbackRoleModel model ) throws CycleDetectedException
    {
        DAG roleGraph = new DAG();

        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            ModelRole role = (ModelRole) i.next();

            roleGraph.addVertex( role.getId() );

            if ( role.getChildRoles() != null )
            {
                for ( Iterator j = role.getChildRoles().iterator(); j.hasNext(); )
                {
                    String childRole = (String) j.next();
                    roleGraph.addVertex( childRole );

                    roleGraph.addEdge( role.getId(), childRole );
                }
            }

            if ( role.getParentRoles() != null )
            {
                for ( Iterator j = role.getParentRoles().iterator(); j.hasNext(); )
                {
                    String parentRole = (String) j.next();
                    roleGraph.addVertex( parentRole );

                    roleGraph.addEdge( parentRole, role.getId() );
                }
            }
        }

        return roleGraph;
    }

    public static DAG generateTemplateGraph( RedbackRoleModel model ) throws CycleDetectedException
    {
        DAG templateGraph = generateRoleGraph( model );

        for ( Iterator i = model.getTemplates().iterator(); i.hasNext(); )
        {
            ModelTemplate template = (ModelTemplate) i.next();

            templateGraph.addVertex( template.getId() );

            if ( template.getChildRoles() != null )
            {
                for ( Iterator j = template.getChildRoles().iterator(); j.hasNext(); )
                {
                    String childRole = (String) j.next();
                    templateGraph.addVertex( childRole );

                    templateGraph.addEdge( template.getId(), childRole );
                }
            }

            if ( template.getParentRoles() != null )
            {
                for ( Iterator j = template.getParentRoles().iterator(); j.hasNext(); )
                {
                    String parentRole = (String) j.next();
                    templateGraph.addVertex( parentRole );

                    templateGraph.addEdge( parentRole, template.getId() );
                }
            }

            if ( template.getChildTemplates() != null )
            {
                for ( Iterator j = template.getChildTemplates().iterator(); j.hasNext(); )
                {
                    String childTemplate = (String) j.next();
                    templateGraph.addVertex( childTemplate );

                    templateGraph.addEdge( template.getId(), childTemplate );
                }
            }

            if ( template.getParentTemplates() != null )
            {
                for ( Iterator j = template.getParentTemplates().iterator(); j.hasNext(); )
                {
                    String parentTemplates = (String) j.next();
                    templateGraph.addVertex( parentTemplates );

                    templateGraph.addEdge( parentTemplates, template.getId() );
                }
            }
        }

        return templateGraph;
    }

    public static List reverseTopologicalSortedRoleList( RedbackRoleModel model ) throws CycleDetectedException
    {
        LinkedList sortedGraph = (LinkedList)TopologicalSorter.sort( RoleModelUtils.generateRoleGraph( model ) );
        LinkedList resortedGraph = new LinkedList();
       

        while ( !sortedGraph.isEmpty() )
        { 
            resortedGraph.add( sortedGraph.removeLast() );
        }
        
       
        for ( Iterator i = resortedGraph.iterator(); i.hasNext(); )
        {
            System.out.println( "Role Id: " + (String)i.next() );
        }
        
        return resortedGraph;
    }
    
}
