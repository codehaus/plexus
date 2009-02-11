package org.codehaus.plexus.test;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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

import junit.framework.TestCase;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.discovery.DiscoveredComponent;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.BasicLifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.test.lifecycle.phase.EenyPhase;
import org.codehaus.plexus.test.lifecycle.phase.MeenyPhase;
import org.codehaus.plexus.test.lifecycle.phase.MinyPhase;
import org.codehaus.plexus.test.lifecycle.phase.MoPhase;
import org.codehaus.plexus.test.list.Pipeline;
import org.codehaus.plexus.test.list.Valve;
import org.codehaus.plexus.test.list.ValveFour;
import org.codehaus.plexus.test.list.ValveOne;
import org.codehaus.plexus.test.list.ValveThree;
import org.codehaus.plexus.test.list.ValveTwo;
import org.codehaus.plexus.test.map.Activity;
import org.codehaus.plexus.test.map.ActivityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlexusContainerTest
    extends TestCase
{
    private String basedir;

    private ClassLoader classLoader;

    private String configuration;

    private DefaultPlexusContainer container;

    public PlexusContainerTest( String name )
    {
        super( name );
    }

    public void setUp()
        throws Exception
    {
        basedir = System.getProperty( "basedir" );

        classLoader = getClass().getClassLoader();

        configuration = "/" + getClass().getName().replace( '.', '/' ) + ".xml";

        assertNotNull( classLoader );

        // ----------------------------------------------------------------------------
        // Context
        // ----------------------------------------------------------------------------

        Map<Object, Object> context = new HashMap<Object, Object>();

        context.put( "basedir", basedir );

        context.put( "plexus.home", basedir + "/target/plexus-home" );

        LifecycleHandler arbitrary = new BasicLifecycleHandler( "arbitrary" );
        arbitrary.addBeginSegment( new EenyPhase() );
        arbitrary.addBeginSegment( new MeenyPhase() );
        arbitrary.addBeginSegment( new MinyPhase() );
        arbitrary.addBeginSegment( new MoPhase() );

        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
            .setName( "test" )
            .setContext( context )
            .setContainerConfiguration( configuration )
            .addLifecycleHandler( arbitrary );

        container = new DefaultPlexusContainer( containerConfiguration );
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
        DefaultServiceB serviceB = (DefaultServiceB) container.lookup( ServiceB.class );

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
        DefaultServiceE serviceE = (DefaultServiceE) container.lookup( ServiceE.class );

        // Make sure the component is alive.
        assertNotNull( serviceE );

        // Make sure the component went through all the lifecycle phases
        assertEquals( true, serviceE.enableLogging );

        assertEquals( true, serviceE.contextualize );

        assertEquals( true, serviceE.initialize );

        assertEquals( true, serviceE.start );

        assertEquals( false, serviceE.stop );

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
        DefaultServiceC serviceC1 = (DefaultServiceC) container.lookup( ServiceC.class, "first-instance" );

        // Make sure the component is alive.
        assertNotNull( serviceC1 );

        assertTrue( serviceC1.started );

        assertFalse( serviceC1.stopped );

        // Retrieve a second reference to the same component.
        DefaultServiceC serviceC2 = (DefaultServiceC) container.lookup( ServiceC.class, "first-instance" );

        // Make sure component is alive.
        assertNotNull( serviceC2 );

        assertTrue( serviceC2.started );

        assertFalse( serviceC2.stopped );

        // Let's make sure it gave us back the same component.
        assertSame( serviceC1, serviceC2 );

        container.release( serviceC1 );

        // The component should still be alive.
        assertTrue( serviceC2.started );

        assertTrue( serviceC2.stopped );

        container.release( serviceC2 );

        // The component should now have been stopped.
        assertTrue( serviceC2.started );

        assertTrue( serviceC2.stopped );
    }

    /*
     * Check that distinct components with the same implementation are managed correctly.
     */
    public void testMultipleSingletonComponentInstances()
        throws Exception
    {
        // Retrieve an instance of component c.
        DefaultServiceC serviceC1 = (DefaultServiceC) container.lookup( ServiceC.class, "first-instance" );

        // Make sure the component is alive.
        assertNotNull( serviceC1 );

        assertTrue( serviceC1.started );

        assertFalse( serviceC1.stopped );

        // Retrieve an instance of component c, with a different role hint.
        // This should give us a different component instance.
        DefaultServiceC serviceC2 = (DefaultServiceC) container.lookup( ServiceC.class, "second-instance" );

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
        DefaultServiceH serviceH = (DefaultServiceH) container.lookup( ServiceH.class );

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
        Map<String, ServiceC> components = container.lookupMap( ServiceC.class );

        assertNotNull( components );

        assertEquals( 2, components.size() );

        ServiceC component = components.get( "first-instance" );

        assertNotNull( component );

        component = components.get( "second-instance" );

        assertNotNull( component );

        container.releaseAll( components );
    }

    public void testAutomatedComponentConfigurationUsingXStreamPoweredComponentConfigurator()
        throws Exception
    {
        Component component = container.lookup( Component.class );

        assertNotNull( component );

        assertNotNull( component.getActivity() );

        assertEquals( "localhost", component.getHost() );

        assertEquals( 10000, component.getPort() );
    }

    public void testAutomatedComponentComposition()
        throws Exception
    {
        ComponentA componentA = container.lookup( ComponentA.class );

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
        ActivityManager am = container.lookup( ActivityManager.class );

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

    public void testComponentCompositionWhereTargetFieldIsAPartialMap()
        throws Exception
    {
        ActivityManager am = container.lookup( ActivityManager.class, "slim" );

        assertEquals( 1, am.getActivityCount() );

        Activity one = am.getActivity( "one" );

        assertNotNull( one );

        assertFalse( one.getState() );

        am.execute( "one" );

        assertTrue( one.getState() );
    }

    public void testComponentCompositionWhereTargetFieldIsAList()
        throws Exception
    {
        Pipeline pipeline = container.lookup( Pipeline.class );

        List valves = pipeline.getValves();

        assertFalse( ( (Valve) valves.get( 0 ) ).getState() );

        assertFalse( ( (Valve) valves.get( 1 ) ).getState() );

        pipeline.execute();

        assertTrue( ( (Valve) valves.get( 0 ) ).getState() );

        assertTrue( ( (Valve) valves.get( 1 ) ).getState() );
    }

    public void testComponentCompositionWhereTargetFieldIsAPartialList()
        throws Exception
    {
        Pipeline pipeline = container.lookup( Pipeline.class, "slim" );

        List valves = pipeline.getValves();

        assertEquals( valves.size(), 1 );

        assertFalse( ( (Valve) valves.get( 0 ) ).getState() );

        pipeline.execute();

        assertTrue( ( (Valve) valves.get( 0 ) ).getState() );
    }

    public void testLookupOfComponentThatShouldBeDiscovered()
        throws Exception
    {
        DiscoveredComponent discoveredComponent = container.lookup( DiscoveredComponent.class );

        assertNotNull( discoveredComponent );
    }

    public void testStartableComponentSnake()
        throws Exception
    {
        StartableComponent ca = container.lookup( StartableComponent.class, "A-snake" );

        assertNotNull( ca );

        ca.assertStartOrderCorrect();

        container.dispose();

        ca.assertStopOrderCorrect();
    }

    public void testStartableComponentTree()
        throws Exception
    {
        StartableComponent ca = container.lookup( StartableComponent.class, "A-tree" );

        assertNotNull( ca );

        ca.assertStartOrderCorrect();

        container.dispose();

        ca.assertStopOrderCorrect();
    }

    public void testLookupCircularity()
        throws Exception
    {
        try
        {
            container.lookup( CircularComponent.class, "A" );
            fail("Expected ComponentLookupException due to circularity");
        }
        catch ( ComponentLookupException e )
        {
            // todo actually test nested exception is as expected when
        }
    }
}
