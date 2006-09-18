package org.codehaus.plexus.security.authorization.rbac.web.action.admin;

/*
* Copyright 2005 The Apache Software Foundation.
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

import com.opensymphony.xwork.Preparable;

import org.codehaus.plexus.security.authorization.rbac.web.action.RbacActionException;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * OperationsAction:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-operations"
 */
public class OperationsAction
    extends PlexusActionSupport
    implements Preparable
{
    private static final String LIST = "list";
    
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private String operationName;
    
    private String description;

    private List allOperations;

    public void prepare()
        throws Exception
    {
        if(allOperations == null)
        {
            allOperations = new ArrayList();
        }
    }
    
    public String list()
    {
        allOperations = manager.getAllOperations();
        
        return LIST;
    }

    public String save()
        throws RbacActionException
    {
        Operation temp = manager.createOperation( operationName );

        temp.setDescription( description );

        manager.saveOperation( temp );
        return LIST;
    }

    public String remove()
        throws RbacActionException
    {
        try
        {
            manager.removeOperation( manager.getOperation( operationName ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate operation to remove " + operationName, ne );
        }
        return LIST;
    }

    public List getAllOperations()
    {
        return allOperations;
    }

    public void setAllOperations( List allOperations )
    {
        this.allOperations = allOperations;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getOperationName()
    {
        return operationName;
    }

    public void setOperationName( String operationName )
    {
        this.operationName = operationName;
    }

}
