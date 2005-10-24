package org.codehaus.plexus.xmlrpc;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.secure.SecureWebServer;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

/**
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 * @todo Handle XmlRpc.setDebug(boolean)
 */
public class DefaultXmlRpcComponent
    extends AbstractLogEnabled
    implements Contextualizable, Initializable, Startable, XmlRpcComponent
{
    /** */
    private PlexusContainer container;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    /** The port to listen on. */
    private int port;

    /** Secure server state. */
    private boolean isSecureServer;

    /** SAX Parser class. */
    private String saxParserClass;

    /** Paranoid state. */
    private boolean paranoid;

    /** Handlers. */
    private List handlers;

    /** Accepted Clients. */
    private List acceptedClients;

    /** Denied Clients. */
    private List deniedClients;

    /**
     * A set of properties to configure SSL and JSSE
     */
    private Properties systemProperties;

    /** Message Listeners. */
    private List listeners = new ArrayList();

    private boolean isInitialized = false;

    // ----------------------------------------------------------------------
    // Privates
    // ----------------------------------------------------------------------

    /**  The standalone xmlrpc server. */
    private XmlRpcServer xmlRpcServer;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws InitializationException
    {
        // Set system properties. These are required if you're using SSL.
        setSystemPropertiesFromConfiguration();

        if ( port == 0 )
        {
            port = 8080;
        }

        getLogger().info( "Initializing the XML-RPC server on port " + port + "." );

        try
        {
            xmlRpcServer.addListener( null, port, isSecureServer );
        }
        catch ( XmlRpcException e )
        {
            throw new InitializationException( "Cannot add listener on port " + port + " [isSecureServer=" + isSecureServer + "]", e );
        }

        if ( !StringUtils.isEmpty( saxParserClass ) )
        {
            try
            {
                XmlRpc.setDriver( saxParserClass );
            }
            catch ( ClassNotFoundException e )
            {
                throw new InitializationException( "Cannot find the specified SAX parser class: " + saxParserClass );
            }
        }

        try
        {
            registerStartupHandlers();
        }
        catch ( Exception e )
        {
            throw new InitializationException( "Cannot register startup handlers: ", e );
        }

        if ( paranoid )
        {
            getLogger().info( "Operating in a state of paranoia" );

            // Only set the accept/deny client lists if we
            // are in a state of paranoia as they will just
            // be ignored so there's no point in setting them.

            if ( acceptedClients == null )
            {
                throw new InitializationException( "When in state of paranoia a list of 'acceptedClients' is required." );
            }

            for ( int i = 0; i < acceptedClients.size(); i++ )
            {
                String clientIP = getClient( acceptedClients, i );

                xmlRpcServer.acceptClient( clientIP );

                getLogger().info( "Accepting client -> " + clientIP );
            }

            if ( deniedClients == null )
            {
                throw new InitializationException( "When in state of paranoia a list of 'deniedClients' is required." );
            }

            for ( int i = 0; i < deniedClients.size(); i++ )
            {
                String clientIP = getClient( acceptedClients, i );

                xmlRpcServer.denyClient( clientIP );

                getLogger().info( "Denying client -> " + clientIP );
            }
        }

        isInitialized = true;
    }

    public void start()
        throws StartingException
    {
        try
        {
            xmlRpcServer.startListener( null, port );
        }
        catch ( XmlRpcException e )
        {
            throw new StartingException( "Cannot start xmlrpc server: ", e );
        }
    }

    public void stop()
        throws StoppingException
    {
        // Stop the XML RPC server.  org.apache.xmlrpc.WebServer blocks in a call to
        // ServerSocket.accept() until a socket connection is made.
        try
        {
            xmlRpcServer.removeListener( null, port );
        }
        catch ( XmlRpcException e )
        {
            throw new StoppingException( "Cannot stop xmlrpc server: ", e );
        }
    }

    // ----------------------------------------------------------------------
    // XmlRpcComponent Implementation
    // ----------------------------------------------------------------------

    /**
     * Registers any handlers that were defined as part this component's
     * configuration.  A handler may be defined as a class (which will be
     * instantiated) or as a role in which case it will be looked up.
     *
     * @throws Exception If there were errors registering a handler.
     */
    private void registerStartupHandlers()
        throws Exception
    {
        if ( handlers == null )
        {
            getLogger().warn( "No handlers to configure." );

            return;
        }

        getLogger().info( "There are " + handlers.size() + " handlers to configure." );

        for ( int i = 0; i < handlers.size(); i++ )
        {
            Object obj = handlers.get( i );

            if ( !(obj instanceof Handler) )
            {
                getLogger().warn( "Unknown object type in the handler array: " + obj.getClass().getName() );

                continue;
            }

            Handler handler = (Handler) obj;

            String name = handler.getName();

            String role = handler.getRole();

            if ( name == null )
            {
                throw new PlexusConfigurationException( "Missing required configuration element: 'name' in 'handler'." );
            }

            if ( name.trim().length() == 0 )
            {
                throw new PlexusConfigurationException( "The 'name' element of a 'handler' cant be empty." );
            }

            if ( role == null )
            {
                throw new PlexusConfigurationException( "Missing required configuration element: 'role' in 'handler'." );
            }

            if ( role.trim().length() == 0 )
            {
                throw new PlexusConfigurationException( "The 'role element of a 'handler' cant be empty." );
            }

            registerComponentHandler( name, role );
        }
    }

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
    public void registerHandler( String handlerName, Object handler )
        throws XmlRpcException, IOException
    {
        xmlRpcServer.addHandler( handlerName, handler );
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
        registerHandler( handlerName, container.lookup( handlerRole ) );

        getLogger().info( "registered: " + handlerName + " with component: " + handlerRole );
    }

    /**
     * Unregister a handler.
     *
     * @param handlerName The name of the handler to unregister.
     */
    public void unregisterHandler( String handlerName )
        throws XmlRpcException
    {
        xmlRpcServer.removeHandler( handlerName );
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
    public Object executeRpc( URL url, String methodName, Vector params )
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
        if ( isInitialized )
        {
            xmlRpcServer.setParanoid( null, port, state );
        }
        else
        {
            paranoid = state;
        }
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
        xmlRpcServer.acceptClient( address );
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
        xmlRpcServer.denyClient( address );
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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Create System properties using the key-value pairs in a given
     * Configuration.  This is used to set system properties and the
     * URL https connection handler needed by JSSE to enable SSL
     * between XMLRPC client and server.
     *
     */
    private void setSystemPropertiesFromConfiguration()
    {
        if ( systemProperties == null )
        {
            return;
        }

        for ( Enumeration e = systemProperties.propertyNames(); e.hasMoreElements(); )
        {
            String key = e.nextElement().toString();

            String value = systemProperties.getProperty( key );

            System.setProperty( key, value );

            getLogger().debug( "Setting property: " + key + " => '" + value + "'." );
        }
    }

    private String getClient( List list, int index )
        throws InitializationException
    {
        Object obj = list.get( index );

        if ( !(obj instanceof Client) )
        {
            throw new InitializationException( "The client address element must be a '" + Client.class.getName() + "'." );
        }

        Client client = (Client) obj;

        return client.getHostname();
    }
}
