package org.codehaus.plexus.jetty.configuration.builder;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.jetty.configuration.HttpListener;
import org.codehaus.plexus.jetty.configuration.ProxyHttpListener;
import org.codehaus.plexus.jetty.configuration.ServiceConfiguration;
import org.codehaus.plexus.jetty.configuration.WebContext;
import org.codehaus.plexus.jetty.configuration.Webapp;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.FileReader;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultServiceConfigurationBuilderTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        ServiceConfigurationBuilder configurationBuilder =
            (ServiceConfigurationBuilder) lookup( ServiceConfigurationBuilder.ROLE );

        Xpp3Dom dom = Xpp3DomBuilder.build(
            new FileReader( getTestFile( "src/test/resources/full-configuration-example.xml" ) ) );

        PlexusConfiguration plexusConfiguration = new XmlPlexusConfiguration( dom );

        ServiceConfiguration configuration =
            configurationBuilder.buildConfiguration( plexusConfiguration, getContainer().getContainerRealm() );

        assertNotNull( configuration );

        assertNotNull( configuration.getWebapps() );

        assertEquals( 1, configuration.getWebapps().size() );

        // ----------------------------------------------------------------------
        // Assert the Webapp
        // ----------------------------------------------------------------------

        Webapp webapp = (Webapp) configuration.getWebapps().get( 0 );

        assertEquals( "/continuum", webapp.getContext() );

        assertEquals( "${plexus.home}/webapp", webapp.getExtractionPath() );

        assertEquals( "${plexus.home}/lib/continuum-web-1.0-alpha-3-SNAPSHOT.jar", webapp.getFile() );

        assertNull( webapp.getPath() );

        assertEquals( "www.foo.com", webapp.getVirtualHost() );

        assertTrue( webapp.isStandardWebappClassloader() );

        // ----------------------------------------------------------------------
        // Assert the listeners
        // ----------------------------------------------------------------------

        List listeners = webapp.getListeners();

        assertNotNull( listeners );

        assertEquals( 2, listeners.size() );

        assertEquals( HttpListener.class, listeners.get( 0 ).getClass() );

        HttpListener httpListener = (HttpListener) listeners.get( 0 );

        assertNull( httpListener.getHost() );

        assertEquals( 123, httpListener.getPort() );

        assertEquals( ProxyHttpListener.class, listeners.get( 1 ).getClass() );

        ProxyHttpListener proxyHttpListener = (ProxyHttpListener) listeners.get( 1 );

        assertNull( proxyHttpListener.getHost() );

        assertEquals( 8090, proxyHttpListener.getPort() );

        assertEquals( "localhost", proxyHttpListener.getProxyHost() );

        assertEquals( 80, proxyHttpListener.getProxyPort() );

        // ----------------------------------------------------------------------------
        // Test the web contexts
        // ----------------------------------------------------------------------------

        assertNotNull( configuration.getWebContexts() );

        assertEquals( 1, configuration.getWebContexts().size() );

        // ----------------------------------------------------------------------
        // Assert the Webapp
        // ----------------------------------------------------------------------

        WebContext webContext = (WebContext) configuration.getWebContexts().get( 0 );

        assertEquals( "/repository", webContext.getContext() );

        assertEquals( "/path/to/repository", webContext.getPath() );

        assertEquals( "www.foo.com", webContext.getVirtualHost() );

        // ----------------------------------------------------------------------
        // Assert the listeners
        // ----------------------------------------------------------------------

        listeners = webContext.getListeners();

        assertNotNull( listeners );

        assertEquals( 2, listeners.size() );

        assertEquals( HttpListener.class, listeners.get( 0 ).getClass() );

        httpListener = (HttpListener) listeners.get( 0 );

        assertNull( httpListener.getHost() );

        assertEquals( 123, httpListener.getPort() );

        assertEquals( ProxyHttpListener.class, listeners.get( 1 ).getClass() );

        proxyHttpListener = (ProxyHttpListener) listeners.get( 1 );

        assertNull( proxyHttpListener.getHost() );

        assertEquals( 8090, proxyHttpListener.getPort() );

        assertEquals( "localhost", proxyHttpListener.getProxyHost() );

        assertEquals( 80, proxyHttpListener.getProxyPort() );
    }
}
