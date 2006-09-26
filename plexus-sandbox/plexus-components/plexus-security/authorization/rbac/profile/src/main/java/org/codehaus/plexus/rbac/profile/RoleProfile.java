package org.codehaus.plexus.rbac.profile;

import org.codehaus.plexus.security.rbac.Role;

import java.util.List;

/*
 * Copyright 2005 The Codehaus.
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
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id:$
 */
public interface RoleProfile
{
    public static final String ROLE = RoleProfile.class.getName();

    /**
     * return the name of the role this is a profile for
     * @return
     */
    public String getRoleName();

    /**
     * return the list of operations that this role will be granted global access to
     * @return
     */
    public List getOperations();

    /**
     * returns the list of roles that this role will reference as child roles for purposes of
     * role heirarchies
     */
    public List getChildRoles();

    /**
     * is this role profile assignable
     * @return
     */
    public boolean isAssignable();

    /**
     * return the role, either for the rbacManager should the role exist already, or
     * go through the motions to create it
     */
    public Role getRole()
        throws RoleProfileException;

    public Role mergeWithRoleProfile( String targetRoleHint )
        throws RoleProfileException;
}
