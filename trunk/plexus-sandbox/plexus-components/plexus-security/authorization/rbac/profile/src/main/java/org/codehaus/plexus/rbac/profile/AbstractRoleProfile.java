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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.PlexusContainer;

import java.util.Iterator;
import java.util.List;

/**
 * AbstractRoleProfile:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public abstract class AbstractRoleProfile
    extends AbstractLogEnabled
    implements RoleProfile
{
    /**
     * @plexus.requirement
     */
    protected RBACManager rbacManager;

    /**
     * @plexus.requirement
     */
    protected PlexusContainer container;

    /**
     *
     * @return
     * @throws RoleProfileException
     */
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
                        operation.setPermanent( isPermanent() );
                        operation = rbacManager.saveOperation( operation );
                    }

                    if ( !rbacManager.permissionExists(
                        operationString + RoleProfileConstants.DELIMITER + getResource().getIdentifier() ) )
                    {
                        Permission permission = rbacManager.createPermission(
                            operationString + RoleProfileConstants.DELIMITER + getResource().getIdentifier() );
                        permission.setOperation( rbacManager.getOperation( operationString ) );
                        permission.setResource( getResource() );
                        permission.setPermanent( isPermanent() );
                        rbacManager.savePermission( permission );
                    }

                    role.addPermission( rbacManager.getPermission(
                        operationString + RoleProfileConstants.DELIMITER + getResource().getIdentifier() ) );
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
     * merge this role with a target role profile
     *
     * @param targetRoleHint
     * @throws RoleProfileException
     */
    public Role mergeWithRoleProfile( String targetRoleHint )
        throws RoleProfileException
    {
        try
        {
           RoleProfile target = (RoleProfile) container.lookup( RoleProfile.ROLE, targetRoleHint );

           Role rootRole = getRole();

           if ( target.getOperations() != null )
            {
                List operations = target.getOperations();

                for ( Iterator i = operations.iterator(); i.hasNext(); )
                {
                    String operationString = (String) i.next();

                    if ( !rbacManager.operationExists( operationString ) )
                    {
                        Operation operation = rbacManager.createOperation( operationString );
                        operation = rbacManager.saveOperation( operation );
                    }

                    if ( !rbacManager.permissionExists(
                        operationString + RoleProfileConstants.DELIMITER + getResource().getIdentifier() ) )
                    {

                        Permission permission = rbacManager.createPermission(
                            operationString + RoleProfileConstants.DELIMITER + getResource().getIdentifier()  );
                        permission.setOperation( rbacManager.getOperation( operationString ) );
                        permission.setResource( getResource() );
                        rbacManager.savePermission( permission );

                    }

                    rootRole.addPermission( rbacManager.getPermission(
                        operationString + RoleProfileConstants.DELIMITER + getResource().getIdentifier() ) );
                }
            }

            if ( target.getChildRoles() != null )
            {
                List childRoles = target.getChildRoles();

                for ( Iterator i = childRoles.iterator(); i.hasNext(); )
                {
                    rootRole.addChildRoleName( (String) i.next() );
                }
            }

            rootRole = rbacManager.saveRole( rootRole );

            return rootRole;
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleProfileException( "unable to location role profile", cle );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RoleProfileException( "error merging role profiles", ne );
        }
        catch ( RbacManagerException e )
        {
            throw new RoleProfileException( "system error with rbac manager", e );
        }
    }


    public Resource getResource()
        throws RoleProfileException
    {
        try
        {
            return rbacManager.getGlobalResource();
        }
        catch ( RbacManagerException e )
        {
            throw new RoleProfileException( "system error with rbac manager", e );
        }
    }


    public boolean isPermanent()
    {
        return false;
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
        catch ( RbacManagerException e )
        {
            throw new RoleProfileException( "system error with rbac manager", e );
        }
    }
}