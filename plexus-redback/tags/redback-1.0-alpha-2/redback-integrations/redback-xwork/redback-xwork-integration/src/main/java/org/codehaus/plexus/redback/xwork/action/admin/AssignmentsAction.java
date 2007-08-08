package org.codehaus.plexus.redback.xwork.action.admin;

/*
 * Copyright 2005-2006 The Codehaus.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.redback.rbac.Permission;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.Resource;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.rbac.UserAssignment;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.system.SecuritySystemConstants;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.xwork.action.AbstractUserCredentialsAction;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionBundle;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionException;
import org.codehaus.plexus.redback.xwork.model.AdminEditUserCredentials;
import org.codehaus.plexus.redback.xwork.role.RoleConstants;
import org.codehaus.plexus.util.StringUtils;

/**
 * AssignmentsAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="redback-assignments"
 * instantiation-strategy="per-lookup"
 */
public class AssignmentsAction
    extends AbstractUserCredentialsAction
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement role-hint="cached"
     */
    private RBACManager manager;

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private String principal;

    private AdminEditUserCredentials user;
    
    /**
     * A List of {@link Role} objects.
     */
    private List assignedRoles;

    /**
     * A List of {@link Role} objects.
     */
    private List availableRoles;


    private List effectivelyAssignedRoles;

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
            addActionError( getText( "rbac.edit.user.empty.principal" ) );
            return ERROR;
        }

        UserManager userManager = super.securitySystem.getUserManager();
        

        if ( !userManager.userExists( principal ) )
        {
            List list = new ArrayList();
            list.add( principal );
            addActionError( getText( "user.does.not.exist", list ) );
            return ERROR;
        }

        try
        {
            User u = userManager.findUser( principal );

            if ( u == null )
            {
                addActionError( getText( "cannot.operate.on.null.user" ) );
                return ERROR;
            }

            user = new AdminEditUserCredentials( u );
        }
        catch ( UserNotFoundException e )
        {
            List list = new ArrayList();
            list.add( principal );
            list.add( e.getMessage() );
            addActionError( getText( "user.not.found.exception", list ) );
            return ERROR;
        }

        // Empty any selected options ...
        this.addSelectedRoles = new ArrayList();
        this.removeSelectedRoles = new ArrayList();
        this.effectivelyAssignedRoles = new ArrayList();

        try
        {
            if ( manager.userAssignmentExists( principal ) )
            {
                this.assignedRoles = new ArrayList( manager.getAssignedRoles( principal ) );

                // get effectively assigned roles
                List tmpList = new ArrayList( manager.getEffectivelyAssignedRoles( principal ) );

                // drop ones that can't be assigned
                for ( Iterator i = tmpList.iterator(); i.hasNext(); )
                {
                    Role role = (Role) i.next();
                    if ( role.isAssignable() )
                    {
                        effectivelyAssignedRoles.add( role );
                    }
                }
            }
            else
            {
                this.assignedRoles = new ArrayList();
            }

            if ( manager.userAssignmentExists( principal ) )
            {
                this.availableRoles = new ArrayList( manager.getEffectivelyUnassignedRoles( principal ) );
            }
            else
            {
                this.availableRoles = new ArrayList( manager.getAllAssignableRoles() );
            }

            // filter the roles through the hack method below
            assignedRoles = filterRolesForCurrentUserAccess( assignedRoles );
            availableRoles = filterRolesForCurrentUserAccess( availableRoles );
        }
        catch ( RbacManagerException e )
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
                catch ( RbacManagerException ne )
                {
                    List list = new ArrayList();
                    list.add( ne.getMessage() );
                    addActionError( getText( "error.adding.selected.roles", list ) );
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
                catch ( RbacManagerException ne )
                {
                    List list = new ArrayList();
                    list.add( ne.getMessage() );
                    addActionError( getText( "error.removing.selected.roles", list ) );
                    return ERROR;
                }
            }

            return SUCCESS;
        }

        return SUCCESS;
    }

    /**
     * this is a hack.
     *
     * this is a hack around the requirements of putting RBAC constraits into the model.
     *
     * this adds one very major restriction to this security system, that a role name must contain the identifiers of
     * the resource that is being constrained for adding and granting of roles, this is unacceptable in the long term
     * and we need to get the model refactored to include this RBAC concept 
     *
     *
     * @param roleList
     * @return
     * @throws RbacManagerException
     */
    private List filterRolesForCurrentUserAccess( List roleList )
        throws RbacManagerException
    {
        String currentUser = ((SecuritySession)session.get( SecuritySystemConstants.SECURITY_SESSION_KEY )).getUser().getPrincipal().toString();

        List filteredRoleList = new ArrayList();

        Map assignedPermissionMap = manager.getAssignedPermissionMap( currentUser );
        List resourceGrants = new ArrayList();

        if ( assignedPermissionMap.containsKey( RoleConstants.USER_MANAGEMENT_ROLE_GRANT_OPERATION ) )
        {
            List roleGrantPermissions = (List) assignedPermissionMap.get( RoleConstants.USER_MANAGEMENT_ROLE_GRANT_OPERATION );

            for (Iterator i = roleGrantPermissions.iterator(); i.hasNext(); )
            {
                Permission permission = (Permission)i.next();

                if ( permission.getResource().getIdentifier().equals( Resource.GLOBAL ))
                {
                    // the current user has the rights to assign any given role
                    return roleList;
                }
                else
                {
                    resourceGrants.add( permission.getResource().getIdentifier() );
                }
            }
        }
        else
        {
            return Collections.EMPTY_LIST;
        }

        // we should have a list of resourceGrants now, this will provide us with the information necessary to restrict
        // the role list
        for ( Iterator i = roleList.iterator(); i.hasNext(); )
        {
            Role role = (Role)i.next();

            for (Iterator j = resourceGrants.iterator(); j.hasNext(); )
            {
                String resourceIdentifier = (String)j.next();

                if ( role.getName().indexOf( resourceIdentifier ) != -1 )
                {
                    filteredRoleList.add( role );
                }
            }
        }

        return filteredRoleList;
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

    public List getEffectivelyAssignedRoles()
    {
        return effectivelyAssignedRoles;
    }

    public void setEffectivelyAssignedRoles( List effectivelyAssignedRoles )
    {
        this.effectivelyAssignedRoles = effectivelyAssignedRoles;
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

    public void setUsername( String username )
    {
        this.principal = username;
    }

    public List getRemoveSelectedRoles()
    {
        return removeSelectedRoles;
    }

    public void setRemoveSelectedRoles( List removeSelectedRoles )
    {
        this.removeSelectedRoles = removeSelectedRoles;
    }

    public AdminEditUserCredentials getUser()
    {
        return user;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_USER_EDIT_OPERATION, Resource.GLOBAL );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_RBAC_ADMIN_OPERATION, Resource.GLOBAL );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_ROLE_GRANT_OPERATION, Resource.GLOBAL );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_ROLE_DROP_OPERATION, Resource.GLOBAL );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_USER_ROLE_OPERATION, Resource.GLOBAL );
        
        return bundle;
    }
}
