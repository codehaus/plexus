package org.codehaus.plexus.xfire.soap;

import org.codehaus.xfire.service.Service;

/**
 * @author Jason van Zyl
 */
public class EchoTest
    extends AbstractSoapTest
{
    private SoapServiceCreator soapServiceCreator;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        soapServiceCreator = (SoapServiceCreator) lookup( SoapServiceCreator.ROLE );
    }

    public void testEchoService()
        throws Exception
    {
        Service echo = soapServiceCreator.createService( Echo.ROLE );

        assertNotNull( echo );
    }
}
