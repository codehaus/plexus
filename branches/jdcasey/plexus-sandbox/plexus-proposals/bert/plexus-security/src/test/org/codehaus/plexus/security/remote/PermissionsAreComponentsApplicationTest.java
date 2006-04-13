package org.codehaus.plexus.security.remote;

import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.authentication.pap.PAPToken;

/**
  * 
  * <p>Created on 23/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class PermissionsAreComponentsApplicationTest extends PlexusTestCase
{

    /**
     * @param arg0
     */
    public PermissionsAreComponentsApplicationTest(String arg0)
    {
        super(arg0);
    }

    public void test() throws Exception
    {
        //start the session
        Application app = (Application) lookup(Application.ROLE);

        //obtain a session
        ApplicationSession sessABC = app.authenticate(newToken("tom", "tomspassword"));
        //ABC should all be accessible
        ServiceA serviceA = (ServiceA) sessABC.lookup(ServiceA.ROLE);
		serviceA.doSomething();
        ServiceB serviceB = (ServiceB) sessABC.lookup(ServiceB.ROLE);
		serviceB.doSomething();
        ServiceC serviceC = (ServiceC) sessABC.lookup(ServiceC.ROLE);
		serviceC.doSomething();
		
        sessABC.release(serviceA);
        sessABC.release(serviceB);
        sessABC.release(serviceC);

        ApplicationSession sessAC = app.authenticate(newToken("dick", "dickspassword"));
        
        //AC should be accessible
        serviceA = (ServiceA) sessAC.lookup(ServiceA.ROLE);
        serviceA.doSomething();
        
        serviceC = (ServiceC) sessAC.lookup(ServiceC.ROLE);
        serviceC.doSomething();
		serviceA.doSomething();
		
        //B should fail
        boolean failed = false;
        try
        {
            serviceB = (ServiceB) sessAC.lookup(ServiceB.ROLE);
        }
        catch (ServiceException e)
        {
            failed = true;
        }
        assertTrue("Expected lookup to of failed", failed);
        sessAC.release(serviceA);
        sessAC.release(serviceC);

        sessABC.invalidate();
        sessAC.invalidate();

    }

    private PAPToken newToken(String userName, String password)
    {
        return new PAPToken(userName, password);
    }

}
