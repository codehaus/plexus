package org.codehaus.plexus.rbac.template;

import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.Role;
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
 * RoleTemplateTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.rbac.template.DynamicRoleTemplate"
 *   role-hint="bogus"
 */
public class BogusDynamicRoleTemplate
    extends AbstractDynamicRoleTemplate
{


    public String getRoleNamePrefix()
    {
        return "Bogus Role";
    }

    /**
     *
     * @return
     * @throws org.codehaus.plexus.rbac.template.RoleTemplateException
     */
    public boolean createRole( String roleTemplateName, String resource )
        throws RoleTemplateException
    {
     System.out.println( "rbacmanager in bogus dynamic " + rbacManager.toString());
        Permission bogusPermission = rbacManager.createPermission( "bogus-permission-" + resource, "bogus-operation", resource );
        bogusPermission = rbacManager.savePermission( bogusPermission );

        Role bogusRole = rbacManager.createRole( getRoleName( roleTemplateName, resource ) );
        bogusRole.addPermission( bogusPermission );
        bogusRole = rbacManager.saveRole( bogusRole );

        if ( rbacManager.roleExists( bogusRole.getName() ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
