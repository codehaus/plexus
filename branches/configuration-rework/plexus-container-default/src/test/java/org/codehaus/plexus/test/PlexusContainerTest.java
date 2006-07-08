package org.codehaus.plexus.test;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import junit.framework.TestCase;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.discovery.DiscoveredComponent;
import org.codehaus.plexus.test.list.Pipeline;
import org.codehaus.plexus.test.list.Valve;
import org.codehaus.plexus.test.map.Activity;
import org.codehaus.plexus.test.map.ActivityManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class PlexusContainerTest
    extends TestCase
{
    private DefaultPlexusContainer container;

    public void setUp()
        throws Exception
    {
        String basedir = System.getProperty( "basedir" );

        InputStream configurationStream = PlexusContainerTest.class.getResourceAsStream( "PlexusContainerTest.xml" );

        assertNotNull( configurationStream );

        assertNotNull( getClass().getClassLoader() );

        container = new DefaultPlexusContainer();

        container.addContextValue( "basedir", basedir );

        container.addContextValue( "plexus.home", basedir + "/target/plexus-home" );

        container.setConfigurationResource( new InputStreamReader( configurationStream ) );

        container.initialize();

        container.start();
    }

    public void tearDown()
        throws Exception
    {
        container.dispose();

        container = null;
    }

    public void testDefaultPlexusContainerSetup()
        throws Exception
    {
        assertEquals( "bar", System.getProperty( "foo" ) );
    }

    // ----------------------------------------------------------------------
    // Test the native plexus lifecycle. Note that the configuration for
    // this TestCase supplies its own lifecycle, so this test verifies that
    // the native lifecycle is available after configuration merging.
    // ----------------------------------------------------------------------

    public void testNativeLifecyclePassage()
        throws Exception
    {
        DefaultServiceB serviceB = (DefaultServiceB) container.lookup( ServiceB.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceB );

        // Make sure the component went through all the lifecycle phases
        assertEquals( true, serviceB.enableLogging );

        assertEquals( true, serviceB.contextualize );

        assertEquals( true, serviceB.initialize );

        assertEquals( true, serviceB.start );

        assertEquals( false, serviceB.stop );

        container.release( serviceB );

        assertEquals( true, serviceB.stop );
    }

    public void testConfigurableLifecyclePassage()
        throws Exception
    {
        DefaultServiceE serviceE = (DefaultServiceE) container.lookup( ServiceE.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceE );

        // Make sure the component went through all the lifecycle phases
        assertEquals( true, serviceE.enableLogging );

        assertEquals( true, serviceE.contextualize );

        assertEquals( true, serviceE.initialize );

        assertEquals( true, serviceE.start );

        assertEquals( false, serviceE.stop );

        assertEquals( true, serviceE.serviced );

        assertEquals( true, serviceE.configured );

        container.release( serviceE );

        assertEquals( true, serviceE.stop );
    }

    /*
     * Check that we can get references to a single component with a role
     * hint.
     */
    public void testSingleComponentLookupWithRoleHint()
        throws Exception
    {
        // Retrieve an instance of component c.
        DefaultServiceC serviceC1 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "first-instance" );

        // Make sure the component is alive.
        assertNotNull( serviceC1 );

        assertTrue( serviceC1.started );

        assertFalse( serviceC1.stopped );

        // Retrieve a second reference to the same component.
        DefaultServiceC serviceC2 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "first-instance" );

        // Make sure component is alive.
        assertNotNull( serviceC2 );

        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        // Let's make sure it gave us back the same component.
        assertSame( serviceC1, serviceC2 );

        container.release( serviceC1 );

        // The component should still be alive.
        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        container.release( serviceC2 );

        // The component should now have been stopped.
        assertTrue( serviceC2.started );

        assertTrue( serviceC2.stopped );
    }

    /*
     * Check that distinct components with the same implementation are
     * managed correctly.
     */
    public void testMultipleSingletonComponentInstances()
        throws Exception
    {
        // Retrieve an instance of component c.
        DefaultServiceC serviceC1 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "first-instance" );

        // Make sure the component is alive.
        assertNotNull( serviceC1 );

        assertTrue( serviceC1.started );

        assertFalse( serviceC1.stopped );

        // Retrieve an instance of component c, with a different role hint.
        // This should give us a different component instance.
        DefaultServiceC serviceC2 = (DefaultServiceC) container.lookup( ServiceC.ROLE, "second-instance" );

        // Make sure component is alive.
        assertNotNull( serviceC2 );

        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        // The components should be distinct.
        assertNotSame( serviceC1, serviceC2 );

        container.release( serviceC1 );

        // The first component should now have been stopped, the second
        // one should still be alive.
        assertTrue( serviceC1.started );

        assertTrue( serviceC1.stopped );

        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        container.release( serviceC2 );

        // The second component should now have been stopped.
        assertTrue( serviceC2.started );

        assertTrue( serviceC2.stopped );
    }

    // ----------------------------------------------------------------------
    // Test using an arbitrary component lifecycle handler
    // ----------------------------------------------------------------------

    public void testArbitraryLifecyclePassageUsingFourArbitraryPhases()
        throws Exception
    {
        // Retrieve an manager of component H.
        DefaultServiceH serviceH = (DefaultServiceH) container.lookup( ServiceH.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceH );

        // Make sure the component went through all the lifecycle phases
        assertEquals( true, serviceH.eeny );

        assertEquals( true, serviceH.meeny );

        assertEquals( true, serviceH.miny );

        assertEquals( true, serviceH.mo );

        container.release( serviceH );
    }

    public void testLookupAll()
        throws Exception
    {
        Map components = container.lookupMap( ServiceC.ROLE );

        assertNotNull( components );

        assertEquals( 2, components.size() );

        ServiceC component = (ServiceC) components.get( "first-instance" );

        assertNotNull( component );

        component = (ServiceC) components.get( "second-instance" );

        assertNotNull( component );

        container.releaseAll( components );
    }

