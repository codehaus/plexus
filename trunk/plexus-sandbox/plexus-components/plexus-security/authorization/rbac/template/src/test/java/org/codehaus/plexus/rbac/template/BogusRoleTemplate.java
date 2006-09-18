package org.codehaus.plexus.rbac.template;

import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;

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
 * RoleTemplateTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.rbac.template.RoleTemplate"
 *   role-hint="bogus"
 */
public class BogusRoleTemplate
   extends AbstractRoleTemplate
{


    public String getRoleName()
    {
        return "bogus-role";
    }

    /**
     *
     * @return
     * @throws RoleTemplateException
     */
    public boolean createRole( )
        throws RoleTemplateException
    {
        Permission bogusPermission = rbacManager.createPermission( "bogus-permission", "bogus-operation", Resource.GLOBAL );
        bogusPermission = rbacManager.savePermission( bogusPermission );

        Role bogusRole = rbacManager.createRole( "bogus-role" );
        bogusRole.addPermission( bogusPermission );
        bogusRole = rbacManager.saveRole( bogusRole );

        return true;
    }
}
