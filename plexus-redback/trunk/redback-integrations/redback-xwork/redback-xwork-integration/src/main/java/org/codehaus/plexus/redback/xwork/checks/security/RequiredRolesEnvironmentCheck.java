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

import java.util.List;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.system.check.EnvironmentCheck;

/**
 * RequiredRolesEnvironmentCheck: this environment check will check that the
 * required roles of the redback-xwork-integration artifact exist to be
 * assigned.
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 * @plexus.component role="org.codehaus.plexus.redback.system.check.EnvironmentCheck"
 * role-hint="required-roles"
 */
public class RequiredRolesEnvironmentCheck
    extends AbstractLogEnabled
    implements EnvironmentCheck
{

    /**
     * @plexus.requirement role-hint=default
     */
    private RBACManager rbacManager;

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
            
            if ( rbacManager.roleExists( "registered-user" ) )
            {
               violations.add( "unable to validate existence of the registered-user role" );
            }
            
            if ( rbacManager.roleExists( "user-administrator" ) )
            {
               violations.add( "unable to validate existence of the user-administator role" );
            }
            
            if ( rbacManager.roleExists( "system-administrator" ) )
            {
               violations.add( "unable to validate existence of the system-administrator role" );
            }
           
            checked = true;
        }
    }
}