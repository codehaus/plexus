package org.codehaus.plexus.redback.example.web;

/*
 * Copyright 2001-2006 The Codehaus.
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
import org.codehaus.plexus.redback.policy.UserSecurityPolicy;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.rbac.UserAssignment;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.system.check.EnvironmentCheck;
import org.codehaus.plexus.redback.user.User;
import org.codehaus.plexus.redback.user.UserManager;
import org.codehaus.plexus.redback.user.UserNotFoundException;

import java.util.List;

/**
 * TestUsersEnvironmentCheck 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.redback.system.check.EnvironmentCheck"
 * role-hint="test-users-check"
 */
public class TestUsersEnvironmentCheck
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

    private void createUser( String username, String fullname, String email, String password, boolean locked,
                             boolean validated )
    {
        UserManager userManager = securitySystem.getUserManager();
        UserSecurityPolicy policy = securitySystem.getPolicy();

        User user;

        try
        {
            user = userManager.findUser( username );
        }
        catch ( UserNotFoundException e )
        {
            policy.setEnabled( false );
            user = userManager.createUser( username, fullname, email );
            user.setPassword( password );
            user.setLocked( locked );
            user.setValidated( validated );
            user = userManager.addUser( user );
        }
        finally
        {
            policy.setEnabled( true );
        }

        try
        {
            Role registeredUserRole = roleProfileManager.getRole( "registered-user" );
            UserAssignment ua = rbacManager.createUserAssignment( user.getPrincipal().toString() );
            ua.addRoleName( registeredUserRole );
            rbacManager.saveUserAssignment( ua );
        }
        catch ( RoleProfileException e )
        {
            getLogger().warn( "Unable to set role: ", e );
        }
        catch ( RbacManagerException e )
        {
            getLogger().warn( "System error:", e );
        }
    }

    public void validateEnvironment( List violations )
    {

        if ( !checked )
        {
            createUser( "testuser1", "Test User 1", "test.user@gmail.com", "pass123", false, true );
            createUser( "testuser2", "Test User 2", "test,user.2@gmail.com", "pass123", false, true );
            
            // Intentionally create bad users to test CSV reporting
            createUser( "csvtest1", "CSV Test \"User\" 1", "csv.1@gmail.com", "pass123", false, false );
            createUser( "csvtest2", "CSV Test \t'Pau 2", "csv.2@gmail.com", "pass123", false, true );
            createUser( "csvtest3", "CSV Test User \next Generation 3", "csv.3@gmail.com", "pass123", true, true );

            // Filmed On Location
            createUser( "amy", "Amy Wong", "amy@kapa.kapa.wong.mars", "guh!", false, true );
            createUser( "bender", "Bender Bending Rodriguez", "bender@planetexpress.com", "elzarRox", 
                        false, true );
            createUser( "leela", "Turanga Leela", "leela@planetexpress.com", "orphanarium", 
                        false, true );
            createUser( "fry", "Philip J. Fry", "fry@planetexpress.com", "cool", false, true );
            createUser( "kif", "Kif Krooker", "kif@nimbus.doop.mil", "sigh", false, true );
            createUser( "zapp", "Zapp Brannigan", "zapp@nimbus.doop.mil", "leela", false, false );
            createUser( "elzar", "Elzar", "elzar@elzarscuisine.com", "BAM!", false, true );
            createUser( "mom", "Mom", "mom@momsfriendlyrobotcompany.com", "heartless", false, true );
            createUser( "nibbler", "Lord Nibbler", "nibbler@planetexpress.com", "growl", false, false );
            createUser( "hermes", "Hermes Conrad", "hermes@bureaucrat.planetexpress.com", "groovy", 
                        false, true );
            createUser( "hubert", "Professor Hubert J. Farnsworth", "owner@planetexpress.com", 
                        "doomsday", false, true );
            createUser( "zoidberg", "Dr. John Zoidberg", "doctor@planetexpress.com", "sayargh", 
                        true, false );

            checked = true;
        }
    }

}
