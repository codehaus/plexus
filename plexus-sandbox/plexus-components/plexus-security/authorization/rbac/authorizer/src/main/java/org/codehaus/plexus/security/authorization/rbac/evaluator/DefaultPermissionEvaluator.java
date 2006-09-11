package org.codehaus.plexus.security.authorization.rbac.evaluator;

import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.Resource;
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

/**
 * DefaultPermissionEvaluator:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component role="org.codehaus.plexus.security.authorization.rbac.evaluator.PermissionEvaluator"
 * role-hint="default"
 */
public class DefaultPermissionEvaluator
    implements PermissionEvaluator
{
    public boolean evaluate( Permission permission, Object operation, Object resource )
        throws PermissionEvaluationException
    {
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
            if ( resource != null && permission.getResource().getIdentifier().equals( resource.toString() ) )
            {
                return true;
            }
        }

        return false;
    }

}
