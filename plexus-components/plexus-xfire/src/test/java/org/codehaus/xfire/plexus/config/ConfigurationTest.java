package org.codehaus.xfire.plexus.config;

import org.codehaus.xfire.plexus.PlexusXFireTest;
import org.codehaus.xfire.service.Service;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class ConfigurationTest
    extends PlexusXFireTest
{
    public void setUp()
        throws Exception
    {
        System.setProperty("xfire.config", "/org/codehaus/xfire/plexus/config/services.xml");
        super.setUp();

        lookup(ConfigurationService.ROLE);
    }

    public void testRegister()
        throws Exception
    {
        Service service = getServiceRegistry().getService("Echo");
        assertNotNull(service);
        assertNotNull(service.getInHandlers());
        assertEquals(3, service.getInHandlers().size());
        assertNotNull(service.getOutHandlers());
        assertEquals(2, service.getOutHandlers().size());

        service = getServiceRegistry().getService("EchoXMLBeans");
        assertNotNull(service);

        assertTrue(getXFire().getInPhases().size() > 0);

        // service = (ObjectService)
        // getServiceRegistry().getService("EchoWSDL");
        // assertNotNull( service );
        // assertEquals(1, service.getOperations().size());
    }

    public void testInvoke()
        throws Exception
    {
        Document response = invokeService("Echo", "/org/codehaus/xfire/plexus/config/echo11.xml");

        addNamespace("e", "urn:Echo");
        assertValid("//e:out[text()='Yo Yo']", response);

        response = invokeService("EchoIntf", "/org/codehaus/xfire/plexus/config/echo11.xml");

        addNamespace("e", "urn:Echo");
        assertValid("//e:out[text()='Yo Yo']", response);
    }
}
