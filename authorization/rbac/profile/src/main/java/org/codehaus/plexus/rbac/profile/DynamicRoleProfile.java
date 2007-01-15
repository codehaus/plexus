package org.codehaus.plexus.rbac.profile;

import org.codehaus.plexus.security.rbac.Role;

import java.util.List;
/*
 * Copyright 2006 The Apache Software Foundation.
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
 * RoleProfile: Implementations of this interface should construct a role and pass it
 * back to the calling code.  The implementation should also add the role to the store as well
 * via the RBACManager.
 *
 * Implementations of this interface address the need for the generation of a new role
 * specifically targeted at a particular resource.  This necessitates the creation of a
 * consistent set of Permissions for dealing with Operations on the resource, and then the
 * adding of the Permissions to the Role and finally the Role itself.
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 */
public interface DynamicRoleProfile
{
    public static final String ROLE = DynamicRoleProfile.class.getName();

    public String getRoleName( String resource );

    public List getOperations();

     /**
     * is this role profile assignable
     * @return
     */
    public boolean isAssignable();

    /**
     * are roles based on this role profile permanent
     */
    public boolean isPermanent();

    public List getChildRoles();

    public List getDynamicChildRoles( String resource );

    public Role getRole( String resource ) throws RoleProfileException;

    public void deleteRole( String resource ) throws RoleProfileException;

    public void renameRole( String oldResource, String newResource ) throws RoleProfileException;
}
