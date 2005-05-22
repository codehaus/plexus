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

import org.codehaus.plexus.application.service.PlexusService;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.xmlrpc.XmlRpcServer;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class XmlRpcPlexusService
    extends AbstractLogEnabled
    implements PlexusService, Startable
{
    /** @requirement */
    private XmlRpcServer xmlRpcServer;

    /** @configuration */
    private int port;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
        throws StartingException
    {
        try
        {
            xmlRpcServer.addListener( null, port, false );

            xmlRpcServer.startListener( null, port );
        }
        catch ( XmlRpcException e )
        {
            throw new StartingException( "Error while starting XML-RPC server.", e );
        }
    }

    public void stop()
    {
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

    public void beforeApplicationStart( ApplicationRuntimeProfile applicationRuntimeProfile,
                                        PlexusConfiguration serviceConfiguration )
        throws Exception
    {
    }

    public void afterApplicationStart( ApplicationRuntimeProfile applicationRuntimeProfile,
                                       PlexusConfiguration serviceConfiguration )
        throws Exception
    {
        PlexusConfiguration[] handlers = serviceConfiguration.getChild( "handlers" ).getChildren( "handler" );

        for ( int i = 0; i < handlers.length; i++ )
        {
            PlexusConfiguration handler = handlers[ i ];

            String role = handler.getChild( "role" ).getValue();

            String name = handler.getChild( "name" ).getValue();

            if ( StringUtils.isEmpty( role ) )
            {
                getLogger().error( "Error in configuration: Missing 'role' child element of 'handler' element." );

                break;
            }

            if ( StringUtils.isEmpty( name ) )
            {
                getLogger().error( "Error in configuration: Missing 'name' child element of 'handler' element." );

                break;
            }

            if ( !applicationRuntimeProfile.getContainer().hasComponent( role ) )
            {
                getLogger().error( "No component with the role '" + role + "' available." );

                break;
            }

            Object component = applicationRuntimeProfile.getContainer().lookup( role );

            getLogger().info( "Adding XML-RPC handler for role '" + role + " to name '" + name + "'." );

            xmlRpcServer.addHandler( name, component );
        }
    }
}
