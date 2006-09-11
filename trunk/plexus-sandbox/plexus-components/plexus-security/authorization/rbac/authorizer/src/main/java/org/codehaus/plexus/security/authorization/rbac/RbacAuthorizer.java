package org.codehaus.plexus.security.authorization.rbac;

/*
 * Copyright 2005 The Apache Software Foundation.
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

import org.codehaus.plexus.security.authorization.AuthorizationDataSource;
import org.codehaus.plexus.security.authorization.AuthorizationException;
import org.codehaus.plexus.security.authorization.AuthorizationResult;
import org.codehaus.plexus.security.authorization.Authorizer;
import org.codehaus.plexus.security.authorization.NotAuthorizedException;
import org.codehaus.plexus.security.authorization.rbac.evaluator.PermissionEvaluator;
import org.codehaus.plexus.security.authorization.rbac.evaluator.PermissionEvaluationException;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.Iterator;
import java.util.Set;

/**
 * RbacAuthorizer:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.authorization.Authorizer"
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
     * @plexus.requirement role-hint="default"
     */
    private PermissionEvaluator evaluator;

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
            Set permissions = manager.getAssignedPermissions( principal.toString() );

            for ( Iterator i = permissions.iterator(); i.hasNext(); )
            {
                Permission permission = (Permission)i.next();
              
                if ( evaluator.evaluate( permission, operation, resource ) )
                {
                    return new AuthorizationResult( true, permission, null );
                }
            }

            return new AuthorizationResult(false, null, new NotAuthorizedException( "no matching permissions" ) );
        }
        catch ( PermissionEvaluationException pe )
        {
            pe.printStackTrace();
            return new AuthorizationResult( false, null, pe );
        }
        catch ( RbacObjectNotFoundException nfe)
        {
            nfe.printStackTrace();

            return new AuthorizationResult( false, null, nfe );
        }
    }
}
