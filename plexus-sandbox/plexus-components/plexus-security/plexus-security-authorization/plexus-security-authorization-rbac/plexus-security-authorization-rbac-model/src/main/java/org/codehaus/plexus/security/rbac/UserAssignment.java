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
 * UserAssignment 
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo expand on javadoc
 */
public interface UserAssignment
{

    /**
     * Method addRole
     * 
     * @param role
     */
    public void addRole( Role role );

    /**
     * Get null
     */
    public String getPrincipal();

    /**
     * Method getRoles
     */
    public List getRoles();

    /**
     * Method removeRole
     * 
     * @param role
     */
    public void removeRole( Role role );

    /**
     * Set null
     * 
     * @param principal
     */
    public void setPrincipal( String principal );

    /**
     * Set null
     * 
     * @param roles
     */
    public void setRoles( List roles );

}