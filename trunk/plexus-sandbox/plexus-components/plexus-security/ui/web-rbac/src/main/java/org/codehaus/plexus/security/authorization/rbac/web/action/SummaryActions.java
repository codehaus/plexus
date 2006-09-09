package org.codehaus.plexus.security.authorization.rbac.web.action;

import org.codehaus.plexus.xwork.action.PlexusActionSupport;
import org.codehaus.plexus.security.rbac.RBACManager;

import java.util.List;
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

/**
 * SummaryActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="plexusSecuritySummary"
 */
public class SummaryActions
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private List roles;

    private List permissions;

    private List operations;

    private List resources;

    public String roleSummary()
    {
        roles = manager.getAllRoles();

        return SUCCESS;
    }

    public String permissionSummary()
    {
        permissions = manager.getAllPermissions();

        return SUCCESS;
    }

    public String operationSummary()
    {
        operations = manager.getAllOperations();

        return SUCCESS;
    }

    public String resourceSummary()
    {
        resources = manager.getAllResources();

        return SUCCESS;
    }

    public List getRoles()
    {
        return roles;
    }

    public void setRoles( List roles )
    {
        this.roles = roles;
    }

    public List getPermissions()
    {
        return permissions;
    }

    public void setPermissions( List permissions )
    {
        this.permissions = permissions;
    }

    public List getOperations()
    {
        return operations;
    }

    public void setOperations( List operations )
    {
        this.operations = operations;
    }

    public List getResources()
    {
        return resources;
    }

    public void setResources( List resources )
    {
        this.resources = resources;
    }
}
