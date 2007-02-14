package org.codehaus.plexus.rbac.profile;

import java.util.Collections;
import java.util.List;
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
 * RoleProfileTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 * @plexus.component role="org.codehaus.plexus.rbac.profile.DynamicRoleProfile"
 * role-hint="bogus"
 */
public class BogusDynamicRoleProfile
    extends AbstractDynamicRoleProfile
{
    public static final String NAME = "BOGUS ROLE";

    public static final String OPERATION = "BOGUS-OPERATION";

    public String getRoleName( String resource )
    {
        return NAME + RoleProfileConstants.DELIMITER + resource;
    }

    public List getOperations()
    {
        return Collections.singletonList( OPERATION );
    }


    public boolean isAssignable()
    {
        return true;
    }
}
