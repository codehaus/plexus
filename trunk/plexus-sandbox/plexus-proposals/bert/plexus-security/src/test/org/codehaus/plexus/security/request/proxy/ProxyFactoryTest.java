package org.codehaus.plexus.security.request.proxy;

import java.util.Vector;

import junit.framework.TestCase;

import org.codehaus.plexus.security.DefaultPlexusSession;
import org.codehaus.plexus.security.mock.MockPlexusSession;
import org.codehaus.plexus.security.request.RequestInterceptor;
import org.codehaus.plexus.security.session.InvalidSessionException;
import org.codehaus.plexus.security.simple.SimpleAgent;

/**
  * 
  * <p>Created on 22/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class ProxyFactoryTest extends TestCase
{

    /**
     * 
     */
    public ProxyFactoryTest()
    {
        super();
    }

    /**
     * @param arg0
     */
    public ProxyFactoryTest(String arg0)
    {
        super(arg0);
    }

	public void test() throws Exception
	{
		
		//setup supporting objects..
		TestRequestInterceptor interceptor = new TestRequestInterceptor();
		SimpleAgent agent = new SimpleAgent("tom","tom", null );
		MockPlexusSession sess= new MockPlexusSession("sess1", agent);				
		//the real component		
		Foo foo = new FooImpl();
		//the proxy component
		Foo fooProxy = (Foo)ProxyFactory.createProxyComponent(Foo.class, foo, interceptor, sess);
				
		//now test request interceptor works
		int result = fooProxy.addIt(1, 2);
		assertEquals("Expected proxy to of returned result",3, result);
		assertEquals("Expected one request to have been intercepted", 1,interceptor.mockGetBegins().size());
		//check the session id was included
		String id = (String)interceptor.mockGetBegins().firstElement();
		assertEquals("sess1",id);
		assertEquals("Expected request to of terminated", 1, interceptor.mockGetEnds().size() );

	}
	

}

class TestRequestInterceptor implements RequestInterceptor
{
	private Vector begins = new Vector();
	private Vector ends = new Vector();
	
    /**
     * 
     */
    public TestRequestInterceptor()
    {
        super();
    }

    /**
     * @see org.codehaus.plexus.security.request.RequestInterceptor#beginRequest(java.lang.String)
     */
    public void beginRequest(String sessionId) throws InvalidSessionException
    {
        begins.add(sessionId);
    }

    /**
     * @see org.codehaus.plexus.security.request.RequestInterceptor#endRequest()
     */
    public void endRequest()
    {
        ends.add(Thread.currentThread());
    }
    
    public Vector mockGetBegins()
    {
    	return begins;
    }
    
	public Vector mockGetEnds()
		{
			return ends;
		}
    
    public void clear()
    {
    	begins.clear();
    	ends.clear();
    }

}
