package org.codehaus.plexus.security.rbac;

import java.util.Map;

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

/**
 * UserAssignment - This the mapping object that takes the principal for a user and associates it with a
 * set of Roles.
 * 
 * This is the many to many mapping object needed by persistence stores.
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo expand on javadoc
 */
public interface UserAssignment
{
    /**
     * Plexus Role Name
     */
    public static final String ROLE = UserAssignment.class.getName();
    
    /**
     * The principal for the User that the set of roles is associated with.
     * 
     * NOTE: This field is considered the Primary Key for this object.
     * 
     * @return the principal for the User.
     */
    public String getPrincipal();

    /**
     * Get the roles for this user.
     */
    public Map getRoles();

    /**
     * Set the user principal object for this association.
     * 
     * NOTE: This field is considered the Primary Key for this object.
     * 
     * @param principal
     */
    public void setPrincipal( String principal );

    /**
     * Set the roles for this user.
     * 
     * @param roles the roles
     */
    public void setRoles( Map roles );
}