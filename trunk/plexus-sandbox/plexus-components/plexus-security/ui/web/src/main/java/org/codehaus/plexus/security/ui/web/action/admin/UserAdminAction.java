package org.codehaus.plexus.security.ui.web.action.admin;

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

import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.List;

/**
 * UserAdminAction - Administrator Tools for the User Database.
 * 
 * <ul>
 *   <li>{@link #list()} - Show User List</li>
 *   <li>{@link #find()} - Find User to Edit</li>
 *   <li>{@link #edit()} - Edit Existing User</li>
 *   <li>{@link #create()} - Create New User</li>
 *   <li>{@link #delete()} - Delete User</li>
 *   <li>{@link #grantRole()} - Grant Role</li>
 *   <li>{@link #dropRole()} - Drop Role</li>
 * </ul>
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class UserAdminAction extends PlexusActionSupport
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------
    
    /**
     * @plexus.requirement
     */
    private UserManager userManager;
    
    // ------------------------------------------------------------------
    // Destination Names - JSPs.
    // ------------------------------------------------------------------

    private static final String LIST = "list";
    private static final String EDIT = "edit";
    private static final String CREATE = "create";
    
    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private List userList;
    
    private List availableRoles;
    
    private List assignedRoles;
    
    private User editUser;
    
    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    /**
     * Obtain and show the user list.
     */
    public String list()
    {
        userList = userManager.getUsers();
        
        return LIST;
    }

    /**
     * Find a specific user to edit.
     */
    public String find()
    {
        return EDIT;
    }
    
    /**
     * Edit a specific user.
     */
    public String edit()
    {
        return EDIT;
    }
    
    /**
     * Delete a specific user.
     */
    public String delete()
    {
        return LIST;
    }
    
    /**
     * Administratively create a user.
     */
    public String create()
    {
        return CREATE;
    }
    
    /**
     * Grant a Role to edit user.
     */
    public String grantRole()
    {
        return EDIT;
    }
    
    /**
     * Drop a Role from edit user.
     */
    public String dropRole()
    {
        return EDIT;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------
    
    public List getUserList()
    {
        return userList;
    }

    public void setUserList( List userList )
    {
        this.userList = userList;
    }
    
    // ------------------------------------------------------------------
    // Internal Support Methods
    // ------------------------------------------------------------------
    
}
