package org.codehaus.plexus.service.xmlrpc.configuration.builder;

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
import org.codehaus.plexus.service.xmlrpc.configuration.ServiceConfiguration;
import org.codehaus.plexus.service.xmlrpc.configuration.XmlRpcService;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel VEnisse</a>
 * @version $Id$
 */
public class DefaultServiceConfigurationBuilder
    extends AbstractLogEnabled
    implements ServiceConfigurationBuilder
{
    public ServiceConfiguration buildConfiguration( PlexusConfiguration serviceConfiguration )
    {
        ServiceConfiguration configuration = new ServiceConfiguration();

        PlexusConfiguration xmlrpc = serviceConfiguration.getChild( "xmlrpc" );

        int port = getPort( xmlrpc );

        if ( port != -1 )
        {
            XmlRpcService xmlrpcService = new XmlRpcService( port );

            configuration.addXmlRpcService( xmlrpcService );
        }

        return configuration;
    }

    private int getPort( PlexusConfiguration xmlrpc )
    {
        String portString = xmlrpc.getChild( "port" ).getValue( null );

        if ( StringUtils.isEmpty( portString ) )
        {
            getLogger().warn( "Error while deploying xml-rpc service: 'port' has to be a integer." );

            return -1;
        }

        try
        {
            return Integer.parseInt( portString );
        }
        catch ( NumberFormatException e )
        {
            getLogger().warn( "Error while deploying xml-rpc service: 'port' has to be a integer." );

            return -1;
        }
    }
}
