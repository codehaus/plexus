/**
 * 
 */
package org.codehaus.plexus.xwork.interceptor;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.xwork.PlexusLifecycleListener;
import org.codehaus.plexus.xwork.PlexusObjectFactory;
import org.easymock.EasyMock;

import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * Test for {@link PlexusObjectFactory} when it attempts to lookup Custom Xwork interceptors.
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class CustomInterceptorTest
    extends PlexusTestCase
{

    /**
     * Tests a plain Interceptor lookup that <em>does not</em> use the {@link PlexusObjectFactory}
     */
    public void testLookup()
    {
        try
        {
            MockCustomInterceptor component = (MockCustomInterceptor) lookup( Interceptor.class.getName(),
                                                                              "testCustomInterceptor" );
            assertNotNull( component );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( "Unexpected Exception" );
        }

    }

    /**
     * Tests an Interceptor lookup using the {@link PlexusObjectFactory}.
     * @throws Exception
     */
    public void testLookupWithPlexusObjectFactory()
        throws Exception
    {
        PlexusObjectFactory objFactory = new PlexusObjectFactory();

        InterceptorConfig config = new InterceptorConfig( "testCustomInterceptor", MockCustomInterceptor.class,
                                                          new HashMap() );

        ServletContext servletContext = (ServletContext) EasyMock.createNiceMock( ServletContext.class );

        ServletContextEvent servletContextEvent = new ServletContextEvent( servletContext );

        EasyMock.expect( servletContext.getAttribute( "webwork.plexus.container" ) ).andReturn( container ).anyTimes();

        EasyMock.replay( new Object[] { servletContext } );

        PlexusLifecycleListener listener = new PlexusLifecycleListener();

        listener.contextInitialized( servletContextEvent );

        objFactory.init( servletContext );

        try
        {
            Interceptor interceptor = objFactory.buildInterceptor( config, new HashMap() );

            fail( "Expected CompositionException" );
            //assertNotNull( interceptor );
        }
        catch ( Exception e )
        {
            assertEquals( CompositionException.class.getName(), e.getCause().getClass().getName() );
        }

    }
}
