package org.codehaus.plexus.summit;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.summit.parameters.RequestParameterParser;

/**
 * SummitView unit tests.
 * 
 * @author Pete Kazmier
 */
public class SummitViewTest
    extends PlexusTestCase
{
    /**
     * Verify that the SummitView can in fact act as a ServiceBroker
     * and lookup() components.
     *
     * @throws Exception If an error occurs.
     */
    public void testServiceBroker() throws Exception
    {
        // Now we lookup() a component with the 'view'
        RequestParameterParser parser = 
            ( RequestParameterParser ) lookup( RequestParameterParser.ROLE );

        assertTrue( parser instanceof RequestParameterParser );
    }
}
