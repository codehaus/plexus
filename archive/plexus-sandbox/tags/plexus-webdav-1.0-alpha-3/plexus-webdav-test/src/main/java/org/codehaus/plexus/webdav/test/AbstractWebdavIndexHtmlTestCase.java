package org.codehaus.plexus.webdav.test;

import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.WebdavResource;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.webdav.servlet.basic.BasicWebDavServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.File;

public class AbstractWebdavIndexHtmlTestCase
    extends AbstractWebdavProviderTestCase
{
    private File serverRepoDir;

    private WebdavResource davRepo;

    /** The Jetty Server. */
    private Server server;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        // Initialize server contents directory.

        serverRepoDir = getTestDir( "sandbox" );

        // Setup the Jetty Server.

        System.setProperty( "DEBUG", "" );
        System.setProperty( "org.mortbay.log.class", "org.slf4j.impl.SimpleLogger" );

        server = new Server( PORT );
        Context rootJettyContext = new Context( server, "/", Context.SESSIONS );

        rootJettyContext.setContextPath( "/" );
        rootJettyContext.setAttribute( PlexusConstants.PLEXUS_KEY, getContainer() );

        ServletHandler servletHandler = rootJettyContext.getServletHandler();

        ServletHolder holder = servletHandler.addServletWithMapping( BasicWebDavServlet.class, CONTEXT + "/*" );

        holder.setInitParameter( BasicWebDavServlet.INIT_ROOT_DIRECTORY, serverRepoDir.getAbsolutePath() );
        holder.setInitParameter( BasicWebDavServlet.INIT_USE_INDEX_HTML, "true" );

        server.start();

        // Setup Client Side

        HttpURL httpSandboxUrl = new HttpURL( "http://localhost:" + PORT + CONTEXT + "/" );

        davRepo = new WebdavResource( httpSandboxUrl );

        davRepo.setDebug( 8 );

        davRepo.setPath( CONTEXT );
    }

    protected void tearDown()
        throws Exception
    {
        serverRepoDir = null;

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

        if ( davRepo != null )
        {
            try
            {
                davRepo.close();
            }
            catch ( Exception e )
            {
                /* ignore */
            }

            davRepo = null;
        }

        super.tearDown();
    }

    public void testCollectionIndexHtml()
        throws Exception
    {
        // Lyrics: Colin Hay - Overkill
        String contents = "I cant get to sleep\n" + "I think about the implications\n" + "Of diving in too deep\n"
            + "And possibly the complications\n" + "Especially at night\n" + "I worry over situations\n"
            + "I know will be alright\n" + "Perahaps its just my imagination\n" + "Day after day it reappears\n"
            + "Night after night my heartbeat, shows the fear\n" + "Ghosts appear and fade away";

        // Create a few collections.
        assertDavMkDir( davRepo, CONTEXT + "/bar" );
        assertDavMkDir( davRepo, CONTEXT + "/foo" );

        // Create a resource
        assertDavTouchFile( davRepo, CONTEXT + "/bar", "index.html", contents );

        // Test for existance of resource
        assertDavFileExists( davRepo, CONTEXT + "/bar", "index.html" );
        assertDavFileNotExists( davRepo, CONTEXT + "/foo", "index.html" );

        // Copy resource
        String actual = davRepo.getMethodDataAsString( CONTEXT + "/bar/" );

        assertEquals( contents, actual );
    }
}
