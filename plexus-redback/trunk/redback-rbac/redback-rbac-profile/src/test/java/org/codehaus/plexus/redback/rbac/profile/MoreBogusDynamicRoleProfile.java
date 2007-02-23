package org.codehaus.plexus.redback.rbac.profile;

import java.util.List;
import java.util.Collections;
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

import org.codehaus.plexus.redback.rbac.profile.AbstractDynamicRoleProfile;
import org.codehaus.plexus.redback.rbac.profile.RoleProfileConstants;

/**
 * RoleProfileTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.redback.rbac.profile.DynamicRoleProfile"
 *   role-hint="more-bogus"
 */
public class MoreBogusDynamicRoleProfile
    extends AbstractDynamicRoleProfile
{
    public String getRoleName( String resource )
    {
        return "MORE BOGUS ROLE" + RoleProfileConstants.DELIMITER + resource;
    }

    public List getOperations()
    {
        return Collections.singletonList( "BOGUS-OPERATION" );
    }


    // add a child for the statis role bogus
    public List getChildRoles()
    {
        return Collections.singletonList( "bogus" );
    }


    // add a child for the dynamic role bogus
    public List getDynamicChildRoles( String resource )
    {
        return Collections.singletonList( "BOGUS ROLE" + RoleProfileConstants.DELIMITER + resource );
    }


    public boolean isAssignable()
    {
        return true;
    }
}
