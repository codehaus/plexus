package org.codehaus.plexus.security.mock;

import junit.framework.TestCase;

import org.codehaus.plexus.security.remote.DefaultServiceA;
import org.codehaus.plexus.security.remote.DefaultServiceB;
import org.codehaus.plexus.security.remote.DefaultServiceC;
import org.codehaus.plexus.security.remote.ServiceA;
import org.codehaus.plexus.security.remote.ServiceB;
import org.codehaus.plexus.security.remote.ServiceC;

/**
  * Test the MockServiceManager. Seeing this is used quite a bit in other tests might as well
  * do some basic tests to ensure it actually works.
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class MockServiceManagerTest extends TestCase
{

    /**
     * 
     */
    public MockServiceManagerTest()
    {
        super();
    }

    /**
     * @param arg0
     */
    public MockServiceManagerTest(String arg0)
    {
        super(arg0);
    }
    
	public void testConnectionCounts() throws Exception
	{
		MockServiceManager mockService = new MockServiceManager();

		mockService.mockAddComponents(ServiceA.ROLE, new DefaultServiceA());
		mockService.mockAddComponents(ServiceB.ROLE, new DefaultServiceB());
		mockService.mockAddComponents(ServiceC.ROLE, new DefaultServiceC());
				
		//test lookups to ensure correct object returned and connection counts are
		//handled correctly
		
		ServiceA serviceA = (ServiceA)mockService.lookup(ServiceA.ROLE);		
		assertNotNull("ServiceA should not be null", serviceA);
		ServiceB serviceB = (ServiceB)mockService.lookup(ServiceB.ROLE);		
		assertNotNull("ServiceB should not be null", serviceB);
		ServiceC serviceC = (ServiceC)mockService.lookup(ServiceC.ROLE);		
		assertNotNull("ServiceC should not be null", serviceC);
		
		assertEquals("Incorrect connection count returned for ServiceA",1, mockService.mockGetConnectionCounts(ServiceA.ROLE));
		assertEquals("Incorrect connection count returned for ServiceB",1, mockService.mockGetConnectionCounts(ServiceB.ROLE));
		assertEquals("Incorrect connection count returned for ServiceC",1, mockService.mockGetConnectionCounts(ServiceC.ROLE));
		
		ServiceB serviceB2 = (ServiceB)mockService.lookup(ServiceB.ROLE);
		assertEquals("Incorrect connection count returned for ServiceB",2, mockService.mockGetConnectionCounts(ServiceB.ROLE));
		mockService.release(serviceB);
		assertEquals("Incorrect connection count returned for ServiceB",1, mockService.mockGetConnectionCounts(ServiceB.ROLE));
		mockService.release(serviceB2);
		assertEquals("Incorrect connection count returned for ServiceB",0, mockService.mockGetConnectionCounts(ServiceB.ROLE));
		
		//ensure other counts haven't been affected
		assertEquals("Incorrect connection count returned for ServiceA",1, mockService.mockGetConnectionCounts(ServiceA.ROLE));
		assertEquals("Incorrect connection count returned for ServiceC",1, mockService.mockGetConnectionCounts(ServiceC.ROLE));
	}

}
