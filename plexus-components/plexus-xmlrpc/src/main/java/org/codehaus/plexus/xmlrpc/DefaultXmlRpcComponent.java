package org.codehaus.plexus.xmlrpc;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.secure.SecureWebServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 * @todo Handle XmlRpc.setDebug(boolean)
 */
public class DefaultXmlRpcComponent
    extends AbstractLogEnabled
    implements Contextualizable, Configurable, Initializable, Startable, Disposable, Serviceable, XmlRpcComponent
{
    /** The service manager for this component. */
    private ServiceManager manager;

    /**  The standalone xmlrpc server. */
    private WebServer webserver;

    /** The port to listen on. */
    private int port;

    /** Secure server state. */
    private boolean isSecureServer;

    /** SAX Parser class. */
    private String saxParserClass;

    /** Paranoid state. */
    private boolean isStateOfParanoia;

    /** Handlers. */
    private Configuration handlerConfiguration;

    /** Accepted Clients. */
    private Configuration[] acceptedClients;

    /** Denied Clients. */
    private Configuration[] deniedClients;

    /** Message Listeners. */
    private List listeners;

    /** ClassLoader */
    private ClassLoader classLoader;

    /** Default Constructor. */
    public DefaultXmlRpcComponent()
    {
        listeners = new ArrayList();
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see Contextualizable#contextualize */
    public void contextualize( Context context )
        throws ContextException
    {
        classLoader = (ClassLoader) context.get( "common.classloader" );
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        // Set system properties. These are required if you're
        // using SSL.
        setSystemPropertiesFromConfiguration( configuration );

        // Set the port for the service
        // Need a default value here and make sure we have a valid
        // value or throw a config exception.
        port = configuration.getChild( "port" ).getValueAsInteger(8080);

        // Determine if the server is secure or not.
        isSecureServer = configuration.getChild( "secureServer" ).getValueAsBoolean(false);

        // Set the XML driver to the correct SAX parser class
        saxParserClass = configuration.getChild( "parser" ).getValue();

        // Turn on paranoia for the webserver if requested.
        isStateOfParanoia = configuration.getChild( "paranoid" ).getValueAsBoolean(false);

        // Check if there are any handlers to register at startup
        handlerConfiguration = configuration.getChild( "handlers" );

        // Set the list of clients that can connect
        // to the xmlrpc server. The accepted client list
        // will only be consulted if we are paranoid.
        acceptedClients = configuration.getChildren( "acceptedClients" );

        // Set the list of clients that can connect
        // to the xmlrpc server. The denied client list
        // will only be consulted if we are paranoid.
        deniedClients = configuration.getChildren( "deniedClients" );
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
        this.manager = manager;
    }

    /**
     * Create System properties using the key-value pairs in a given
     * Configuration.  This is used to set system properties and the
     * URL https connection handler needed by JSSE to enable SSL
     * between XMLRPC client and server.
     *
     * @param configuration the Configuration defining the System
     * properties to be set
     */
    void setSystemPropertiesFromConfiguration( Configuration configuration )
        throws ConfigurationException
    {
        Configuration[] systemProperties = configuration.getChildren( "systemProperty" );

        getLogger().debug( "system properties: " + systemProperties.length );

        for ( int i = 0; i < systemProperties.length; i++ )
        {
            Configuration systemProperty = systemProperties[i];

            String key = systemProperty.getAttribute( "name" );
            String value = systemProperty.getAttribute( "value" );

            getLogger().debug( "System property: " + key + " => " + value );

            System.setProperty( key, value );
        }
    }

    // ------------------------------------------------------------------------
    // S T A R T A B L E
    // ------------------------------------------------------------------------

    public void start()
        throws Exception
    {
    }

    /**
     * This function initializes the XmlRpcService.
     */
    public void initialize()
        throws Exception
    {
        getLogger().info( "Attempting to start the XML-RPC server." );

        // Need a default value here.
        if ( isSecureServer )
        {
            webserver = new SecureWebServer( port );
        }
        else
        {
            webserver = new WebServer( port );
        }

        if ( saxParserClass != null )
        {
            XmlRpc.setDriver( saxParserClass );
        }

        registerStartupHandlers();

        if ( isStateOfParanoia )
        {
            webserver.setParanoid( isStateOfParanoia );

            getLogger().info( "Operating in a state of paranoia" );

            // Only set the accept/deny client lists if we
            // are in a state of paranoia as they will just
            // be ignored so there's no point in setting them.

            for ( int i = 0; i < acceptedClients.length; i++ )
            {
                Configuration acceptedClient = acceptedClients[i];
                String clientIP = acceptedClient.getAttribute( "clientIP" );

                if ( clientIP != null && !clientIP.equals( "" ) )
                {
                    webserver.acceptClient( clientIP );

                    getLogger().info( "Accepting client -> " + clientIP );
                }
            }

            for ( int i = 0; i < deniedClients.length; i++ )
            {
                Configuration deniedClient = deniedClients[i];
                String clientIP = deniedClient.getAttribute( "clientIP" );

                if ( clientIP != null && !clientIP.equals( "" ) )
                {
                    webserver.denyClient( clientIP );

                    getLogger().info( "Accepting client -> " + clientIP );
                }
            }
        }
    }

    /**
     * Registers any handlers that were defined as part this component's
     * configuration.  A handler may be defined as a class (which will be
     * instantiated) or as a role in which case it will be looked up.
     *
     * @throws Exception If there were errors registering a handler.
     */
    private void registerStartupHandlers() throws Exception
    {
        Configuration[] handlers = handlerConfiguration.getChildren( "handler" );

        getLogger().info( "We have " + handlers.length + " to configure." );
        
        for ( int i = 0; i < handlers.length; i++ )
        {
            Configuration c = handlers[i];

            if ( c.getName().equals( "handler" ) )
            {
                String handlerName = c.getChild( "name" ).getValue();
                String handlerClass = c.getChild( "class" ).getValue( null );
                String handlerRole = c.getChild( "role" ).getValue( null );

                if ( handlerClass != null && handlerRole == null )
                {
                    registerClassHandler( handlerName, handlerClass );
                }
                else if ( handlerRole != null && handlerClass == null )
                {
                    registerComponentHandler( handlerName, handlerRole );
                }
                else
                {
                    throw new ConfigurationException(
                        "Handler must define either a 'class' or 'role'" );
                }
            }
        }
    }

    public void stop()
        throws Exception
    {
    }

    // ------------------------------------------------------------------------
    // D I S P O S A B L E
    // ------------------------------------------------------------------------

    /**
     * Shuts down this service, stopping running threads.
     */
    public void dispose()
    {
        // Stop the XML RPC server.  org.apache.xmlrpc.WebServer blocks in a call to
        // ServerSocket.accept() until a socket connection is made.
        webserver.shutdown();
        try
        {
            Socket interrupt = new Socket( InetAddress.getLocalHost(), port );
            interrupt.close();
        }
        catch ( Exception notShutdown )
        {
            // Remotely possible we're leaving an open listener socket around.
            getLogger().warn( "It's possible the xmlrpc server was not shutdown: " +
                              notShutdown.getMessage() );
        }
    }

    // ------------------------------------------------------------------------
    // I M P L E M E N A T I O N
    // ------------------------------------------------------------------------

    /**
     * Register an Object as a default handler for the service.
     *
     * @param handler The handler to use.
     * @exception XmlRpcException
     * @exception IOException
     */
    public void registerHandler( Object handler )
        throws XmlRpcException, IOException
    {
        registerHandler( "$default", handler );
    }

    /**
     * Register an Object as a handler for the service.
     *
     * @param handlerName The name the handler is registered under.
     * @param handler The handler to use.
     * @throws XmlRpcException If an XmlRpcException occurs.
     * @throws IOException If an IOException occurs.
     */
    public void registerHandler( String handlerName,
                                 Object handler )
        throws XmlRpcException, IOException
    {
        webserver.addHandler( handlerName, handler );
    }

    /**
     * A helper method that tries to initialize a handler and register it.
     * The purpose is to check for all the exceptions that may occur in
     * dynamic class loading and throw an Exception on
     * error.
     *
     * @param handlerName The name the handler is registered under.
     * @param handlerClass The name of the class to use as a handler.
     * @exception Exception Couldn't instantiate handler.
     */
    private void registerClassHandler( String handlerName, String handlerClass )
        throws Exception
    {
        try
        {
            Object handler = classLoader.loadClass( handlerClass ).newInstance();
            webserver.addHandler( handlerName, handler );
            getLogger().info( "registered: " + handlerName + " with class: " + handlerClass );

        }
            // those two errors must be passed to the VM
        catch ( ThreadDeath t )
        {
            throw t;
        }
        catch ( OutOfMemoryError t )
        {
            throw t;
        }

        catch ( Throwable t )
        {
            throw new Exception( "Failed to instantiate " + handlerClass, t );
        }
    }

    /**
     * Helper that registers a component as a handler with the specified
     * handler name.
     *
     * @param handlerName The name to register this handle as.
     * @param handlerRole The role of the component serving as the handler.
     * @exception Exception If the component could not be looked up.
     */
    private void registerComponentHandler( String handlerName, String handlerRole )
        throws Exception
    {
        registerHandler( handlerName, manager.lookup( handlerRole ) );
        getLogger().info( "registered: " + handlerName + " with component: " + handlerRole );
    }

    /**
     * Unregister a handler.
     *
     * @param handlerName The name of the handler to unregister.
     */
    public void unregisterHandler( String handlerName )
    {
        webserver.removeHandler( handlerName );
    }

    /**
     * Client's interface to XML-RPC.
     *
     * The return type is Object which you'll need to cast to
     * whatever you are expecting.
     *
     * @param url A URL.
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception XmlRpcException
     * @exception IOException
     */
    public Object executeRpc( URL url,
                              String methodName,
                              Vector params )
        throws Exception
    {
        try
        {
            XmlRpcClient client = new XmlRpcClient( url );
            return client.execute( methodName, params );
        }
        catch ( Exception e )
        {
            throw new Exception( "XML-RPC call failed", e );
        }
    }

    /**
     * Switch client filtering on/off.
     *
     * @param state Whether to filter clients.
     *
     * @see #acceptClient(java.lang.String)
     * @see #denyClient(java.lang.String)
     */
    public void setParanoid( boolean state )
    {
        webserver.setParanoid( state );
    }

    /**
     * Add an IP address to the list of accepted clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must
     * call setParanoid(true) in order for this to have
     * any effect.
     *
     * @param address The address to add to the list.
     *
     * @see #denyClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void acceptClient( String address )
    {
        webserver.acceptClient( address );
    }

    /**
     * Add an IP address to the list of denied clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @param address The address to add to the list.
     *
     * @see #acceptClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void denyClient( String address )
    {
        webserver.denyClient( address );
    }

    /** Add message listener. */
    public void addMessageListener( XmlRpcMessageListener listener )
    {
        listeners.add( listener );
    }

    /** Message received. */
    public void messageReceived( String message )
    {
        for ( Iterator i = listeners.iterator(); i.hasNext(); )
        {
            XmlRpcMessageListener listener = (XmlRpcMessageListener) i.next();
            listener.xmlRpcMessageReceived( message );
        }
    }
}
