package org.codehaus.plexus.service.jetty.configuration.builder;

/*
 * The MIT License
 *
 * Copyright (c) 2004-2005, The Codehaus
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

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.service.jetty.configuration.ServiceConfiguration;
import org.codehaus.plexus.service.jetty.configuration.WebApplication;
import org.codehaus.plexus.service.jetty.configuration.HttpListener;
import org.codehaus.plexus.service.jetty.configuration.ProxyHttpListener;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultServiceConfigurationBuilder
    extends AbstractLogEnabled
    implements ServiceConfigurationBuilder
{
    public ServiceConfiguration buildConfiguration( PlexusConfiguration serviceConfiguration )
    {
        ServiceConfiguration configuration = new ServiceConfiguration();

        PlexusConfiguration[] webapps = serviceConfiguration.getChild( "webapps" ).getChildren( "webapp" );

        for ( int i = 0; i < webapps.length; i++ )
        {
            // ----------------------------------------------------------------------
            // Path
            // ----------------------------------------------------------------------

            String path = webapps[ i ].getChild( "path" ).getValue( null );

            if ( StringUtils.isEmpty( path ) )
            {
                path = null;
            }

            // ----------------------------------------------------------------------
            // File
            // ----------------------------------------------------------------------

            String file = webapps[ i ].getChild( "file" ).getValue( null );

            if ( StringUtils.isEmpty( file ) )
            {
                file = null;
            }

            if ( path == null && file == null )
            {
                getLogger().warn( "Error while deploying web application: " +
                                  "For each 'webapp' a 'path' or 'file' element has to be specified." );

                continue;
            }

            // ----------------------------------------------------------------------
            // Extraction Path
            // ----------------------------------------------------------------------

            String extractionPath = webapps[ i ].getChild( "extraction-path" ).getValue( null );

            if ( StringUtils.isEmpty( extractionPath ) )
            {
                getLogger().warn( "Error while deploying web application: " +
                                  "For each 'extraction-path' element has to be specified." );

                continue;
            }

            // ----------------------------------------------------------------------
            // Context
            // ----------------------------------------------------------------------

            String context = webapps[ i ].getChild( "context" ).getValue( null );

            if ( StringUtils.isEmpty( context ) )
            {
                getLogger().warn( "Error while deploying web application: 'context' is missing or empty." );

                continue;
            }

            // ----------------------------------------------------------------------
            // Virtual host
            // ----------------------------------------------------------------------

            String virtualHost = webapps[ i ].getChild( "virtual-host" ).getValue( null );

            if ( StringUtils.isEmpty( virtualHost ) )
            {
                virtualHost = null;
            }

            PlexusConfiguration[] listeners = webapps[ i ].getChild( "listeners" ).getChildren();

            WebApplication app = new WebApplication( file,
                                                     path,
                                                     extractionPath,
                                                     context,
                                                     virtualHost );

            for ( int j = 0; j < listeners.length; j++ )
            {
                PlexusConfiguration listener = listeners[ j ];


                String proxyPortString = listener.getChild( "proxy-port" ).getValue( null );

                HttpListener httpListener;

                if ( listener.getName().equals( "http-listener" ) )
                {
                    int port = getPort( listener );

                    if ( port == -1 )
                    {
                        continue;
                    }

                    httpListener = new HttpListener( getHost( listener ),
                                                     port );
                }
                else if ( listener.getName().equals( "proxy-http-listener" ) )
                {
                    String proxyHost = getProxyHost( listener );

                    if ( StringUtils.isEmpty( proxyHost ) || StringUtils.isEmpty( proxyPortString ) )
                    {
                        getLogger().warn( "Both proxyHost and proxyPort has to be specified." );

                        continue;
                    }

                    int port = getPort( listener );

                    int proxyPort = getProxyPort( listener );

                    if ( port == -1 || proxyPort == -1 )
                    {
                        continue;
                    }

                    httpListener = new ProxyHttpListener( getHost( listener ),
                                                          port,
                                                          proxyHost,
                                                          proxyPort );
                }
                else
                {
                    getLogger().warn( "Unknown listener type '" + listener.getName() + "'." );

                    continue;
                }

                app.getListeners().add( httpListener );
            }

            if ( app.getListeners().size() == 0 )
            {
                getLogger().warn( "At least one listener has to be configured before adding the web application." );

                continue;
            }

            configuration.addWebApplication( app );
        }

        return configuration;
    }

    private String getHost( PlexusConfiguration listener )
    {
        String host = listener.getChild( "host" ).getValue( null );

        if ( StringUtils.isEmpty( host ) )
        {
            return null;
        }

        return host;
    }

    private int getPort( PlexusConfiguration listener )
    {
        String portString = listener.getChild( "port" ).getValue( null );

        if ( StringUtils.isEmpty( portString ) )
        {
            getLogger().warn( "Error while deploying web application: 'port' has to be a integer." );

            return -1;
        }

        try
        {
            return Integer.parseInt( portString );
        }
        catch ( NumberFormatException e )
        {
            getLogger().warn( "Error while deploying web application: 'port' has to be a integer." );

            return -1;
        }
    }
    private String getProxyHost( PlexusConfiguration listener )
    {
        String proxyHost = listener.getChild( "proxy-host" ).getValue( null );

        if ( StringUtils.isEmpty( proxyHost ) )
        {
            proxyHost = null;
        }

        return proxyHost;
    }

    private int getProxyPort( PlexusConfiguration listener )
    {
        String proxyPortString = listener.getChild( "proxy-port" ).getValue( null );

        if ( StringUtils.isEmpty( proxyPortString ) )
        {
            getLogger().warn( "Error while deploying web application: 'proxy-port' has to be a integer." );

            return -1;
        }

        try
        {
            return Integer.parseInt( proxyPortString );
        }
        catch ( NumberFormatException e )
        {
            getLogger().warn( "Error while deploying web application: 'proxy-port' has to be a integer." );

            return -1;
        }
    }
}
