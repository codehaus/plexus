package org.codehaus.plexus.redback.xwork.checks.security;

/*
 * Copyright 2005-2006 The Codehaus.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.UserAssignment;
import org.codehaus.plexus.redback.system.check.EnvironmentCheck;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.xwork.role.RoleConstants;

/**
 * LockedAdminEnvironmentCheck: checks if accounts marked as system administrator are locked
 * and unlocks them on startup.
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * @plexus.component role="org.codehaus.plexus.redback.system.check.EnvironmentCheck"
 * role-hint="locked-admin-check"
 */
public class LockedAdminEnvironmentCheck extends AbstractLogEnabled implements EnvironmentCheck
{

    /**
     * @plexus.requirement role-hint="configurable"
     */
    private UserManager userManager;

    /**
     * @plexus.requirement role-hint="cached"
     */
    private RBACManager rbacManager;

    /**
     * boolean detailing if this environment check has been executed
     */
    private boolean checked = false;

    /**
     * This environment check will unlock system administrator accounts that are locked on the restart of the
     * application when the environment checks are processed.
     * 
     * @param violations
     */
    public void validateEnvironment( List violations )
    {
        if ( !checked )
        {
            List roles = new ArrayList();
            roles.add( RoleConstants.SYSTEM_ADMINISTRATOR_ROLE );

            List systemAdminstrators;
            try
            {
                systemAdminstrators = rbacManager.getUserAssignmentsForRoles( roles );
                
                for ( Iterator i = systemAdminstrators.iterator(); i.hasNext(); )
                {
                    UserAssignment userAssignment = (UserAssignment)i.next();
                    
                    try
                    {
                        User admin = userManager.findUser( userAssignment.getPrincipal() );
                        
                        if ( admin.isLocked() )
                        {
                            getLogger().info( "Found locked system administrator: " + admin.getUsername() );
                            getLogger().info(  "Unlocking system administrator: " + admin.getUsername() );
                            admin.setLocked( false );
                            userManager.updateUser( admin );
                        }
                    }
                    catch ( UserNotFoundException ne )
                    {
                        getLogger().warn( "Dangling UserAssignment -> " + userAssignment.getPrincipal() );
                        violations.add( ne );
                    }
                }
            }
            catch ( RbacManagerException e )
            {                
                violations.add( e );
            }
            
            checked = true;
        }
    }
}