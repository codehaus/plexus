package org.codehaus.plexus.security.request.proxy;

import junit.framework.TestCase;

import org.codehaus.plexus.security.mock.MockServiceManager;
import org.codehaus.plexus.security.remote.DefaultServiceA;
import org.codehaus.plexus.security.remote.DefaultServiceB;
import org.codehaus.plexus.security.remote.DefaultServiceC;
import org.codehaus.plexus.security.remote.ServiceA;
import org.codehaus.plexus.security.remote.ServiceB;
import org.codehaus.plexus.security.remote.ServiceC;

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

		mockService.mockAddComponents(ServiceA.ROLE, new DefaultServiceA());
		mockService.mockAddComponents(ServiceB.ROLE, new DefaultServiceB());
		mockService.mockAddComponents(ServiceC.ROLE, new DefaultServiceC());
    	
    	
    	
    }

}
