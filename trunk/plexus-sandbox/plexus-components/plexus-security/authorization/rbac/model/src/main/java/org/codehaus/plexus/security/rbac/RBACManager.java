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
 * RBACManager 
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo expand on javadoc
 */
public interface RBACManager
{
    /**
     * Plexus Role Name
     */
    public static final String ROLE = RBACManager.class.getName();

    /**
     * Method addAssignment
     * 
     * @param userAssignment
     */
    public void addAssignment( UserAssignment userAssignment );

    /**
     * Method addRole
     * 
     * @param role
     */
    public void addRole( Role role );

    /**
     * Method getAssignments
     */
    public List getAssignments();

    /**
     * Method getRoles
     */
    public List getRoles();

    /**
     * Method removeAssignment
     * 
     * @param userAssignment
     */
    public void removeAssignment( UserAssignment userAssignment );

    /**
     * Method removeRole
     * 
     * @param role
     */
    public void removeRole( Role role );

    /**
     * Set null
     * 
     * @param assignments
     */
    public void setAssignments( List assignments );

    /**
     * Set The roles available to be assigned
     * 
     * @param roles
     */
    public void setRoles( List roles );

    public void setOperations( List operation );
    public void addOperation( Operation operation );
    public List getOperations();
    public void setPermissions( List permissions );
    public void addPermission( Permission permission );
    public List getPermissions();
    public void setResources ( List resources );
    public void addResource( Resource resource );
    public List getResources();
    
    public Role createRole(String name, String description);
    public Permission createPermission(String name, String description);
    public Permission createPermission(String name, String description, String operation, String resource);
    public Operation createOperation(String name, String description);
    public Resource createResource(String identifier);
}