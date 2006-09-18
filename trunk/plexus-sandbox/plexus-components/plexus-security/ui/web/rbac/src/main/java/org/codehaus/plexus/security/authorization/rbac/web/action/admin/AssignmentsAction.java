package org.codehaus.plexus.security.authorization.rbac.web.action.admin;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * AssignmentsAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-assignments"
 *                   instantiation-strategy="per-lookup"
 */
public class AssignmentsAction
    extends PlexusActionSupport
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private String principal;

    /**
     * A List of {@link Role} objects.
     */
    private List assignedRoles;

    /**
     * A List of {@link Role} objects.
     */
    private List availableRoles;

    /**
     * List of names (recieved from client) of roles to add.
     */
    private List addSelectedRoles;

    /**
     * List of names (recieved from client) of roles to remove.
     */
    private List removeSelectedRoles;

    private boolean addRolesButton;

    private boolean removeRolesButton;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------
    
    /**
     * Display the edit user panel.
     * 
     * This should consist of the Role details for the specified user.
     * 
     * A table of currently assigned roles.
     *   This table should have a column to remove the role from the user.
     *   This table should also have a column of checkboxes that can be selected
     *   and then removed from the user.
     * 
     * A table of roles that can be assigned.
     *   This table should have a set of checkboxes that can be selected and
     *   then added to the user.
     *   
     * Duplicate role assignment needs to be taken care of.
     */
    public String show()
    {
        if ( StringUtils.isEmpty( principal ) )
        {
            addActionError( "Cannot use AssignmentsAction for RBAC Edit User with an empty principal." );
            return ERROR;
        }

        // Empty any selected options ...
        this.addSelectedRoles = new ArrayList();
        this.removeSelectedRoles = new ArrayList();

        try
        {
            this.assignedRoles = new ArrayList( manager.getAssignedRoles( principal ) );
            this.availableRoles = new ArrayList( manager.getUnassignedRoles( principal ) );
        }
        catch ( RbacStoreException e )
        {
            addActionError( e.getMessage() );
            return ERROR;
        }
        catch ( RbacObjectNotFoundException e )
        {
            addActionError( e.getMessage() );
            return ERROR;
        }
        
        return SUCCESS;
    }

    /**
     * Display the edit user panel.
     * 
     * @return
     */
    public String edituser()
    {
        if ( addRolesButton )
        {
            // Add Roles Selected.

            return SUCCESS;
        }

        if ( removeRolesButton )
        {
            // Remove Roles Selected

            return SUCCESS;
        }


        return SUCCESS;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public boolean isAddRolesButton()
    {
        return addRolesButton;
    }

    public void setAddRolesButton( boolean addRolesButton )
    {
        this.addRolesButton = addRolesButton;
    }

    public List getAssignedRoles()
    {
        return assignedRoles;
    }

    public void setAssignedRoles( List assignedRoles )
    {
        this.assignedRoles = assignedRoles;
    }

    public List getAvailableRoles()
    {
        return availableRoles;
    }

    public void setAvailableRoles( List availableRoles )
    {
        this.availableRoles = availableRoles;
    }

    public boolean isRemoveRolesButton()
    {
        return removeRolesButton;
    }

    public void setRemoveRolesButton( boolean removeRolesButton )
    {
        this.removeRolesButton = removeRolesButton;
    }

    public List getAddSelectedRoles()
    {
        return addSelectedRoles;
    }

    public void setAddSelectedRoles( List addSelectedRoles )
    {
        this.addSelectedRoles = addSelectedRoles;
    }

    public String getPrincipal()
    {
        return principal;
    }

    public void setPrincipal( String principal )
    {
        this.principal = principal;
    }

    public List getRemoveSelectedRoles()
    {
        return removeSelectedRoles;
    }

    public void setRemoveSelectedRoles( List removeSelectedRoles )
    {
        this.removeSelectedRoles = removeSelectedRoles;
    }
}
