package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class ValidateAppPhaseTest
    extends PlexusTestCase
{
    private ValidateAppPhase phase;

    private AppDeploymentContext context;

    private String appServerHome;

    private File applicationsDirectory;

    private ApplicationServer appServer;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        DefaultPlexusContainer appServerContainer = (DefaultPlexusContainer) getContainer();

        appServerHome = (String) appServerContainer.getContext().get( "plexus.home" );

        appServer = (ApplicationServer) lookup( ApplicationServer.ROLE );
        appServer.setAppServerBase( new File( appServerHome ) );

        applicationsDirectory = new File( appServerHome, "apps" );
        context = new AppDeploymentContext( new File( applicationsDirectory, "dummy.jar" ), applicationsDirectory, null,
                                            appServerContainer, appServer, false );

        phase = (ValidateAppPhase) lookup( AppDeploymentPhase.ROLE, "validate-app" );
    }

    public void testBuiltInConfiguration()
        throws AppDeploymentException, IOException
    {
        String appId = "built-in-configuration";
        context.setApplicationId( appId );
        File appDir = new File( applicationsDirectory, appId );
        context.setAppDir( appDir );
        new File( appDir, "lib" ).mkdirs();

        File confFile = new File( appDir, "conf/application.xml" );
        confFile.getParentFile().mkdirs();
        FileUtils.fileWrite( confFile.getAbsolutePath(), appId );

        phase.execute( context );

        assertEquals( "Check app config file", context.getAppConfigurationFile(), confFile );
    }

    public void testOverriddenConfiguration()
        throws AppDeploymentException, IOException
    {
        String appId = "overridden-configuration";
        context.setApplicationId( appId );
        File appDir = new File( applicationsDirectory, appId );
        File origConfFile = new File( appDir, "conf/application.xml" );
        origConfFile.getParentFile().mkdirs();
        FileUtils.fileWrite( origConfFile.getAbsolutePath(), appId );
        context.setAppDir( appDir );
        new File( appDir, "lib" ).mkdirs();

        File confFile = new File( appServerHome, "conf/overridden-configuration/application.xml" );
        confFile.getParentFile().mkdirs();
        FileUtils.fileWrite( confFile.getAbsolutePath(), appId );

        phase.execute( context );

        assertEquals( "Check app config file", context.getAppConfigurationFile(), confFile );
    }

    public void testMissingConfiguration()
    {
        String appId = "no-configuration";
        context.setApplicationId( appId );
        File appDir = new File( applicationsDirectory, appId );
        File origConfFile = new File( appDir, "conf/application.xml" );
        origConfFile.getParentFile().mkdirs();
        context.setAppDir( appDir );
        new File( appDir, "lib" ).mkdirs();

        File confFile = new File( appServerHome, "conf/overridden-configuration/application.xml" );
        confFile.getParentFile().mkdirs();

        try
        {
            phase.execute( context );
            fail( "Should have failed with an exception" );
        }
        catch ( AppDeploymentException e )
        {
            // expected
            assertTrue( e.getMessage().indexOf( "configurator" ) > 0 );
        }
    }

    public void testMissingLibDirectory()
        throws IOException
    {
        String appId = "no-lib";
        context.setApplicationId( appId );
        File appDir = new File( applicationsDirectory, appId );
        context.setAppDir( appDir );

        File confFile = new File( appDir, "conf/application.xml" );
        confFile.getParentFile().mkdirs();
        FileUtils.fileWrite( confFile.getAbsolutePath(), appId );

        try
        {
            phase.execute( context );
            fail( "Should have failed with an exception" );
        }
        catch ( AppDeploymentException e )
        {
            // expected
            assertTrue( e.getMessage().indexOf( "library" ) > 0 );
        }
    }
}
