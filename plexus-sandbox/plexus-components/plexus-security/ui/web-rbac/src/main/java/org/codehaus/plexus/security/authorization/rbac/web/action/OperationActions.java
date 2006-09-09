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
 */
public class OperationActions
    extends PlexusActionSupport
    implements ModelDriven, Preparable
{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private int operationId;

    private Operation operation;

    public void prepare()
        throws Exception
    {
        if ( operation == null )
        {
            try
            {
                operation = manager.getOperation( operationId );
            }
            catch ( RbacObjectNotFoundException ne )
            {
                operation = manager.createOperation( "name", "resourceIdentifier" );
            }
        }
    }

    public String save()
    {
        try
        {
            manager.getOperation( operation.getId() );
            manager.updateOperation( operation );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            manager.addOperation( operation );
        }

        return SUCCESS;
    }

    public String remove()
        throws RbacActionException
    {
        try
        {
            manager.removeOperation( manager.getOperation( operationId ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate operation to remove " + operationId, ne );
        }
        return SUCCESS;
    }


    public Object getModel()
    {
        return operation;
    }

    public int getOperationId()
    {
        return operationId;
    }

    public void setOperationId( int operationId )
    {
        this.operationId = operationId;
    }

    public void setOperation( Operation operation )
    {
        this.operation = operation;
    }

}
