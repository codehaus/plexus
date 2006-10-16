package org.codehaus.plexus.jetty;

import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.util.MultiException;

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
        throws InitializationException
    {
        server = new HttpServer();

        SocketListener listener = new SocketListener();

        listener.setPort( port );

        server.addListener( listener );

        if ( contexts != null )
        {
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
    }

    public void start()
        throws StartingException
    {
        try
        {
            server.start();
        }
        catch ( MultiException e )
        {
            throw new StartingException( "Error starting the jetty httpd server: ", e );
        }
    }

    public void stop()
        throws StoppingException
    {
        try
        {
            server.stop();
        }
        catch ( InterruptedException e )
        {
            throw new StoppingException( "Error stopping the jetty httpd server: ", e );
        }
    }
}