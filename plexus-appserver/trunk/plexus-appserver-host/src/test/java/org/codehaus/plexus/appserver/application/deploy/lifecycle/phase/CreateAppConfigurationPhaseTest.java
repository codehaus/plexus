package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;

import java.io.File;

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

public class CreateAppConfigurationPhaseTest
    extends PlexusTestCase
{
    private CreateAppConfigurationPhase phase;

    private AppDeploymentContext context;

    private String appServerHome;

    private ApplicationServer appServer;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        DefaultPlexusContainer appServerContainer = (DefaultPlexusContainer) getContainer();
        appServerHome = (String) appServerContainer.getContext().get( "plexus.home" );
        appServer = (ApplicationServer) lookup( ApplicationServer.ROLE );

        File applicationsDirectory = new File( appServerHome, "apps" );
        context = new AppDeploymentContext( new File( applicationsDirectory, "dummy.jar" ), applicationsDirectory, null,
                                            appServerContainer, appServer, false );
        File appDir = new File( applicationsDirectory, "dummy" );
        context.setAppConfigurationFile( new File( appDir, "conf/application.xml" ) );
        context.setAppDir( appDir );

        phase = (CreateAppConfigurationPhase) lookup( AppDeploymentPhase.ROLE, "create-app-configuration" );
    }

    public void testAppServerDefaultHome()
        throws Exception
    {
        phase.execute( context );

        assertEquals( "Check appserver.home", appServerHome,
                      context.getContextValues().get( "appserver.home" ) );
        assertEquals( "Check appServer.appServerHome", appServerHome, appServer.getAppServerHome().getAbsolutePath() );
    }

    public void testAppServerDefaultBase()
        throws Exception
    {
        phase.execute( context );

        assertEquals( "Check appserver.base", appServerHome,
                      context.getContextValues().get( "appserver.base" ) );
        assertEquals( "Check appServer.appServerBase", appServerHome, appServer.getAppServerBase().getAbsolutePath() );
    }

    public void testAppServerBase()
        throws Exception
    {
        File appServerBase = getTestFile( "target/appserver-base" );
        appServer.setAppServerBase( appServerBase );

        phase.execute( context );

        assertEquals( "Check appserver.base", appServerBase.getAbsolutePath(),
                      context.getContextValues().get( "appserver.base" ) );
        assertEquals( "Check appServer.appServerBase", appServerBase.getAbsolutePath(),
                      appServer.getAppServerBase().getAbsolutePath() );
    }
}
