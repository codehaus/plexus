package org.codehaus.plexus.webdav.test;

/*
 * Copyright 2001-2007 The Codehaus.
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

import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.DepthSupport;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.webdav.BasicWebDavServlet;
import org.codehaus.plexus.webdav.DavServerManager;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.InputStream;

/**
 * AbstractWebdavProviderTestCase 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AbstractWebdavProviderTestCase
    extends PlexusTestCase
{
    private static final int PORT = 4321;

    private static final String CONTEXT = "/projects";

    private DavServerManager manager;

    private String providerHint = "simple";

    private File serverContentsDir;

    /**
     * The Jetty Server.
     */
    private Server server;

    private WebdavResource davResource;

    public DavServerManager getManager()
    {
        return manager;
    }

    public String getProviderHint()
    {
        return providerHint;
    }

    public void setManager( DavServerManager manager )
    {
        this.manager = manager;
    }

    public void setProviderHint( String providerHint )
    {
        this.providerHint = providerHint;
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
        manager = (DavServerManager) lookup( DavServerManager.ROLE, getProviderHint() );

        // Initialize server contents directory.
        serverContentsDir = new File( "target/test-contents/" + getName() );

        FileUtils.deleteDirectory( serverContentsDir );
        if ( serverContentsDir.exists() )
        {
            fail( "Unable to execute test, server contents test directory [" + serverContentsDir.getAbsolutePath()
                + "] exists, and cannot be deleted by the test case." );
        }

        if ( !serverContentsDir.mkdirs() )
        {
            fail( "Unable to execute test, server contents test directory [" + serverContentsDir.getAbsolutePath()
                + "] cannot be created." );
        }

        // Setup the Jetty Server.
        System.setProperty( "DEBUG", "" );
        System.setProperty( "org.mortbay.log.class", "org.slf4j.impl.SimpleLogger" );

        server = new Server( PORT );
        Context root = new Context( server, "/", Context.SESSIONS );

        root.setContextPath( "/" );
        root.setAttribute( PlexusConstants.PLEXUS_KEY, getContainer() );

        ServletHandler servletHandler = root.getServletHandler();

        ServletHolder holder = servletHandler.addServletWithMapping( BasicWebDavServlet.class, CONTEXT + "/*" );

        holder.setInitParameter( "dav.root", serverContentsDir.getAbsolutePath() );
    }

    protected void tearDown()
        throws Exception
    {
        serverContentsDir = null;

        if ( server != null )
        {
            try
            {
                server.stop();
            }
            catch ( Exception e )
            {
                /* ignore */
            }
            server = null;
        }

        if ( davResource != null )
        {
            try
            {
                davResource.close();
            }
            catch ( Exception e )
            {
                /* ignore */
            }

            davResource = null;
        }
        super.tearDown();
    }

    private void dumpCollection( WebdavResource webdavResource, String path )
        throws Exception
    {
        webdavResource.setPath( path );
        WebdavResource resources[] = webdavResource.listWebdavResources();

        System.out.println( "Dump Collection [" + path + "]: " + resources.length + " hits." );

        dumpCollectionRecursive( "", webdavResource, path );
    }

    private void dumpCollectionRecursive( String indent, WebdavResource webdavResource, String path )
        throws Exception
    {
        if ( indent.length() > 12 )
        {
            return;
        }

        WebdavResource resources[] = webdavResource.listWebdavResources();

        for ( int i = 0; i < resources.length; i++ )
        {
            System.out.println( indent + "WebDavResource[" + path + "|" + i + "]: "
                + ( resources[i].isCollection() ? "(collection) " : "" ) + resources[i].getName() );

            if ( resources[i].isCollection() )
            {
                dumpCollectionRecursive( indent + "  ", resources[i], path + "/" + resources[i].getName() );
            }
        }
    }

    private void assertMethod( String msg, boolean expectedState, boolean actualState )
    {
        if ( expectedState != actualState )
        {
            int statusCode = davResource.getStatusCode();
            String statusMessage = davResource.getStatusMessage();
            fail( "Unable to process method as expected [" + msg + "] expected [" + expectedState + "] actual ["
                + actualState + "], status code [" + statusCode + "], status message [" + statusMessage + "]." );
        }
    }

    // --------------------------------------------------------------------
    // Actual Test Cases.
    // --------------------------------------------------------------------

    public void testPutGet()
        throws Exception
    {
        FileUtils.fileWrite( new File( serverContentsDir, "data.txt" ).getAbsolutePath(), "yo!" );

        server.start();

        HttpURL httpUrl = new HttpURL( "http://localhost:" + PORT + CONTEXT );

        davResource = new WebdavResource( httpUrl );
        davResource.setPath( CONTEXT );

        assertMethod( "Put [/data.txt]", true, davResource.putMethod( CONTEXT + "/data.txt", "yo!\n" ) );

        dumpCollection( davResource, CONTEXT );

        InputStream inputStream = davResource.getMethodData( CONTEXT + "/data.txt" );

        assertEquals( "yo!\n", IOUtil.toString( inputStream ) );

        server.stop();
    }

    public void testCollectionTasks()
        throws Exception
    {
        server.start();

        HttpURL httpUrl = new HttpURL( "http://localhost:" + PORT + CONTEXT );

        davResource = new WebdavResource( httpUrl );
        davResource.setPath( CONTEXT );

        // Pull up a collection list.
        String resources[] = davResource.list();
        assertNotNull( resources );
        assertEquals( 0, resources.length );

        // Create a few collections.
        assertMethod( "Create Collection '/bar'", true, davResource.mkcolMethod( CONTEXT + "/bar" ) );
        assertMethod( "Create Collection '/bar/foo'", true, davResource.mkcolMethod( CONTEXT + "/bar/foo" ) );

        // Test for collection via webdav interface.
        resources = davResource.list();
        assertNotNull( resources );
        assertEquals( 1, resources.length );

        davResource.setPath( CONTEXT + "/bar" );
        // Needed for .exists() to function.
        davResource.setProperties( WebdavResource.NAME, DepthSupport.DEPTH_0 );

        assertTrue( "DAV Resource [/bar] should exist.", davResource.exists() );
        assertTrue( "DAV Resource [/bar] should be a collection.", davResource.isCollection() );

        // Test for existance of directories on disk.
        File expectedDir = new File( serverContentsDir, "bar/foo" );
        assertTrue( "Directory/Collection bar/foo should exist.", expectedDir.exists() && expectedDir.isDirectory() );

        server.stop();
    }

    public void testResourceCopy()
        throws Exception
    {
        server.start();

        HttpURL httpUrl = new HttpURL( "http://localhost:" + PORT + CONTEXT );

        davResource = new WebdavResource( httpUrl );
        davResource.setPath( CONTEXT );
        davResource.setDebug( 1 );

        // Create a few collections.
        assertMethod( "Create Collection '/bar'", true, davResource.mkcolMethod( CONTEXT + "/bar" ) );
        assertMethod( "Create Collection '/foo'", true, davResource.mkcolMethod( CONTEXT + "/foo" ) );

        // Create a resource
        davResource.setPath( CONTEXT + "/bar" );
        assertMethod( "Put [/bar/data.txt]", true, davResource.putMethod( CONTEXT + "/bar/data.txt", "yo!" ) );

        // Test for existance of resource on disk.
        assertTrue( "Existance of [/bar/data.txt]", new File( serverContentsDir, "bar/data.txt" ).exists() );
        assertFalse( "Existance of [/foo/data.txt]", new File( serverContentsDir, "foo/data.txt" ).exists() );

        // Move resource
        davResource.setPath( CONTEXT );
        assertMethod( "Copy Resource", true, davResource.copyMethod( CONTEXT + "/bar/data.txt", CONTEXT
            + "/foo/data.txt" ) );

        // Test for existance of resource on disk.
        assertTrue( "Existance of [/bar/data.txt]", new File( serverContentsDir, "bar/data.txt" ).exists() );
        assertTrue( "Existance of [/foo/data.txt]", new File( serverContentsDir, "foo/data.txt" ).exists() );

        // Test for existance via webdav interface.
        davResource.setPath( CONTEXT + "/bar" );
        String resources[] = davResource.list();
        assertNotNull( resources );
        assertEquals( 1, resources.length );

        davResource.setPath( CONTEXT + "/foo" );
        resources = davResource.list();
        assertNotNull( resources );
        assertEquals( 1, resources.length );

        server.stop();
    }

    public void testResourceMove()
        throws Exception
    {
        server.start();

        HttpURL httpUrl = new HttpURL( "http://localhost:" + PORT + CONTEXT );

        davResource = new WebdavResource( httpUrl );
        davResource.setPath( CONTEXT );

        // Create a few collections.
        assertMethod( "Create Collection '/bar'", true, davResource.mkcolMethod( CONTEXT + "/bar" ) );
        assertMethod( "Create Collection '/foo'", true, davResource.mkcolMethod( CONTEXT + "/foo" ) );

        // Create a resource
        davResource.setPath( CONTEXT + "/bar" );
        assertMethod( "Put [/bar/data.txt]", true, davResource.putMethod( CONTEXT + "/bar/data.txt", "yo!" ) );

        // Test for existance of resource on disk.
        assertTrue( "Existance of [/bar/data.txt]", new File( serverContentsDir, "bar/data.txt" ).exists() );
        assertFalse( "Existance of [/foo/data.txt]", new File( serverContentsDir, "foo/data.txt" ).exists() );

        // Move resource
        davResource.setPath( CONTEXT );
        assertMethod( "Move Resource", true, davResource.moveMethod( CONTEXT + "/bar/data.txt", "foo/data.txt" ) );

        // Test for existance of resource on disk.
        assertFalse( "Existance of [/bar/data.txt]", new File( serverContentsDir, "bar/data.txt" ).exists() );
        assertTrue( "Existance of [/foo/data.txt]", new File( serverContentsDir, "foo/data.txt" ).exists() );

        // Test for existance via webdav interface.
        davResource.setPath( CONTEXT + "/bar" );
        String resources[] = davResource.list();
        assertNotNull( resources );
        assertEquals( 0, resources.length );

        davResource.setPath( CONTEXT + "/foo" );
        resources = davResource.list();
        assertNotNull( resources );
        assertEquals( 1, resources.length );

        server.stop();
    }

    public void testResourceDelete()
        throws Exception
    {
        server.start();

        HttpURL httpUrl = new HttpURL( "http://localhost:" + PORT + CONTEXT );

        davResource = new WebdavResource( httpUrl );
        davResource.setPath( CONTEXT );

        // Create a few collections.
        assertMethod( "Create Collection '/bar'", true, davResource.mkcolMethod( CONTEXT + "/bar" ) );

        // Create a resource
        davResource.setPath( CONTEXT + "/bar" );
        assertMethod( "Put [/bar/data.txt]", true, davResource.putMethod( CONTEXT + "/bar/data.txt", "yo!" ) );

        // Test for existance of resource on disk.
        assertTrue( "Existance of [/bar/data.txt]", new File( serverContentsDir, "bar/data.txt" ).exists() );

        // Move resource
        davResource.setPath( CONTEXT );
        assertMethod( "Delete Resource", true, davResource.deleteMethod( CONTEXT + "/bar/data.txt" ) );

        // Test for existance via webdav interface.
        davResource.setPath( CONTEXT + "/bar" );
        String resources[] = davResource.list();
        assertNotNull( resources );
        assertEquals( 0, resources.length );

        // Test for existance of resource on disk.
        assertFalse( "Existance of [/bar/data.txt]", new File( serverContentsDir, "bar/data.txt" ).exists() );

        server.stop();
    }
}
