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

import java.util.Iterator;
import java.util.List;

/**
 * Roles is a collection of Roles.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface Roles
{
    /**
     * Plexus Role Name
     */
    public static final String ROLE = Roles.class.getName();

    /**
     * Implementation managed ID for this object.
     * 
     * NOTE: There is intentionally no .setId(int) object.
     * 
     * @return the id for this object.
     */
    public int getId();

    public Role getRole( int roleId )
        throws RbacObjectNotFoundException, RbacStoreException;

    public void addRole( Role role )
        throws RbacStoreException;

    public void removeRole( Role role )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getRoles()
        throws RbacStoreException;

    public void setRoles( List roles )
        throws RbacStoreException;

    public Iterator iterator();

    public boolean hasRoles();

    public boolean isEmpty();
    
    /**
     * Flatten the hierarchy of roles into a single list.
     * 
     * WARNING: This process looses the heirarchy information.
     * 
     * @return the flat list of roles found in this Role and below.
     */
    public List flattenRoleHierarchy();
}
