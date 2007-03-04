package org.codehaus.plexus.maven.plugin.runtime;

import java.io.File;

/*
 * Copyright 2007 The Codehaus Foundation.
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
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 2 mars 07
 * @version $Id$
 */
public class RuntimeAssemblerMojoTest
    extends AbstractAppServerMojoTest
{

    public void testContinuumRuntimeAssembler()
        throws Exception
    {

        RuntimeAssemblerMojo mojo = (RuntimeAssemblerMojo) getAppServerMojo( "src/test/unit/plexus-runtime/pom.xml",
                                                                             "assemble-runtime" );

        mojo.execute();

        // assert on script in bin
        File shLauncher = new File( getBasedir(), "target/plexus-runtime/bin/plexus.sh" );
        assertTrue( "plexus.sh not in bin", shLauncher.exists() );

        File batLauncher = new File( getBasedir(), "target/plexus-runtime/bin/plexus.bat" );
        assertTrue( "plexus.bat not in bin", batLauncher.exists() );

        // assert on conf directory
        File classworldConf = new File( getBasedir(), "target/plexus-runtime/conf/classworlds.conf" );
        assertTrue( "classworlds.conf not in conf directory", classworldConf.exists() );

        File plexusConf = new File( getBasedir(), "target/plexus-runtime/conf/plexus.xml" );
        assertTrue( "plexus.xml not in conf directory", plexusConf.exists() );

        // assert classworlds in core/boot
        File classWorldsJar = new File( getBasedir(),
                                        "target/plexus-runtime/core/boot/plexus-classworlds-1.2-alpha-7.jar" );
        assertTrue( "classworlds not in core/boot", classWorldsJar.exists() );

        // assert on core artifacts 
        // container(s), appserver-host and plexus-utils
        File componentApiJar = new File( getBasedir(),
                                         "target/plexus-runtime/core/plexus-component-api-1.0-alpha-18.jar" );
        assertTrue( "plexus-component-api not in core dir", componentApiJar.exists() );

        File containerJar = new File( getBasedir(),
                                      "target/plexus-runtime/core/plexus-container-default-1.0-alpha-18.jar" );
        assertTrue( "plexus-container-default not in core dir", containerJar.exists() );

        File appHost = new File( getBasedir(),
                                 "target/plexus-runtime/core/plexus-appserver-host-2.0-alpha-8-SNAPSHOT.jar" );
        assertTrue( "plexus-appserver-host not in core dir", appHost.exists() );

        File plexusUtils = new File( getBasedir(), "target/plexus-runtime/core/plexus-utils-1.4.jar" );
        assertTrue( "plexus-utils not in core dir", plexusUtils.exists() );

        // assert on additionalCoreArtifacts
        File commonsLogging = new File( getBasedir(), "target/plexus-runtime/core/commons-logging-api-1.0.4.jar" );
        assertTrue( "commons-logging-api not in core dir", commonsLogging.exists() );

        File log4j = new File( getBasedir(), "target/plexus-runtime/core/log4j-1.2.8.jar" );
        assertTrue( "log4j not in core dir", log4j.exists() );
        
        File mail = new File( getBasedir(), "target/plexus-runtime/core/mail-1.4.jar" );
        assertTrue( "mail not in core dir", mail.exists() );
        
        //assert on logs directory
        File logsDir = new File( getBasedir(), "target/plexus-runtime/logs" );
        assertTrue( "no logs directory", logsDir.exists() );
        
        //assert on services directory
        File servicesDir = new File( getBasedir(), "target/plexus-runtime/services" );
        assertTrue( "no services directory", servicesDir.exists() );
        
        //assert on temp directory
        File tempDir = new File( getBasedir(), "target/plexus-runtime/temp" );
        assertTrue( "no temp directory", tempDir.exists() );
    }

}
