package org.codehaus.plexus.redback.xwork.role.profile;

/*
 * Copyright 2005-2006 The Codehaus.
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

import org.codehaus.plexus.rbac.profile.AbstractRoleProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * UserAdministratorRoleProfile:
 *
 * @author: Jesse McConnell <jmcconnell@apache.org>
 * @version: $ID:$
 * @plexus.component role="org.codehaus.plexus.rbac.profile.RoleProfile"
 * role-hint="user-administrator"
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
        operations.add( RoleConstants.USER_MANAGEMENT_ROLE_DROP_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_ROLE_GRANT_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_USER_CREATE_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_USER_DELETE_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_USER_EDIT_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_USER_ROLE_OPERATION );
        operations.add( RoleConstants.USER_MANAGEMENT_USER_LIST_OPERATION );

        return operations;
    }


    public boolean isAssignable()
    {
        return true;
    }


    public boolean isPermanent()
    {
        return true;
    }
}
