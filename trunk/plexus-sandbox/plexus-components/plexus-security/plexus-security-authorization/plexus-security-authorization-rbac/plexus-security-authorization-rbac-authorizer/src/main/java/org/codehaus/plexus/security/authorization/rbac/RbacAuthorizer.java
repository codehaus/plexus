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
import org.codehaus.plexus.security.authorization.rbac.store.RbacStore;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStoreException;

import java.util.Iterator;
import java.util.List;

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
    implements Authorizer
{

    /**
     * @plexus.requirement
     */
    private RbacStore store;

    public AuthorizationResult isAuthorized( AuthorizationDataSource source )
        throws AuthorizationException
    {
        Object principal = source.getPrincipal();
        Object operation = source.getPermission();
        Object resource = source.getResource();

        try
        {
            List roles = store.getRoleAssignments( principal.toString() );

            for ( Iterator i = roles.iterator(); i.hasNext(); )
            {
                Role role = (Role)i.next();

                for ( Iterator j = role.getPermissions().iterator(); j.hasNext(); )
                {
                    Permission permission = (Permission) j.next();

                    // check if the operation for that permission matches the desired operation
                    if ( permission.getOperation().getName().equals( operation.toString() ) )
                    {
                        if ( permission.getOperation().isResourceRequired() )
                        {
                            if ( resource != null )
                            {
                                // todo, expand this a bit more to support *-* type patterns                              
                                if ( permission.getOperation().getResource().isPattern() )
                                {
                                    String strToMatch = permission.getOperation().getResource().getIdentifier();

                                    int patternPosition = strToMatch.indexOf( '*' );

                                    strToMatch = strToMatch.substring( 0, patternPosition );

                                    if ( resource.toString().startsWith( strToMatch ) )
                                    {
                                        return new AuthorizationResult(true, permission, null);
                                    }
                                }
                                else
                                {
                                    if ( permission.getOperation().getResource().getIdentifier().equals( resource.toString() ) )
                                    {
                                        return new AuthorizationResult(true, permission, null);
                                    }
                                }
                            }
                        }
                        else
                        {
                            return new AuthorizationResult(true, permission, null);
                        }
                    }
                }
            }
            return new AuthorizationResult(false, null, new NotAuthorizedException( "no matching permissions" ) );

        }
        catch ( RbacStoreException e )
        {
            throw new AuthorizationException( "error with rbac store", e );
        }
    }
}
