package org.codehaus.plexus.redback.authorization.rbac;

/*
 * Copyright 2001-2006 The Codehaus.
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
import org.codehaus.plexus.redback.authorization.AuthorizationDataSource;
import org.codehaus.plexus.redback.authorization.AuthorizationException;
import org.codehaus.plexus.redback.authorization.AuthorizationResult;
import org.codehaus.plexus.redback.authorization.Authorizer;
import org.codehaus.plexus.redback.authorization.NotAuthorizedException;
import org.codehaus.plexus.redback.authorization.rbac.evaluator.PermissionEvaluationException;
import org.codehaus.plexus.redback.authorization.rbac.evaluator.PermissionEvaluator;
import org.codehaus.plexus.redback.rbac.Permission;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.UserNotFoundException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * RbacAuthorizer:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.redback.authorization.Authorizer"
 *   role-hint="rbac"
 */
public class RbacAuthorizer
    extends AbstractLogEnabled
    implements Authorizer
{

    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    /**
     * @plexus.requirement role-hint="default"
     */
    private PermissionEvaluator evaluator;

    public String getId()
    {
        return "RBAC Authorizer - " + this.getClass().getName();
    }

    /**
     * @param source
     * @return
     * @throws AuthorizationException
     */
    public AuthorizationResult isAuthorized( AuthorizationDataSource source )
        throws AuthorizationException
    {
        Object principal = source.getPrincipal();
        Object operation = source.getPermission();
        Object resource = source.getResource();

        try
        {
            if ( principal != null )
            {
                // Set permissions = manager.getAssignedPermissions( principal.toString(), operation );
                Map permissionMap = manager.getAssignedPermissionMap( principal.toString() );

                if ( permissionMap.keySet().contains( operation.toString() ) )
                {
                    List permissions = (List) permissionMap.get( operation.toString() );

                    for ( Iterator i = permissions.iterator(); i.hasNext(); )
                    {
                        Permission permission = (Permission) i.next();

                        getLogger().debug( "checking permission " + permission.getName() );

                        if ( evaluator.evaluate( permission, operation, resource, principal ) )
                        {
                            return new AuthorizationResult( true, permission, null );
                        }
                    }
                }
            }
            // check if guest user is enabled, if so check the global permissions
            User guest = userManager.findUser( "guest" );
            if ( !guest.isLocked() )
            {
                // Set permissions = manager.getAssignedPermissions( principal.toString(), operation );
                Map permissionMap = manager.getAssignedPermissionMap( guest.getPrincipal().toString() );

                if ( permissionMap.keySet().contains( operation.toString() ) )
                {
                    List permissions = (List) permissionMap.get( operation.toString() );

                    for ( Iterator i = permissions.iterator(); i.hasNext(); )
                    {
                        Permission permission = (Permission) i.next();

                        getLogger().debug( "checking permission " + permission.getName() );

                        if ( evaluator.evaluate( permission, operation, resource, guest.getPrincipal() ) )
                        {
                            return new AuthorizationResult( true, permission, null );
                        }
                    }
                }
            }

            return new AuthorizationResult( false, null, new NotAuthorizedException( "no matching permissions" ) );
        }
        catch ( PermissionEvaluationException pe )
        {
            return new AuthorizationResult( false, null, pe );
        }
        catch ( RbacObjectNotFoundException nfe )
        {
            return new AuthorizationResult( false, null, nfe );
        }
        catch ( UserNotFoundException ne )
        {
            return new AuthorizationResult( false, null,
                                            new NotAuthorizedException( "no matching permissions, guest not found" ) );
        }
        catch ( RbacManagerException rme )
        {
            return new AuthorizationResult( false, null, rme );
        }
    }
}