//    class SingletonComponentTestThread
//        extends AbstractTestThread
//    {
//        private Object expectedComponent;
//
//        private Object returnedComponent;
//
//        private PlexusContainer container;
//
//        private String role;
//
//        public SingletonComponentTestThread( PlexusContainer container, String role, Object expectedComponent )
//        {
//            super();
//
//            this.expectedComponent = expectedComponent;
//
//            this.container = container;
//
//            this.role = role;
//        }
//
//        /**
//         * @param registry
//         */
//        public SingletonComponentTestThread( TestThreadManager registry, PlexusContainer container, String role,
//                                             Object expectedComponent )
//        {
//            super( registry );
//
//            this.expectedComponent = expectedComponent;
//
//            this.container = container;
//
//            this.role = role;
//        }
//
//        /* (non-Javadoc)
//         * @see org.codehaus.plexus.util.AbstractRegisteredThread#doRun()
//         */
//        public void doRun()
//            throws Throwable
//        {
//            try
//            {
//                returnedComponent = container.lookup( role );
//
//                if ( returnedComponent == null )
//                {
//                    setErrorMsg( "Null component returned" );
//                }
//                else if ( returnedComponent == expectedComponent )
//                {
//                    setPassed( true );
//                }
//                else
//                {
//                    setErrorMsg(
//                        "Returned component was a different manager. Expected=" + expectedComponent + ", got=" +
//                        returnedComponent );
//                }
//            }
//            finally
//            {
//                container.release( returnedComponent );
//            }
//        }
//    }


    public void testAutomatedComponentConfigurationUsingXStreamPoweredComponentConfigurator()
        throws Exception
    {
        Component component = (Component) container.lookup( Component.ROLE );

        assertNotNull( component );

        assertNotNull( component.getActivity() );

        assertEquals( "localhost", component.getHost() );

        assertEquals( 10000, component.getPort() );
    }

    public void testAutomatedComponentComposition()
        throws Exception
    {
        ComponentA componentA = (ComponentA) container.lookup( ComponentA.ROLE );

        assertNotNull( componentA );

        assertEquals( "localhost", componentA.getHost() );

        assertEquals( 10000, componentA.getPort() );

        ComponentB componentB = componentA.getComponentB();

        assertNotNull( componentB );

        ComponentC componentC = componentA.getComponentC();

        assertNotNull( componentC );

        ComponentD componentD = componentC.getComponentD();

        assertNotNull( componentD );

        assertEquals( "jason", componentD.getName() );
    }

    public void testComponentCompositionWhereTargetFieldIsAMap()
        throws Exception
    {
        ActivityManager am = (ActivityManager) container.lookup( ActivityManager.ROLE );

        Activity one = am.getActivity( "one" );

        assertNotNull( one );

        assertFalse( one.getState() );

        am.execute( "one" );

        assertTrue( one.getState() );

        Activity two = am.getActivity( "two" );

        assertNotNull( two );

        assertFalse( two.getState() );

        am.execute( "two" );

        assertTrue( two.getState() );
    }

    public void testComponentCompositionWhereTargetFieldIsAList()
        throws Exception
    {
        Pipeline pipeline = (Pipeline) container.lookup( Pipeline.ROLE );

        List valves = pipeline.getValves();

        assertFalse( ( (Valve) valves.get( 0 ) ).getState() );

        assertFalse( ( (Valve) valves.get( 1 ) ).getState() );

        pipeline.execute();

        assertTrue( ( (Valve) valves.get( 0 ) ).getState() );

        assertTrue( ( (Valve) valves.get( 1 ) ).getState() );
    }

    public void testLookupOfInternallyDefinedComponentConfigurator()
        throws Exception
    {
        container.lookup( ComponentConfigurator.ROLE );
    }

    public void testLookupOfComponentThatShouldBeDiscovered()
        throws Exception
    {
        DiscoveredComponent discoveredComponent = (DiscoveredComponent) container.lookup( DiscoveredComponent.ROLE );

        assertNotNull( discoveredComponent );
    }
}
