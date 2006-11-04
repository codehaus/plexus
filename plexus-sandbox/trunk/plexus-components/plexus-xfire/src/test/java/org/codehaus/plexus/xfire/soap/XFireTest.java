package org.codehaus.plexus.xfire.soap;

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
        SoapServiceCreator sc = (SoapServiceCreator) lookup( SoapServiceCreator.ROLE );
    }
}
