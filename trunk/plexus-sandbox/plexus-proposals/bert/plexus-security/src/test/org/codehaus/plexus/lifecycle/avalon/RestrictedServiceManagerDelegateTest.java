package org.codehaus.plexus.lifecycle.avalon;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;
import org.codehaus.plexus.security.dummy.DummyServiceA;
import org.codehaus.plexus.security.dummy.DummyServiceB;
import org.codehaus.plexus.security.dummy.DummyServiceC;
import org.codehaus.plexus.security.dummy.ServiceA;
import org.codehaus.plexus.security.dummy.ServiceB;
import org.codehaus.plexus.security.dummy.ServiceC;
import org.codehaus.plexus.security.mock.MockServiceManager;

/**
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RestrictedServiceManagerDelegateTest extends TestCase
{

    /**
     * 
     */
    public RestrictedServiceManagerDelegateTest()
    {
        super();
    }

    /**
     * @param arg0
     */
    public RestrictedServiceManagerDelegateTest(String arg0)
    {
        super(arg0);
    }

    public void test() throws Exception
    {
        MockServiceManager mockService = new MockServiceManager();

        mockService.mockAddComponents(ServiceA.ROLE, new DummyServiceA());
        mockService.mockAddComponents(ServiceB.ROLE, new DummyServiceB());
        mockService.mockAddComponents(ServiceC.ROLE, new DummyServiceC());

        RestrictedServiceManagerDelegate resService = new RestrictedServiceManagerDelegate();
        
        //build the configuration
        String s =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                 "<configuration><allow-components><role>"+
                ServiceA.class.getName()+
                "</role><role>"+
                ServiceC.class.getName()+
                "</role></allow-components></configuration>";
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();
        Configuration config = builder.parse(new StringReader(s));

        resService.service(mockService);
        resService.configure(config);

		//now test for component access. Lookups for ServiceA and ServiceC should pass,
		//ServiceB should fail
		
		//pass
		ServiceA serviceA = (ServiceA)resService.lookup(ServiceA.ROLE);		
		assertNotNull("ServiceA should not be null", serviceA);
		ServiceC serviceC = (ServiceC)resService.lookup(ServiceC.ROLE);		
		assertNotNull("ServiceC should not be null", serviceC);
		
		//fail
		boolean failed = false;
		try
		{
			resService.lookup(ServiceB.ROLE);
		}
		catch(ServiceException e)
		{
			failed = true;
		}
		assertTrue("Expected lookup for ServiceB to of failed", failed);
		
		
    }

}
