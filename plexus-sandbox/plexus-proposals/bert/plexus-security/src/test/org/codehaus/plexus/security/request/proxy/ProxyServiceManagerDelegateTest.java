package org.codehaus.plexus.security.request.proxy;

import junit.framework.TestCase;

import org.codehaus.plexus.security.dummy.DummyServiceA;
import org.codehaus.plexus.security.dummy.DummyServiceB;
import org.codehaus.plexus.security.dummy.DummyServiceC;
import org.codehaus.plexus.security.dummy.ServiceA;
import org.codehaus.plexus.security.dummy.ServiceB;
import org.codehaus.plexus.security.dummy.ServiceC;
import org.codehaus.plexus.security.mock.MockServiceManager;

/**
  * Test the ProxyServiceManager. Test to ensure proxies are generated
  * correctly and that mwthod interception actaully occurs.
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class ProxyServiceManagerDelegateTest extends TestCase
{

    /**
     * 
     */
    public ProxyServiceManagerDelegateTest()
    {
        super();
    }

    /**
     * @param arg0
     */
    public ProxyServiceManagerDelegateTest(String arg0)
    {
        super(arg0);
    }
    
    public void test() throws Exception
    {
		MockServiceManager mockService = new MockServiceManager();

		mockService.mockAddComponents(ServiceA.ROLE, new DummyServiceA());
		mockService.mockAddComponents(ServiceB.ROLE, new DummyServiceB());
		mockService.mockAddComponents(ServiceC.ROLE, new DummyServiceC());
    	
    	
    	
    }

}
