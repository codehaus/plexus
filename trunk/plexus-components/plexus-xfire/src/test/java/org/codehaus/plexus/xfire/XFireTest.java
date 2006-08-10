package org.codehaus.plexus.xfire;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author Jason van Zyl
 */
public class XFireTest
    extends PlexusTestCase
{
    public void testConstruction()
        throws Exception
    {
        ServiceCreator sc = (ServiceCreator) lookup( ServiceCreator.ROLE );
    }
}
