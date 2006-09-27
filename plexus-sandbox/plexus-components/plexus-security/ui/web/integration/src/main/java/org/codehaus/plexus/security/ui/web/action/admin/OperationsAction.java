package org.codehaus.plexus.security.ui.web.action.admin;

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

import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.role.profile.RoleConstants;
import org.codehaus.plexus.security.ui.web.util.OperationSorter;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * OperationsAction:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-operations"
 *                   instantiation-strategy="per-lookup"
 */
public class OperationsAction
    extends PlexusActionSupport
{
    private static final String LIST = "list";

    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private String operationName;

    private String description;

    private List allOperations;

    public String list()
    {
        allOperations = manager.getAllOperations();
        
        if(allOperations == null)
        {
            allOperations = Collections.EMPTY_LIST;
        }
        
        Collections.sort( allOperations, new OperationSorter() );

        return LIST;
    }

    public String save()
    {
        Operation temp = manager.createOperation( operationName );

        temp.setDescription( description );

        manager.saveOperation( temp );
        return LIST;
    }

    public String remove()
    {
        try
        {
            manager.removeOperation( manager.getOperation( operationName ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            addActionError( "Unable to remove operation '" + operationName + "'" );
            return ERROR;
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

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_RBAC_ADMIN_OPERATION, Resource.GLOBAL );
        return bundle;
    }
}
