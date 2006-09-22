package org.codehaus.plexus.security.ui.web.role.profile;

import org.codehaus.plexus.rbac.profile.AbstractRoleProfile;

import java.util.ArrayList;
import java.util.List;
/*
 * Copyright 2006 The Codehaus.
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
 * UserAdministratorRoleProfile:
 *
 * @author: Jesse McConnell <jmcconnell@apache.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.rbac.profile.RoleProfile"
 *   role-hint="user-management-role"
 */
public class UserAdministratorRoleProfile
    extends AbstractRoleProfile
{

    public String getRoleName()
    {
        return RoleConstants.USER_ADMINISTRATOR_ROLE;
    }

    public List getOperations()
    {
        List operations = new ArrayList();
        operations.add( RoleConstants.USER_MANAGEMENT_REGISTER_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_CREATE_USER_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_EDIT_USER_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_GRANT_ROLE_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_REMOVE_ROLE_OPERATION );

        return operations;
    }

}
