package org.codehaus.plexus.security.ui.web.role.template;

import org.codehaus.plexus.rbac.template.AbstractRoleTemplate;
import org.codehaus.plexus.rbac.template.RoleTemplateException;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
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
 * UserManagementRoleTemplate:
 *
 * @author: Jesse McConnell <jmcconnell@apache.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.rbac.template.RoleTemplate"
 *   role-hint="user-management-role"
 */
public class UserManagementRoleTemplate
    extends AbstractRoleTemplate
{
    public String getRoleName()
    {
        return RoleConstants.USER_MANAGEMENT_ROLE;
    }

    /**
     *
     * @return
     * @throws RoleTemplateException
     */
    public boolean createRole()
        throws RoleTemplateException
    {
        Permission managerPerm = rbacManager.createPermission( RoleConstants.USER_MANAGEMENT_PERMISSION, RoleConstants.USER_MANAGEMENT_MASTER_OPERATION, Resource.GLOBAL );
        managerPerm = rbacManager.savePermission( managerPerm );

        Role managerRole = rbacManager.createRole( RoleConstants.USER_MANAGEMENT_ROLE );
        managerRole.addPermission( managerPerm );
        managerRole = rbacManager.saveRole( managerRole );

        return true;
    }
}
