package org.codehaus.plexus.rbac.profile;

import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;

import java.util.List;
import java.util.Iterator;
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
 * AbstractRoleProfile:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public abstract class AbstractDynamicRoleProfile
    implements DynamicRoleProfile
{
    /**
     * @plexus.requirement
     */
    protected RBACManager rbacManager;


    private Role generateRole( String resource )
        throws RoleProfileException
    {
        Role role = null;
        
        try
        {
            // make sure the resource exists
            if ( !rbacManager.resourceExists( resource ) )
            {
                Resource res = rbacManager.createResource( resource );
                rbacManager.saveResource( res );
            }

            role = rbacManager.createRole( getRoleName( resource ) );
            
            if ( getOperations() != null )
            {
                List operations = getOperations();

                for ( Iterator i = operations.iterator(); i.hasNext(); )
                {
                    String operationString = (String) i.next();

                    if ( !rbacManager.operationExists( operationString ) )
                    {
                        Operation operation = rbacManager.createOperation( operationString );
                        operation.setPermanent( isPermanent() );
                        operation = rbacManager.saveOperation( operation );
                    }

                    if ( !rbacManager.permissionExists( operationString + RoleProfileConstants.DELIMITER + resource ) )
                    {

                        Permission permission =
                            rbacManager.createPermission( operationString + RoleProfileConstants.DELIMITER + resource );
                        permission.setOperation( rbacManager.getOperation( operationString ) );
                        permission.setResource( rbacManager.getResource( resource ) );
                        permission.setPermanent( isPermanent() );
                        rbacManager.savePermission( permission );

                    }

                    role.addPermission(
                        rbacManager.getPermission( operationString + RoleProfileConstants.DELIMITER + resource ) );
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

            if ( getDynamicChildRoles( resource ) != null )
            {
                List childRoles = getDynamicChildRoles( resource );

                for ( Iterator i = childRoles.iterator(); i.hasNext(); )
                {
                    role.addChildRoleName( (String) i.next() );
                }
            }

            role.setAssignable( isAssignable() );
            role.setPermanent( isPermanent() );

            role = rbacManager.saveRole( role );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RoleProfileException( "error initializing role components", ne );
        }
        catch ( RbacManagerException e )
        {
            throw new RoleProfileException( "system error with rbac manager", e );
        }

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


    /**
     * some roles might require the resource in order to get dynamic child role if multiple roles are being made
     * together for a certain resource
     *
     * @param resource
     * @return
     */
    public List getDynamicChildRoles( String resource )
    {
        return null;
    }


    public boolean isPermanent()
    {
        return false;
    }

    public Role getRole( String resource )
        throws RoleProfileException
    {
        try
        {
            if ( rbacManager.roleExists( getRoleName( resource ) ) )
            {
                return rbacManager.getRole( getRoleName( resource ) );
            }
            else
            {
                return generateRole( resource );
            }
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RoleProfileException( "unable to get role", ne );
        }
        catch ( RbacManagerException e )
        {
            throw new RoleProfileException( "system error with rbac manager", e );
        }
    }
}
