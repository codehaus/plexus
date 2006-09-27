package org.codehaus.plexus.security.ui.web.role.profile;

import org.codehaus.plexus.rbac.profile.AbstractRoleProfile;
import org.codehaus.plexus.rbac.profile.RoleProfileException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;

import java.util.List;
import java.util.Collections;
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
 * RegisteredUserRoleProfile:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.rbac.profile.RoleProfile"
 *   role-hint="registered-user" 
 */
public class RegisteredUserRoleProfile
    extends AbstractRoleProfile
{


    public String getRoleName()
    {
        return RoleConstants.REGISTERED_USER_ROLE;
    }
 
    public List getOperations()
    {
        return Collections.singletonList( RoleConstants.USER_MANAGEMENT_USER_EDIT_OPERATION );
    }

    public boolean isAssignable()
    {
        return true;
    }


    public Resource getResource()
        throws RoleProfileException
    {
        Resource username;

        if ( !rbacManager.resourceExists( "${username}" ) )
        {
            username = rbacManager.createResource( "${username}" );
            rbacManager.saveResource( username );
        }
        else
        {
            try
            {
                username = rbacManager.getResource( "${username}" );

            }
            catch ( RbacObjectNotFoundException ne )
            {
                throw new RoleProfileException( "unable to return resource" );
            }
        }
        
        return username;
    }
}