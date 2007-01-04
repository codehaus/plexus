package org.codehaus.plexus.webdav;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusWebDavServletTest
    extends PlexusTestCase
{
    private static final int PORT = 4321;

    public void testBasic()
        throws Exception
    {
        File fsPath = getTestFile( "target/dav-storage" );

        FileUtils.deleteDirectory( fsPath );
        FileUtils.forceMkdir( fsPath );

        FileUtils.fileWrite( new File( fsPath, "bar" ).getAbsolutePath(), "yo!");

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        System.setProperty( "DEBUG", "" );
        System.setProperty( "org.mortbay.log.class", "org.slf4j.impl.SimpleLogger" );
        Server server = new Server( PORT );
        Context root = new Context( server, "/", Context.SESSIONS );
        root.setAttribute( PlexusConstants.PLEXUS_KEY, getContainer() );
        ServletHandler servletHandler = new ServletHandler();
        root.setHandler( servletHandler );
        ServletHolder holder = servletHandler.addServletWithMapping( PlexusWebDavServlet.class, "/projects/*" );
        holder.setInitParameter( "dav.root", fsPath.getAbsolutePath() );
        server.start();

        URL url = new URL( "http://localhost:" + PORT + "/projects/bar" );

        assertEquals( "yo!", IOUtil.toString( url.openStream() ) );

        server.stop();
    }
}
