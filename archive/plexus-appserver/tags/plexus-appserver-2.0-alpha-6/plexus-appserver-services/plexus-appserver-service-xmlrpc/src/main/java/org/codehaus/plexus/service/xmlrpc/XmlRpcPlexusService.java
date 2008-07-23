package org.codehaus.plexus.service.xmlrpc;

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

import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.plexus.appserver.service.PlexusServiceException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.service.xmlrpc.configuration.ServiceConfiguration;
import org.codehaus.plexus.service.xmlrpc.configuration.XmlRpcService;
import org.codehaus.plexus.service.xmlrpc.configuration.builder.ServiceConfigurationBuilder;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xmlrpc.XmlRpcServer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel VEnisse</a>
 * @version $Id$
 */
public class XmlRpcPlexusService
    extends AbstractLogEnabled
    implements PlexusService, Startable
{
    /**
     * @plexus.requirement
     */
    private ServiceConfigurationBuilder configurationBuilder;

    /**
     * @requirement
     */
    private XmlRpcServer xmlRpcServer;

    /**
     * port activity flag
     */
    private int port = -1;

    /**
     * Set of ports to be activated. The port can only be used once.
     */
    private Set activePorts = new HashSet();

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
        throws StartingException
    {
        getLogger().info( "Starting XML-RPC service." );
    }

    public void stop()
    {
        getLogger().info( "Stopping XML-RPC service." );

        if ( port == -1 )
        {
            return;
        }

        try
        {
            xmlRpcServer.removeListener( null, port );
        }
        catch ( XmlRpcException e )
        {
            getLogger().error( "Error while stopping the XML-RPC server.", e );
        }
    }

    // ----------------------------------------------------------------------
    // PlexusService Implementation
    // ----------------------------------------------------------------------

    public void beforeApplicationStart( AppRuntimeProfile appRuntimeProfile, PlexusConfiguration serviceConfiguration )
        throws PlexusServiceException
    {
        ServiceConfiguration configuration = configurationBuilder.buildConfiguration( serviceConfiguration );

        for ( Iterator it = configuration.getXmlRpcServices().iterator(); it.hasNext(); )
        {
            XmlRpcService service = (XmlRpcService) it.next();

            try
            {
                String activePort = Integer.toString( service.getPort() );

                if ( !activePorts.contains( activePort ) )
                {
                    activePorts.add( activePort );

                    xmlRpcServer.addListener( null, service.getPort(), false );

                    xmlRpcServer.startListener( null, service.getPort() );

                    port = service.getPort();
                }
            }
            catch ( XmlRpcException e )
            {
                throw new PlexusServiceException(
                    "Error while starting XML-RPC server on port " + service.getPort() + ".", e );
            }
        }
    }

    public void afterApplicationStart( AppRuntimeProfile appRuntimeProfile, PlexusConfiguration serviceConfiguration )
        throws PlexusServiceException
    {
        if ( port == -1 )
        {
            return;
        }

        PlexusConfiguration[] handlers = serviceConfiguration.getChild( "handlers" ).getChildren( "handler" );

        for ( int i = 0; i < handlers.length; i++ )
        {
            PlexusConfiguration handler = handlers[i];

            try
            {
                String role = handler.getChild( "role" ).getValue();

                String name = handler.getChild( "name" ).getValue();

                if ( StringUtils.isEmpty( role ) )
                {
                    getLogger().error( "Error in configurator: Missing 'role' child element of 'handler' element." );

                    break;
                }

                if ( StringUtils.isEmpty( name ) )
                {
                    getLogger().error( "Error in configurator: Missing 'name' child element of 'handler' element." );

                    break;
                }

                if ( !appRuntimeProfile.getApplicationServerContainer().hasComponent( role ) )
                {
                    getLogger().error( "No component with the role '" + role + "' available." );

                    break;
                }

                Object component = appRuntimeProfile.getApplicationServerContainer().lookup( role );

                getLogger().info( "Adding XML-RPC handler for role '" + role + " to name '" + name + "'." );

                xmlRpcServer.addHandler( null, name, port, component );
            }
            catch ( PlexusConfigurationException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
            catch ( ComponentLookupException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
            catch ( XmlRpcException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
        }
    }

    public void applicationStop( AppRuntimeProfile runtimeProfile )
    {
    }

}
