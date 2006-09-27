package org.codehaus.plexus.security.ui.web.checks.security;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.rbac.profile.RoleProfileManager;
import org.codehaus.plexus.rbac.profile.RoleProfileException;
import org.codehaus.plexus.security.system.check.EnvironmentCheck;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;

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
 * RequiredRolesEnvironmentCheck:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 * @plexus.component role="org.codehaus.plexus.security.system.check.EnvironmentCheck"
 * role-hint="guest-user-check"
 */
public class GuestUserEnvironmentCheck
    extends AbstractLogEnabled
    implements EnvironmentCheck
{

    /**
     * @plexus.requirement role-hint=default
     */
    private RoleProfileManager roleProfileManager;

    /**
     * @plexus.requirement
     */
    private RBACManager rbacManager;

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

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
            UserManager userManager = securitySystem.getUserManager();
            UserSecurityPolicy policy = securitySystem.getPolicy();

            User guest;

            try
            {
                guest = userManager.findUser( "guest" );
            }
            catch ( UserNotFoundException ne )
            {
                policy.setEnabled( false );

                guest = userManager.createUser( "guest", "Guest", "" );
                guest = userManager.addUser( guest );
            }
            finally
            {
                policy.setEnabled( true );
            }

            try
            {
                Role guestRole = roleProfileManager.getRole( "guest" );
                UserAssignment ua = rbacManager.createUserAssignment( guest.getPrincipal().toString() );
                ua.addRoleName( guestRole );
                rbacManager.saveUserAssignment( ua );

            }
            catch ( RoleProfileException rpe )
            {
                violations.add("unable to initialize guest user properly: " + rpe.getMessage() );
            }

            checked = true;
        }
    }
}
