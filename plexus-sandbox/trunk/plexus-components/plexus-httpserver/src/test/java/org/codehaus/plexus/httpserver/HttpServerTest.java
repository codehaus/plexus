package org.codehaus.plexus.httpserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpServerTest extends PlexusTestCase
{
    public void testConfiguration()
        throws Exception
    {
        DefaultHttpServer server = (DefaultHttpServer)lookup( HttpServer.class.getName() );

        assertEquals( 9999, server.getPort() );
    }

    public void txestManual() throws Exception
    {
        Thread.sleep(30000); //Give the server time to be manually accessed
    }

    public void testIndex() throws Exception
    {
        // this lookup is needed untill the container starts all services before the tests is run
        lookup( HttpServer.class.getName() );

        Thread.sleep(100); //Give the server time to spin up
        URL u = new URL("http://127.0.0.1:9999/");
        BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
        assertEquals("HELLO THIS IS HTML", br.readLine());
        br.close();
    }

    public void test404a() throws Exception
    {
        // this lookup is needed untill the container starts all services before the tests is run
        lookup( HttpServer.class.getName() );

        Thread.sleep(100); //Give the server time to spin up

        URL u = new URL("http://127.0.0.1:9999/404a");
        URLConnection conn = u.openConnection();
        conn.connect();
        //BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
        //assertEquals("HELLO THIS IS HTML", br.readLine());
        //br.close();
    }

    public void txest404b() throws Exception
    {
        Thread.sleep(100); //Give the server time to spin up
        URL u = new URL("http://127.0.0.1:9999/404b");
        BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
        assertEquals("HELLO THIS IS HTML", br.readLine());
        br.close();
    }

    public void txest800() throws Exception
    {
        Thread.sleep(100); //Give the server time to spin up
        URL u = new URL("http://127.0.0.1:9999/800");
        BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
        assertEquals("HELLO THIS IS HTML", br.readLine());
        br.close();
    }

}
