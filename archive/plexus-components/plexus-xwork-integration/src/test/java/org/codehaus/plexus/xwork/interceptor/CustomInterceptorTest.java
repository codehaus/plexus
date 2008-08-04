/**
 *
 */
package org.codehaus.plexus.xwork.interceptor;

/*
 * Copyright 2006-2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.xwork.PlexusLifecycleListener;
import org.codehaus.plexus.xwork.PlexusObjectFactory;
import org.easymock.EasyMock;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.HashMap;

/**
 * Test for {@link PlexusObjectFactory} when it attempts to lookup Custom Xwork interceptors.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class CustomInterceptorTest
    extends PlexusTestCase
{

    /**
     * Tests a plain Interceptor lookup that <em>does not</em> use the {@link PlexusObjectFactory}
     *
     * @throws Exception on errors
     */
    public void testLookup()
        throws Exception
    {
        MockCustomInterceptor component =
            (MockCustomInterceptor) lookup( Interceptor.class.getName(), "testCustomInterceptor" );
        assertNotNull( component );
    }

    /**
     * Tests an Interceptor lookup using the {@link PlexusObjectFactory}.
     *
     * @throws Exception on errors
     */
    public void testLookupWithPlexusObjectFactory()
        throws Exception
    {
        PlexusObjectFactory objFactory = new PlexusObjectFactory();

        InterceptorConfig config =
            new InterceptorConfig( "testCustomInterceptor", MockCustomInterceptor.class, new HashMap() );

        ServletContext servletContext = (ServletContext) EasyMock.createNiceMock( ServletContext.class );

        ServletContextEvent servletContextEvent = new ServletContextEvent( servletContext );

        EasyMock.expect( servletContext.getAttribute( "webwork.plexus.container" ) ).andReturn( container ).anyTimes();

        EasyMock.replay( new Object[]{servletContext} );

        PlexusLifecycleListener listener = new PlexusLifecycleListener();

        listener.contextInitialized( servletContextEvent );

        objFactory.init( servletContext );

        //noinspection CatchGenericClass,OverlyBroadCatchBlock
        try
        {
            objFactory.buildInterceptor( config, new HashMap() );

            fail( "Expected CompositionException" );
        }
        catch ( Exception e )
        {
            assertEquals( CompositionException.class.getName(), e.getCause().getClass().getName() );
        }
    }
}
