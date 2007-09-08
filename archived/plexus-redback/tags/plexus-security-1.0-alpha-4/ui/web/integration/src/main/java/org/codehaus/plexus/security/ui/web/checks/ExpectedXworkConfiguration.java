package org.codehaus.plexus.security.ui.web.checks;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationManager;

import org.codehaus.plexus.security.system.check.EnvironmentCheck;
import org.codehaus.plexus.security.ui.web.checks.xwork.XworkPackageConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * ExpectedXworkConfiguration reason for existance is to validate that the executing
 * environment has everything needed for a proper execution of 
 * Plexus Security :: UI Web components and javascript and jsps.
 * </p>
 * 
 * <p>
 * It is quite possible for the environment overlay to have not been done.
 * Such as when using <code>"mvn jetty:run"</code>, but forgetting to run
 * <code>"mvn war:inplace"</code> first.
 * </p> 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.system.check.EnvironmentCheck"
 *                   role-hint="ExpectedStandardWebXwork"
 */
public class ExpectedXworkConfiguration
    extends AbstractXworkConfigurationCheck
    implements EnvironmentCheck
{
    public void validateEnvironment( List violations )
    {
        // Get the configuration.
        Configuration xworkConfig = ConfigurationManager.getConfiguration();

        if ( xworkConfig != null )
        {
            List internalViolations = new ArrayList();

            XworkPackageConfig expectedPackage = new XworkPackageConfig( "/security" );

            expectedPackage.addAction( "account", "pss-account", "show" ).addResult( "input" ).addResult( "error" )
                .addResult( "success" );

            expectedPackage.addAction( "login", "pss-login", "show" ).addResult( "input" ).addResult( "error" )
                .addResult( "success" );

            expectedPackage.addAction( "logout", "pss-logout", "show" ).addResult( "input" ).addResult( "error" )
                .addResult( "success" );

            expectedPackage.addAction( "register", "pss-register", "show" ).addResult( "input" ).addResult( "error" )
                .addResult( "success" );

            expectedPackage.addAction( "password", "pss-password", "show" ).addResult( "input" ).addResult( "error" )
                .addResult( "success" );

            // -----------------------------------------------------------------
            // Security Admin Tests
            
            expectedPackage.addAction( "systeminfo", "pss-sysinfo", "show" );
            expectedPackage.addAction( "adminConsole", "pss-admin-console", "show" );

            expectedPackage.addAction( "userlist", "pss-admin-user-list", "show" ).addResult( "input" )
                .addResult( "success" );

            expectedPackage.addAction( "useredit", "pss-admin-user-edit", "edit" ).addResult( "input" )
                .addResult( "error" ).addResult( "success" );

            expectedPackage.addAction( "usercreate", "pss-admin-user-create", "edit" ).addResult( "input" )
                .addResult( "error" ).addResult( "success" );

            expectedPackage.addAction( "userdelete", "pss-admin-user-delete", "confirm" ).addResult( "input" )
                .addResult( "error" ).addResult( "success" );

            expectedPackage.addAction( "assignments", "pss-assignments", "show" ).addResult( "input" )
                .addResult( "error" ).addResult( "success" );

            expectedPackage.addAction( "roles", "pss-roles", "show" ).addResult( "input" ).addResult( "error" )
                .addResult( "success" );

            expectedPackage.addAction( "permissions", "pss-permissions", "show" ).addResult( "input" )
                .addResult( "error" ).addResult( "success" );
            
            checkPackage( internalViolations, expectedPackage, xworkConfig );

            if ( internalViolations.size() > 0 )
            {
                violations.addAll( internalViolations );
                violations.add( "Missing [" + internalViolations.size() + "] xwork.xml configuration elements." );
            }
        }
        else
        {
            violations.add( "Missing xwork.xml configuration." );
        }
    }

}
