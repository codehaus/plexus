package org.codehaus.plexus.rbac.profile;

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

import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;

import java.util.Iterator;
import java.util.List;

/**
 * AbstractRoleProfile:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public abstract class AbstractRoleProfile
    implements RoleProfile
{
    /**
     * @plexus.requirement
     */
    protected RBACManager rbacManager;

    private Role generateRole()
        throws RoleProfileException
    {

        Role role = rbacManager.createRole( getRoleName() );

        try
        {
            if ( getOperations() != null )
            {
                List operations = getOperations();

                for ( Iterator i = operations.iterator(); i.hasNext(); )
                {
                    String operationString = (String) i.next();

                    if ( !rbacManager.operationExists( operationString ) )
                    {
                        Operation operation = rbacManager.createOperation( operationString );
                        operation = rbacManager.saveOperation( operation );
                    }

                    if ( !rbacManager.permissionExists(
                        operationString + RoleProfileConstants.DELIMITER + Resource.GLOBAL ) )
                    {

                        Permission permission = rbacManager.createPermission(
                            operationString + RoleProfileConstants.DELIMITER + Resource.GLOBAL );
                        permission.setOperation( rbacManager.getOperation( operationString ) );
                        permission.setResource( rbacManager.getGlobalResource() );
                        rbacManager.savePermission( permission );

                    }

                    role.addPermission( rbacManager.getPermission(
                        operationString + RoleProfileConstants.DELIMITER + Resource.GLOBAL ) );
                }
            }

            if ( getChildRoles() != null )
            {
                List childRoles = getChildRoles();

                for ( Iterator i = childRoles.iterator(); i.hasNext(); )
                {
                    role.addChildRoleName( (String) i.next() );
                }
            }
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RoleProfileException( "error initializing role components", ne );
        }

        role = rbacManager.saveRole( role );

        return role;
    }


    /**
     * by default roles don't have child roles, so the abstract base class for this will just take care of it
     * and if the implementing role profile has need of it then can just override it.
     *
     * @return
     */
    public List getChildRoles()
    {
        return null;
    }


    public Role getRole()
        throws RoleProfileException
    {
        try
        {
            if ( rbacManager.roleExists( getRoleName() ) )
            {
                return rbacManager.getRole( getRoleName() );
            }
            else
            {
                return generateRole();
            }
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RoleProfileException( "unable to get role", ne );
        }
    }
}