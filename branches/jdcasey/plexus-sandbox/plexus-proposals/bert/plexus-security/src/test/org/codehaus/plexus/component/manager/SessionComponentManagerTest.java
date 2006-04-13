package org.codehaus.plexus.component.manager;

import java.util.Iterator;

import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.SessionManager;
import org.codehaus.plexus.security.authentication.pap.PAPToken;
import org.codehaus.plexus.util.AbstractTestThread;
import org.codehaus.plexus.util.TestThreadManager;

/**
  * 
  * <p>Created on 22/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class SessionComponentManagerTest extends PlexusTestCase
{
    /**
     * @param arg0
     */
    public SessionComponentManagerTest(String arg0)
    {
        super(arg0);
    }

    public void test() throws Exception
    {
        //start the session
        SessionManager security = (SessionManager) lookup(SessionManager.ROLE);
        
        //begin the session as we have to be in session scope
        PlexusSession sess = security.authenticate(newToken("tom", "tomspassword"));
        security.beginRequest(sess.getId());
        DefaultServiceA serviceA1 = (DefaultServiceA) lookup(ServiceA.ROLE);
        
		// Make sure the component is alive.
		assertNotNull(serviceA1);

		// Make sure the component went through all the lifecycle phases
		assertEquals( true, serviceA1.enableLogging );
		assertEquals( true, serviceA1.contextualize );
		assertEquals( true, serviceA1.service );
		assertEquals( true, serviceA1.configure );
		assertEquals( true, serviceA1.initialize );
		assertEquals( true, serviceA1.start );
		assertEquals( false, serviceA1.disposed );
		
		//ensure we get the same component		
        ServiceA serviceA2 = (ServiceA) lookup(ServiceA.ROLE);
        assertEquals(serviceA1, serviceA2);

		//cleanup
        security.endRequest();
        release(serviceA1);
        release(serviceA2);
        
        //end the session and ensure components are destroyed
        sess.invalidate();
		assertEquals( true, serviceA1.disposed );
        
        
        release(security);
        
        //now try outside of the session scope. This _should_ fail
        
        boolean failed= false;
        try
        {
			serviceA2 = (ServiceA) lookup(ServiceA.ROLE);
        }
        catch(ServiceException e)
        {
        	failed = true;
        }
        assertTrue("Expected session component lookup to fail outside of session scope", failed);
    }

    public void testSessionComponentTheSameInSessionScope() throws Exception
    {
        
        SessionManager security = (SessionManager) lookup(SessionManager.ROLE);

		//start the first session so we can obtain the component bound to the first session
        PlexusSession sess1 = security.authenticate(newToken("tom", "tomspassword"));
        security.beginRequest(sess1.getId());
        DefaultServiceA serviceA1 = (DefaultServiceA) lookup(ServiceA.ROLE);
        security.endRequest();

		//now the second session
        PlexusSession sess2 = security.authenticate(newToken("dick", "dickspassword"));
        security.beginRequest(sess2.getId());
        DefaultServiceA serviceA2 = (DefaultServiceA) lookup(ServiceA.ROLE);
        security.endRequest();
        
        //session ids should not be null
		assertNotNull("Session id should not be null", sess1.getId());
		assertNotNull("Session id should not be null", sess2.getId());
        
        //session ids should be different
        assertNotSame("Session ids should be different", sess1.getId(), sess2.getId() );

		//the components from the seperate sessions scopes should be different
		assertFalse("Expected session components to be different instances if requested from within different session scopes", serviceA1 == serviceA2 );
		
        //setup the test threads. Test if sessons are bound to seperate threads correctly and 
        //session components are the same instance within the same session even across
        //multiple threads
        TestThreadManager reg = new TestThreadManager(this);
        for (int i = 0; i < 8; i++)
        {
            SessionComponentTestThread st = null;
            //mix threads up
            if (i / 2 == 1)
            {
                st = new SessionComponentTestThread(reg, sess1, ServiceA.ROLE, serviceA1, security);
            }
            else
            {
                st = new SessionComponentTestThread(reg, sess2, ServiceA.ROLE, serviceA2, security);
            }
            reg.registerThread(st);
        }
        reg.runTestThreads();

        //now wait for the threads to finish..
        synchronized (this)
        {
            try
            {
                if (reg.isStillRunningThreads())
                {
                    wait();
                }
            }
            catch (InterruptedException e)
            {
            }
        }

        assertEquals("Expected 8 test threads to of run", reg.getRunThreads().size(), 8);
        //now test if any components were returned which were incorrect
        if (reg.hasFailedThreads())
        {
            //collect all failed tests
            StringBuffer out = new StringBuffer();
            String nl = System.getProperty("line.separator");

            for (Iterator iter = reg.getFailedTests().iterator(); iter.hasNext();)
            {
                out.append(nl);
                out.append(((SessionComponentTestThread) iter.next()).getErrorMsg());
            }
            fail(
                "Session based component is not being instantiated correctly. Failed test threads: "
                    + out);
        }
        release(serviceA1);
        release(serviceA2);
        
        //check invalidation ends a components lifecycle
		assertFalse("Expected serviceA1 component to _not_ be disposed", serviceA1.disposed );
        sess1.invalidate();
        assertTrue("Expected serviceA1 component to be disposed", serviceA1.disposed );
		assertFalse("Expected serviceA2 component to _not_ be disposed", serviceA2.disposed );
		
		sess2.invalidate();
		assertTrue("Expected serviceA2 component to be disposed", serviceA1.disposed );
		
		//reauthenticating should provide a new session id
		
    }

    private PAPToken newToken(String userName, String password)
    {
        return new PAPToken(userName, password);
    }

    class SessionComponentTestThread extends AbstractTestThread
    {
        private Object expectedComponent;
        private Object returnedComponent;
        private PlexusSession session;
        private String role;
        private SessionManager sessionManager;
        /**
         *
         */
        public SessionComponentTestThread(
            TestThreadManager registry,
            PlexusSession session,
            String role,
            Object expectedComponent,
            SessionManager sessionManager)
        {
            super(registry);
            this.expectedComponent = expectedComponent;
            this.session = session;
            this.role = role;
            this.sessionManager = sessionManager;
        }

        /* (non-Javadoc)
         * @see org.codehaus.plexus.util.AbstractRegisteredThread#doRun()
         */
        public void doRun() throws Throwable
        {
            try
            {
                sessionManager.beginRequest(session.getId());
                returnedComponent = lookup(role);
                if (returnedComponent == null)
                {
                    setErrorMsg("Null component returned");
                }
                else if (returnedComponent == expectedComponent)
                {
                    setPassed(true);
                }
                else
                {
                    setErrorMsg(
                        "Returned component was a different component. Expected="
                            + expectedComponent
                            + ", got="
                            + returnedComponent);
                }
            }
            finally
            {
                release(returnedComponent);
                sessionManager.endRequest();
            }
        }
    }
}
