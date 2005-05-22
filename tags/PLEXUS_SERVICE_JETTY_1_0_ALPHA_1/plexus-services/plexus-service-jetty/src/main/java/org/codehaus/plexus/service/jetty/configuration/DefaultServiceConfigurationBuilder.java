package org.codehaus.plexus.service.jetty.configuration;

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

            String extractionPath = webapps[ i ].getChild( "extractionPath" ).getValue( null );

            if ( StringUtils.isEmpty( extractionPath ) )
            {
                getLogger().warn( "Error while deploying web application: " +
                                  "For each 'extractionPath' element has to be specified." );

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

            String virtualHost = webapps[ i ].getChild( "virtualHost" ).getValue( null );

            if ( StringUtils.isEmpty( virtualHost ) )
            {
                virtualHost = null;
            }

            // ----------------------------------------------------------------------
            // Port
            // ----------------------------------------------------------------------

            String portString = webapps[ i ].getChild( "port" ).getValue( null );

            if ( StringUtils.isEmpty( portString ) )
            {
                getLogger().warn( "Error while deploying web application: 'port' is missing or empty." );

                continue;
            }

            int port = 0;

            try
            {
                port = Integer.parseInt( portString );
            }
            catch ( NumberFormatException e )
            {
                getLogger().warn( "Error while deploying web application: 'port' is not a number." );

                continue;
            }

            WebApplication app = new WebApplication( file, path, extractionPath, context, virtualHost, port );

            configuration.addWebApplication( app );
        }

        return configuration;
    }
}
