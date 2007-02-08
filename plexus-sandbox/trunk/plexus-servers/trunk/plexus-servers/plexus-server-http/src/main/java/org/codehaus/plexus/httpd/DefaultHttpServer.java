/*
 *  Copyright (C) MX4J.
 *  All rights reserved.
 *
 *  This software is distributed under the terms of the MX4J License version 1.0.
 *  See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.httpd;

import org.apache.velocity.context.Context;
import org.codehaus.plexus.server.ConnectionHandlingException;
import org.codehaus.plexus.server.DefaultServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * HttpAdaptor sets the basic adaptor listening for HTTP requests
 *
 * @author    <a href="mailto:tibu@users.sourceforge.net">Carlos Quiroz</a>
 * @plexus.component
 */
public class DefaultHttpServer
    extends DefaultServer
    implements HttpServer
{
    private static final String VERSION = "1.1.1";

    /** Map of commands indexed by the request path */
    private Map commands = new HashMap();

    private String authenticationMethod = "none";

    // Should be dependant on the server?
    private String realm = "MX4J";

    private Map authorizations = new HashMap();

    private AdaptorServerSocketFactory socketFactory = null;

    private String processorClass;

    private Date startDate;

    private long requestsCount;

    private Processor defaultProcessor;

    private String[][] defaultCommandProcessors = {
        {"server", "org.apache.plexus.manager.adaptor.processor.ServerCommandProcessor"},
        {"mbean", "org.apache.plexus.manager.adaptor.processor.MBeanCommandProcessor"},
    };

    /**
     * Sets the Authentication Method.
     *
     * @param method none/basic/digest
     */
    public void setAuthenticationMethod( String method )
    {
        if ( alive )
        {
            throw new IllegalArgumentException( "Not possible to change authentication method with the server running" );
        }
        if ( method == null
            ||
            !( method.equals( "none" )
            || method.equals( "basic" )
            || method.equals( "digest" ) ) )
        {
            throw new IllegalArgumentException( "Only accept methods none/basic/digest" );
        }
        this.authenticationMethod = method;
    }


    /**
     * Authentication Method
     *
     * @return authentication method
     */
    public String getAuthenticationMethod()
    {
        return authenticationMethod;
    }

    /**
     * Indicates whether the server's running
     *
     * @return        The active value
     */
    public boolean isActive()
    {
        return alive;
    }


    /**
     * Starting date
     *
     * @return        The date when the server was started
     */
    public Date getStartDate()
    {
        return startDate;
    }


    /**
     * Requests count
     *
     * @return        The total of requests served so far
     */
    public long getRequestsCount()
    {
        return requestsCount;
    }


    /**
     * Gets the HttpAdaptor version
     *
     * @return        HttpAdaptor's version
     */
    public String getVersion()
    {
        return VERSION;
    }


    /**
     * Adds a command processor object
     */
    public void addCommandProcessor( String path, HttpCommandProcessor processor )
    {
        commands.put( path, processor );

        if ( alive )
        {
        }
    }


    /**
     * Adds a command processor object by class
     */
    public void addCommandProcessor( String path, String processorClass )
    {
        try
        {
            HttpCommandProcessor processor = (HttpCommandProcessor) Class.forName( processorClass ).newInstance();
            addCommandProcessor( path, processor );
        }
        catch ( Exception e )
        {
            getLogger().error( "Exception creating Command Processor of class " + processorClass, e );
        }
    }


    /**
     * Removes a command processor object by class
     *
     */
    public void removeCommandProcessor( String path )
    {
        if ( commands.containsKey( path ) )
        {
            commands.remove( path );
        }
    }

    /**
     * Adds an authorization pair as username/password
     */
    public void addAuthorization( String username, String password )
    {
        if ( username == null || password == null )
        {
            throw new IllegalArgumentException( "username and passwords cannot be null" );
        }
        authorizations.put( username, password );
    }


    public void postRegister( Boolean registrationDone )
    {
    }


    public void preDeregister()
        throws java.lang.Exception
    {
        // stop the server
        stop();
    }


    public void postDeregister()
    {
    }

    private boolean isUsernameValid( String username, String password )
    {
        if ( authorizations.containsKey( username ) )
        {
            return password.equals( authorizations.get( username ) );
        }
        return false;
    }


    protected HttpCommandProcessor getProcessor( String path )
    {
        return (HttpCommandProcessor) commands.get( path );
    }


    /**
     * Build the commands
     */
    protected void buildCommands()
    {
        for ( int i = 0; i < defaultCommandProcessors.length; i++ )
        {
            try
            {
                HttpCommandProcessor processor =
                    (HttpCommandProcessor) Class.forName( defaultCommandProcessors[i][1] ).newInstance();
                commands.put( defaultCommandProcessors[i][0], processor );
            }
            catch ( Exception e )
            {
                getLogger().warn( "Exception building command procesor", e );
            }
        }
    }


    protected void postProcess( HttpOutputStream out,
                                HttpInputStream in,
                                Context context,
                                String template )
        throws IOException, Exception
    {
        defaultProcessor.writeResponse( out, in, context, template );
    }


    protected void findUnknownElement( String path, HttpOutputStream out, HttpInputStream in )
        throws IOException
    {
        defaultProcessor.notFoundElement( path, out, in );
    }


    protected String preProcess( String path )
        throws IOException
    {
        return defaultProcessor.preProcess( path );
    }

    protected void postProcess( HttpOutputStream out, HttpInputStream in, Exception e )
        throws IOException
    {
        defaultProcessor.writeError( out, in, e );
    }

    public void handleConnection( Socket socket )
        throws ConnectionHandlingException
    {
        new HttpClient( socket ).run();
    }

    private class HttpClient
    {
        private Socket client;


        HttpClient( Socket client )
        {
            this.client = client;
        }

        public boolean isValid( String authorizationString )
        {
            if ( authenticationMethod.startsWith( "basic" ) )
            {
                authorizationString = authorizationString.substring( 5, authorizationString.length() );
                String decodeString = Base64.decodeToString( authorizationString );
                if ( decodeString.indexOf( ":" ) > 0 )
                {
                    try
                    {
                        StringTokenizer tokens = new StringTokenizer( decodeString, ":" );
                        String username = tokens.nextToken();
                        String password = tokens.nextToken();
                        return isUsernameValid( username, password );
                    }
                    catch ( Exception e )
                    {
                        return false;
                    }
                }
            }
            return false;
        }


        private boolean handleAuthentication( HttpInputStream in,
                                              HttpOutputStream out )
            throws IOException
        {
            if ( authenticationMethod.equals( "basic" ) )
            {
                String result = in.getHeader( "authorization" );
                if ( result != null )
                {
                    if ( isValid( result ) )
                    {
                        return true;
                    }
                    throw new HttpException( HttpConstants.STATUS_FORBIDDEN, "Authentication failed" );
                }

                out.setCode( HttpConstants.STATUS_AUTHENTICATE );
                out.setHeader( "WWW-Authenticate", "Basic realm=\"" + realm + "\"" );
                out.sendHeaders();
                out.flush();
                return false;
            }
            if ( authenticationMethod.equals( "digest" ) )
            {
                // not implemented
            }
            return true;
        }


        public void run()
        {
            HttpInputStream httpIn = null;
            HttpOutputStream httpOut = null;
            try
            {
                // get input streams
                InputStream in = client.getInputStream();
                httpIn = new HttpInputStream( in );
                httpIn.readRequest();

                // Find a suitable command processor
                String path = httpIn.getPath();
                String queryString = httpIn.getQueryString();
                getLogger().info( "Request " + path + ( ( queryString == null ) ? "" : ( "?" + queryString ) ) );
                String postPath = preProcess( path );

                if ( !postPath.equals( path ) )
                {
                    getLogger().info( "Processor replaced path " + path + " with the path " + postPath );
                    path = postPath;
                }

                OutputStream out = client.getOutputStream();
                httpOut = new HttpOutputStream( out, httpIn );

                if ( !handleAuthentication( httpIn, httpOut ) )
                {
                    return;
                }

                String command = path.substring( 1, path.length() );
                HttpCommandProcessor processor = getProcessor( command );

                if ( processor == null )
                {
                    getLogger().info( "No suitable command processor found, requesting from processor path " + path );
                    findUnknownElement( path, httpOut, httpIn );
                }
                else
                {
                    Context context = processor.executeRequest( httpIn );
                    postProcess( httpOut, httpIn, context, command );
                }
            }
            catch ( Exception ex )
            {
                getLogger().warn( "Exception during http request", ex );
                if ( httpOut != null )
                {
                    try
                    {
                        postProcess( httpOut, httpIn, ex );
                    }
                    catch ( IOException e )
                    {
                        getLogger().warn( "IOException during http request", e );
                    }
                    catch ( RuntimeException rte )
                    {
                        getLogger().error( "RuntimeException during http request", rte );
                    }
                    catch ( Error er )
                    {
                        getLogger().error( "Error during http request ", er );
                    }
                    catch ( Throwable t )
                    {
                        getLogger().fatalError( "Throwable during http request ", t );
                    }
                }
            }
            catch ( Error ex )
            {
                getLogger().error( "Error during http request ", ex );
            }
            finally
            {
                try
                {
                    if ( httpOut != null )
                    {
                        httpOut.flush();
                    }
                    // always close the socket
                    client.close();
                }
                catch ( IOException e )
                {
                    getLogger().warn( "Exception during request processing", e );
                }
            }
        }
    }

}

