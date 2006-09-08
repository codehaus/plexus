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

/**
 * Role 
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo fix and expand javadoc
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
     * Method addRole
     * 
     * @param role
     */
    public void addRole( Role role );

    /**
     * Get null
     */
    public String getDescription();

    /**
     * Get null
     */
    public String getName();

    /**
     * Method getPermissions
     */
    public List getPermissions();

    /**
     * Method getRoles
     */
    public List getRoles();

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
     * Method removeRole
     * 
     * @param role
     */
    public void removeRole( Role role );

    /**
     * true if this role is available to be assigned to a user
     * 
     * @param assignable
     */
    public void setAssignable( boolean assignable );

    /**
     * Set null
     * 
     * @param description
     */
    public void setDescription( String description );

    /**
     * Set null
     * 
     * @param name
     */
    public void setName( String name );

    /**
     * Set null
     * 
     * @param permissions
     */
    public void setPermissions( List permissions );

    /**
     * roles that will inherit the permissions of this role
     * 
     * @param roles
     */
    public void setRoles( List roles );

}