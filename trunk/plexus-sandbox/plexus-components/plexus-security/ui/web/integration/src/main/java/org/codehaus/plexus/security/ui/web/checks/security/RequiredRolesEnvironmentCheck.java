package org.codehaus.plexus.security.ui.web.checks.security;

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
import org.codehaus.plexus.rbac.profile.RoleProfileException;
import org.codehaus.plexus.rbac.profile.RoleProfileManager;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.system.check.EnvironmentCheck;

import java.util.List;

/**
 * RequiredRolesEnvironmentCheck:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 * @plexus.component role="org.codehaus.plexus.security.system.check.EnvironmentCheck"
 * role-hint="required-roles"
 */
public class RequiredRolesEnvironmentCheck
    extends AbstractLogEnabled
    implements EnvironmentCheck
{

    /**
     * @plexus.requirement role-hint=default
     */
    private RoleProfileManager roleProfileManager;

    /**
     * boolean detailing if this environment check has been executed
     */
    private boolean checked = false;

    /**
     * @param violations
     */
    public void validateEnvironment( List violations )
    {
        if ( !checked )
        {
            getLogger().info( "Checking the existance of required roles." );
            // we require the User Administrator role to exist
            try
            {
                Role registeredUser = roleProfileManager.getRole( "registered-user" );
            }
            catch ( RoleProfileException e )
            {
                e.printStackTrace();
                violations.add( "unable to validate registered-user role" );
            }

            // we require the User Administrator role to exist
            try
            {
                Role userAdmin = roleProfileManager.getRole( "user-administrator" );
            }
            catch ( RoleProfileException e )
            {
                violations.add( "unable to validate user-administrator role" );
            }

            // userAdmin will be a child for sysAdmin so make sure we do that one first
            try
            {
                Role sysAdmin = roleProfileManager.getRole( "system-administrator" );
            }
            catch ( RoleProfileException e )
            {
                violations.add( "unable to validate system-administrator role" );
            }

            checked = true;
        }
    }
}