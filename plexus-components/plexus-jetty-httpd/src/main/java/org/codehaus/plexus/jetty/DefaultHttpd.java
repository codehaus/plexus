package org.codehaus.plexus.jetty;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;

import java.util.Iterator;
import java.util.List;

public class DefaultHttpd
    implements Httpd, Initializable, Startable
{
    private HttpServer server;

    private int port;

    private List contexts;

    private String[] m =
        {
            HttpRequest.__GET,
            HttpRequest.__POST,
            HttpRequest.__HEAD,
            HttpRequest.__OPTIONS,
            HttpRequest.__TRACE,
            HttpRequest.__PUT
        };

    public void initialize()
        throws Exception
    {
        server = new HttpServer();

        SocketListener listener = new SocketListener();

        listener.setPort( port );

        server.addListener( listener );

        for ( Iterator iterator = contexts.iterator(); iterator.hasNext(); )
        {
            Context context = (Context) iterator.next();

            HttpContext httpContext = new HttpContext();

            httpContext.setContextPath( context.getPath() );

            httpContext.setResourceBase( context.getDocumentRoot() );

            ResourceHandler rh = new ResourceHandler();            

            rh.setAllowedMethods( m );

            httpContext.addHandler( rh );

            server.addContext( httpContext );
        }
    }

    public void start()
        throws Exception
    {
        // Start the http server
        server.start();
    }

    public void stop()
        throws Exception
    {
        server.stop();
    }
}