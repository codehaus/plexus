/**
 *
 */
package org.codehaus.plexus.discovery.dnssd;

import junit.textui.TestRunner;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.discovery.DiscoverableResource;
import org.codehaus.plexus.discovery.ResourceDiscoverer;

import java.util.Iterator;

/**
 * @author Aldrin Leal
 *         <p/>
 *         TODO: Add Support for DNS-LLQ (i.e., dnsextd)
 *         TODO: Implement / Test Publishing
 */
public class DNSSDDiscovererTest
    extends PlexusTestCase
{
    /**
     * Resource Discoverer being Used
     */
    private ResourceDiscoverer resourceDiscoverer;

    /**
     * {@inheritDoc}
     *
     * @see org.codehaus.plexus.PlexusTestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        String name = this.getName().substring( "test".length() );

        this.resourceDiscoverer = (DNSSDResourceDiscoverer) this.container
            .lookup( ResourceDiscoverer.ROLE, name );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.codehaus.plexus.PlexusTestCase#tearDown()
     */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    /**
     * Tests DNS-SD Resource Discovery
     *
     * @throws Exception Someone set us up the bomb
     */
    public void testDiscovery()
        throws Exception
    {
        Thread.sleep( 30000 );

        for ( Iterator iter = this.resourceDiscoverer.findResources(); iter
            .hasNext(); )
        {
            DiscoverableResource curResource = (DiscoverableResource) iter
                .next();

            System.out.println( "curResource=" + curResource );
        }
    }

    /**
     * Entry-Point
     *
     * @param args ignored arguments
     */
    public static void main( String[] args )
    {
        TestRunner.run( DNSSDDiscovererTest.class );
    }
}
