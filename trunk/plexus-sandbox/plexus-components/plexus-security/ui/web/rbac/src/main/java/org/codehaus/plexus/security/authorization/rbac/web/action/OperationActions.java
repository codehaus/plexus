package org.codehaus.plexus.security.authorization.rbac.web.action;

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

import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.Preparable;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * OperationActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="plexusSecurityOperation"
 * @deprecated will be removed before version 1.0
 */
public class OperationActions
    extends PlexusActionSupport
    implements ModelDriven, Preparable
{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private String operationName;

    private Operation operation;

    public void prepare()
        throws Exception
    {
        if ( operation == null )
        {
            if ( manager.operationExists( operationName ) )
            {
                operation = manager.getOperation( operationName );
                operationName = operation.getName();
            }
            else
            {
                operation = manager.createOperation( "name" );
            }
        }
    }

    public String save()
        throws RbacActionException
    {
        Operation temp = manager.createOperation( operation.getName() );

        temp.setName( operation.getName() );
        temp.setDescription( operation.getDescription() );

        manager.saveOperation( temp );
        return SUCCESS;
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
        return SUCCESS;
    }


    public Object getModel()
    {
        return operation;
    }

    public String getOperationName()
    {
        return operationName;
    }

    public void setOperationName( String operationName )
    {
        this.operationName = operationName;
    }

    public void setOperation( Operation operation )
    {
        this.operation = operation;
    }

}
