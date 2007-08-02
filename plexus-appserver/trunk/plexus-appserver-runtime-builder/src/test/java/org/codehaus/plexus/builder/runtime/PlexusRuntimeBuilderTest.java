package org.codehaus.plexus.builder.runtime;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import org.codehaus.plexus.builder.AbstractBuilderTest;
import org.codehaus.plexus.util.PropertyUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusRuntimeBuilderTest
    extends AbstractBuilderTest
{

    public void testRuntimeBuilder()
        throws Exception
    {
        PlexusRuntimeBuilder runtimeBuilder = (PlexusRuntimeBuilder) lookup( PlexusRuntimeBuilder.ROLE );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File plexusConfiguration = getTestFile( "src/test/resources/conf/plexus.xml" );

        File configurationPropertiesFile = getTestFile( "src/test/resources/configuration.properties" );

        Properties configurationProperties = PropertyUtils.loadProperties( configurationPropertiesFile );

        runtimeBuilder.build( getWorkingDirectory(), new ArrayList(), getLocalArtifactRepository(),
                              getProjectArtifacts(), getAdditionalCoreArtifacts(), plexusConfiguration,
                              null, configurationProperties, false );

        // assert on script in bin
        File shLauncher = new File( getWorkingDirectory(), "bin/plexus.sh" );
        assertTrue( "plexus.sh not in bin", shLauncher.exists() );

        File batLauncher = new File( getWorkingDirectory(), "/bin/plexus.bat" );
        assertTrue( "plexus.bat not in bin", batLauncher.exists() );

        // assert on conf directory
        File classworldConf = new File( getWorkingDirectory(), "conf/classworlds.conf" );
        assertTrue( "classworlds.conf not in conf directory", classworldConf.exists() );

        File plexusConf = new File( getWorkingDirectory(), "conf/plexus.xml" );
        assertTrue( "plexus.xml not in conf directory", plexusConf.exists() );

        // assert classworlds in core/boot
        File classWorldsJar = new File( getWorkingDirectory(), "core/boot/plexus-classworlds-1.2-alpha-7.jar" );
        assertTrue( "classworlds not in core/boot", classWorldsJar.exists() );

        // assert on core artifacts
        // container(s), appserver-host and plexus-utils
        File containerJar = new File( getWorkingDirectory(), "core/plexus-container-default-1.0-alpha-18.jar" );
        assertTrue( "plexus-container-default not in core dir", containerJar.exists() );

        File appHost = new File( getWorkingDirectory(), "core/plexus-appserver-host-2.0-alpha-8-SNAPSHOT.jar" );
        assertTrue( "plexus-appserver-host not in core dir", appHost.exists() );

        File plexusUtils = new File( getWorkingDirectory(), "core/plexus-utils-1.4.jar" );
        assertTrue( "plexus-utils not in core dir", plexusUtils.exists() );

        // assert on additionalCoreArtifacts
        File commonsLogging = new File( getWorkingDirectory(), "core/commons-logging-1.0.4.jar" );
        assertTrue( "commons-logging not in core dir", commonsLogging.exists() );

        File log4j = new File( getWorkingDirectory(), "core/log4j-1.2.8.jar" );
        assertTrue( "log4j not in core dir", log4j.exists() );

        File mail = new File( getWorkingDirectory(), "core/mail-1.4.jar" );
        assertTrue( "mail not in core dir", mail.exists() );

        //assert on logs directory
        File logsDir = new File( getWorkingDirectory(), "logs" );
        assertTrue( "no logs directory", logsDir.exists() );

        //assert on services directory
        File servicesDir = new File( getWorkingDirectory(), "services" );
        assertTrue( "no services directory", servicesDir.exists() );

        //assert on temp directory
        File tempDir = new File( getWorkingDirectory(), "temp" );
        assertTrue( "no temp directory", tempDir.exists() );

        // assert on add plexus-application
        runtimeBuilder.addPlexusApplication( getPlexusApplication(), getWorkingDirectory() );
        File appsDirectory = new File( getWorkingDirectory(), "apps" );
        assertTrue( "no apps directory", appsDirectory.exists() );
        assertEquals( "not only one file in apps dir", 1, appsDirectory.list().length );
        File appArtifact = new File( appsDirectory, "bar-1.0.jar" );
        assertTrue( "bar-1.0.jar not in apps directory", appArtifact.exists() );

        // assert on add plexus services
        runtimeBuilder.addPlexusService( getPlexusService(), getWorkingDirectory() );
        File servicesDirectory = new File( getWorkingDirectory(), "services" );
        assertTrue( "no services directory", servicesDirectory.exists() );
        assertEquals( "not only one file in services dir", 1, servicesDirectory.list().length );
        File serviceArtifact = new File( servicesDirectory, "plexus-appserver-service-jetty-2.0-alpha-8-SNAPSHOT.sar" );
        assertTrue( "plexus-appserver-service-jetty-2.0-alpha-8-SNAPSHOT.sar not in services directory",
                    serviceArtifact.exists() );

    }

}
