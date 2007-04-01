/**
 * Copyright 2006 Aldrin Leal, aldrin at leal dot eng dot bee ar
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.codehaus.plexus.discovery.mdns;

import java.net.URL;
import java.util.Iterator;

import junit.textui.TestRunner;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.discovery.DiscoverableResource;
import org.codehaus.plexus.discovery.ResourceDiscoverer;
import org.codehaus.plexus.discovery.ResourcePublisher;

/**
 * Test for MDNSResourceDiscoverer
 *
 * @author Aldrin Leal
 */
public class MDNSResourceDiscovererTest
    extends PlexusTestCase
{
    /** ResourcePublisher being used */
    private MDNSResourcePublisher resourcePublisher;

    /** ResourceDiscoverer being used */
    private MDNSResourceDiscoverer resourceDiscoverer;

    /** Resource Stub */
    private DiscoverableResource discoverableResource;

    /**
     * Entry-Point
     *
     * @param args
     */
    public static void main( String[] args )
    {
        TestRunner.run( MDNSResourceDiscovererTest.class );
    }

    /**
     * Configures the Test
     *
     * @see org.codehaus.plexus.PlexusTestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        String name = this.getName().substring( "test".length() );

        this.resourcePublisher = (MDNSResourcePublisher) lookup( ResourcePublisher.ROLE );

        this.resourceDiscoverer = (MDNSResourceDiscoverer) lookup( ResourceDiscoverer.ROLE, name );
    }

    /** Tests a simple nearby discovery */
    public void testSimpleHttpDiscovery()
        throws Exception
    {
        this.discoverableResource = new DiscoverableResource();

        this.discoverableResource.setId( "test-server" );
        this.discoverableResource.setName( "Test Server" );
        this.discoverableResource
            .setUrl( new URL( "http://localhost:8095/path?a=b&c+d=e&f=Hello%2C%20World%21&g&h=" ) );
        this.discoverableResource.setType( "_http.tcp.local." );

        this.resourcePublisher.registerResource( this.discoverableResource );

        Thread.sleep( 15000 );

        for ( Iterator iter = this.resourceDiscoverer.findResources(); iter
            .hasNext(); )
        {
            DiscoverableResource curResource = (DiscoverableResource) iter
                .next();

            System.out.println( "curResource=" + curResource );
        }
    }

    /**
     * Shutdown
     *
     * @see org.codehaus.plexus.PlexusTestCase#tearDown()
     */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }
}
