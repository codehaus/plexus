package org.codehaus.plexus.security.authorization.rbac.evaluator;

import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
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
 * DefaultPermissionEvaluator:
 *
 * Currently only one expression is available for evaluation, ${username} will be replaced with the username
 * of the person making the authorization check
 *
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id:$
 * @plexus.component role="org.codehaus.plexus.security.authorization.rbac.evaluator.PermissionEvaluator"
 * role-hint="default"
 */
public class DefaultPermissionEvaluator
    extends AbstractLogEnabled
    implements PermissionEvaluator
{
    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    public boolean evaluate( Permission permission, Object operation, Object resource, Object principal )
        throws PermissionEvaluationException
    {
        String permissionResource = permission.getResource().getIdentifier();

        getLogger().debug( "checking " + operation.toString() + " vs " + permissionResource );

        // expression evaluation checking
        if ( permissionResource.startsWith( "${" ) )
        {
            String tempStr = permissionResource.substring( 2, permissionResource.indexOf( "}" ) );

            if ( "username".equals(tempStr) )
            {
                try
                {
                    permissionResource = userManager.findUser( principal.toString() ).getUsername();
                }
                catch ( UserNotFoundException ne )
                {
                    throw new PermissionEvaluationException( "unable to locate user to retrieve username", ne );
                }
            }
        }

        // check if this permission applies to the operation at all
        if ( permission.getOperation().getName().equals( operation.toString() ) )
        {
            // check if it is a global resource, if it is then since the operations match we return true
            if ( Resource.GLOBAL.equals( permission.getResource().getIdentifier() ) )
            {
                return true;
            }

            // check if the resource identifier of the permission matches the resource we are checking against
            // if it does then return true
            if ( resource != null && permissionResource.equals( resource.toString() ) )
            {
                return true;
            }
        }

        return false;
    }

}
