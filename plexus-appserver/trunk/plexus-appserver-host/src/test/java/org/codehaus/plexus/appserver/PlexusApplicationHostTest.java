package org.codehaus.plexus.appserver;

import junit.framework.TestCase;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.net.URL;

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

public class PlexusApplicationHostTest
    extends TestCase
{
    private String plexusHome;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        System.setProperty( "plexus.home", "target/plexus-home" );

        startHost();
        plexusHome = new File( "target/plexus-home" ).getAbsolutePath();
    }

    public void testPlexusHome()
        throws Exception
    {
        PlexusApplicationHost host = startHost();

        assertEquals( "Check plexus.home", plexusHome, host.getContainer().getContext().get( "plexus.home" ) );
    }

    public void testDefaultHome()
        throws Exception
    {
        FileUtils.deleteDirectory( new File( plexusHome, "logs" ) );
        FileUtils.deleteDirectory( new File( plexusHome, "temp" ) );

        PlexusApplicationHost host = startHost();

        assertEquals( "Check appserver.home", plexusHome, host.getContainer().getContext().get( "appserver.home" ) );
        assertBase( plexusHome, host.getContainer().getContext() );
    }

    public void testDefaultBase()
        throws Exception
    {
        String appserverHome = new File( "target/appserver-home" ).getAbsolutePath();
        System.setProperty( "appserver.home", "target/appserver-home" );

        FileUtils.deleteDirectory( new File( appserverHome, "logs" ) );
        FileUtils.deleteDirectory( new File( appserverHome, "temp" ) );

        PlexusApplicationHost host = startHost();

        assertEquals( "Check appserver.home", appserverHome, host.getContainer().getContext().get( "appserver.home" ) );
        assertBase( appserverHome, host.getContainer().getContext() );
    }

    public void testAppserverBase()
        throws Exception
    {
        String appserverHome = new File( "target/appserver-home" ).getAbsolutePath();
        System.setProperty( "appserver.home", "target/appserver-home" );

        String appserverBase = new File( "target/appserver-base" ).getAbsolutePath();
        System.setProperty( "appserver.base", "target/appserver-base" );

        FileUtils.deleteDirectory( new File( appserverBase, "logs" ) );
        FileUtils.deleteDirectory( new File( appserverBase, "temp" ) );

        PlexusApplicationHost host = startHost();

        assertEquals( "Check appserver.home", appserverHome, host.getContainer().getContext().get( "appserver.home" ) );
        assertBase( appserverBase, host.getContainer().getContext() );
    }

    public void testMissingConfigFile()
    {
        PlexusApplicationHost host = new PlexusApplicationHost();
        try
        {
            host.start( getClassWorld() );
            fail( "Shouldn't find a config file" );
        }
        catch ( Exception e )
        {
            assertEquals( "Unable to find a default configuration file", e.getMessage() );
        }
    }

    public void testBaseConfigFile()
        throws Exception
    {
        File appserverBase = new File( "target/appserver-base" );
        File confDir = new File( appserverBase, "conf" );
        confDir.mkdirs();
        File confFile = new File( confDir, "plexus.xml" );
        FileUtils.copyFile( new File( getResourceFile( "/plexus.xml" ) ), confFile );
        System.setProperty( "appserver.base", appserverBase.getAbsolutePath() );

        File appserverHome = new File( "target/appserver-home" );
        confDir = new File( appserverHome, "conf" );
        confDir.mkdirs();
        FileUtils.copyFile( new File( getResourceFile( "/plexus.xml" ) ), new File( confDir, "plexus.xml" ) );
        System.setProperty( "appserver.home", appserverHome.getAbsolutePath() );

        PlexusApplicationHost host = new PlexusApplicationHost();
        host.start( getClassWorld() );

        assertEquals( "Check config file used is in base", confFile.getAbsolutePath(), host.getConfigurationResource() );
    }

    public void testHomeConfigFile()
        throws Exception
    {
        File appserverBase = new File( "target/clean-appserver-base" );
        System.setProperty( "appserver.base", appserverBase.getAbsolutePath() );

        File appserverHome = new File( "target/appserver-home" );
        File confDir = new File( appserverHome, "conf" );
        confDir.mkdirs();
        File confFile = new File( confDir, "plexus.xml" );
        FileUtils.copyFile( new File( getResourceFile( "/plexus.xml" ) ), confFile );
        System.setProperty( "appserver.home", appserverHome.getAbsolutePath() );

        PlexusApplicationHost host = new PlexusApplicationHost();
        host.start( getClassWorld() );

        assertEquals( "Check config file used is in base", confFile.getAbsolutePath(), host.getConfigurationResource() );
    }

    private PlexusApplicationHost startHost()
        throws Exception
    {
        PlexusApplicationHost host = new PlexusApplicationHost();
        host.start( getClassWorld(), getResourceFile( "/plexus.xml" ) );
        return host;
    }

    private ClassWorld getClassWorld()
    {
        ClassWorld world = new ClassWorld( "plexus.core", getClass().getClassLoader() );
        return world;
    }

    private void assertBase( String plexusBase, Context context )
        throws ContextException
    {
        assertEquals( "Check appserver.base", plexusBase, context.get( "appserver.base" ) );
        File logsDir = new File( plexusBase, "logs" );
        assertEquals( "Check plexus.logs", logsDir.getAbsolutePath(), context.get( "plexus.logs" ) );
        assertTrue( "Check logs created", logsDir.exists() && logsDir.isDirectory() );
        File tempDir = new File( plexusBase, "temp" );
        assertEquals( "Check plexus.temp", tempDir.getAbsolutePath(), context.get( "plexus.temp" ) );
        assertTrue( "Check temp created", tempDir.exists() && tempDir.isDirectory() );
        assertEquals( "Check plexus.work", new File( plexusBase, "work" ).getAbsolutePath(),
                      context.get( "plexus.work" ) );
    }

    private String getResourceFile( String path )
    {
        URL url = getClass().getResource( path );
        return url.getPath();
    }
}
