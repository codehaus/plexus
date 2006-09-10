package org.codehaus.plexus.security.rbac;

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

import java.util.List;
import java.util.Map;

/**
 * Role 
 *
 * A role is assignable to a user and effectively grants that user all of the
 * permissions that are present in that role.  A role can also contain other roles
 * which add the permissions in those roles to the available permissions for authorization.
 *
 * A role can contain any number of permissions
 * A role can contain any number of other roles
 * A role can be assigned to any number of users
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface Role
{
    /**
     * Plexus Role Name
     */
    public static final String ROLE = Role.class.getName();

    /**
     * Method addPermission
     *
     * @param permission
     */
    public void addPermission( Permission permission );
    
    /**
     * Method addChildRole
     * 
     * @param childRole
     */
    public void addChildRole( Role childRole );

    /**
     * Method getChildRoles
     */
    public Map getChildRoles();
    
    /**
     * Convienence method to see if Role has Child Roles.
     * 
     * @return true if child roles exists and has any roles being tracked.
     */
    public boolean hasChildRoles();

    /**
     * Long description of the role.
     */
    public String getDescription();

    /**
     * Get the name.
     * 
     * NOTE: This field is considered the Primary Key for this object.
     */
    public String getName();

    /**
     * Method getPermissions
     */
    public List getPermissions();

    /**
     * true if this role is available to be assigned to a user
     */
    public boolean isAssignable();

    /**
     * Method removePermission
     *
     * @param permission
     */
    public void removePermission( Permission permission );

    /**
     * true if this role is available to be assigned to a user
     *
     * @param assignable
     */
    public void setAssignable( boolean assignable );

    /**
     * roles that will inherit the permissions of this role
     *
     * @param roles
     */
    public void setChildRoles( Map roles );

    /**
     * Set the Description
     *
     * @param description
     */
    public void setDescription( String description );

    /**
     * Set Name
     * 
     * NOTE: This field is considered the Primary Key for this object.
     *
     * @param name
     */
    public void setName( String name );

    /**
     * Set Permissions
     *
     * @param permissions
     */
    public void setPermissions( List permissions );
}