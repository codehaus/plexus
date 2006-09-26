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
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * AssignmentsAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="pss-assignments"
 * instantiation-strategy="per-lookup"
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
     * <p/>
     * This should consist of the Role details for the specified user.
     * <p/>
     * A table of currently assigned roles.
     * This table should have a column to remove the role from the user.
     * This table should also have a column of checkboxes that can be selected
     * and then removed from the user.
     * <p/>
     * A table of roles that can be assigned.
     * This table should have a set of checkboxes that can be selected and
     * then added to the user.
     * <p/>
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
            if ( manager.userAssignmentExists( principal ) )
            {
                getLogger().info("1");
                this.assignedRoles = new ArrayList( manager.getAssignedRoles( principal ) );
            }
            else
            {
                getLogger().info("2");
                this.assignedRoles = new ArrayList();
            }

            if ( manager.userAssignmentExists( principal ) )
            {
                getLogger().info("3");
                this.availableRoles = new ArrayList( manager.getUnassignedRoles( principal ) );
            }
            else
            {
                getLogger().info("4");
                this.availableRoles = new ArrayList( manager.getAllRoles() );
            }
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

        getLogger().info("assigned roles:" + assignedRoles.size() ); 
        getLogger().info("available roles: " + availableRoles.size() );

        return SUCCESS;
    }

    /**
     * Display the edit user panel.
     *
     * @return
     */
    public String edituser()
    {
        getLogger().info( "in edit user now" );
        if ( addRolesButton )
        {
            getLogger().info( "add roles button was clicked" );
            if ( addSelectedRoles != null && addSelectedRoles.size() > 0 )
            {
                getLogger().info( "we selected some roles to grant " + addSelectedRoles.size() );
                try
                {
                    UserAssignment assignment;

                    if ( manager.userAssignmentExists( principal ) )
                    {
                        assignment = manager.getUserAssignment( principal );
                    }
                    else
                    {
                        assignment = manager.createUserAssignment( principal );
                    }

                    for ( Iterator i = addSelectedRoles.iterator(); i.hasNext(); )
                    {
                        String role = (String) i.next();
                        getLogger().info( "adding " + role + " to " + principal );
                        assignment.addRoleName( role );
                    }

                    manager.saveUserAssignment( assignment );
                }
                catch ( RbacObjectNotFoundException ne )
                {
                    addActionError( "error adding the selected roles: " + ne.getMessage() );
                    return ERROR;
                }
            }

            return SUCCESS;
        }

        if ( removeRolesButton )
        {
            if ( removeSelectedRoles != null && removeSelectedRoles.size() > 0 )
            {
                getLogger().info( "we selected some roles to remove" );
                try
                {
                    UserAssignment assignment = manager.getUserAssignment( principal );

                    List roles = assignment.getRoleNames();

                    for ( Iterator i = removeSelectedRoles.iterator(); i.hasNext(); )
                    {
                        roles.remove( (String) i.next() );
                    }

                    assignment.setRoleNames( roles );
                    manager.saveUserAssignment( assignment );
                }
                catch ( RbacObjectNotFoundException ne )
                {
                    addActionError( "error removing the selected roles: " + ne.getMessage() );
                    return ERROR;
                }
            }

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
