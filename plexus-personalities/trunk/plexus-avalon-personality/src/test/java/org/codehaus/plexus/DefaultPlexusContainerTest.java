package org.codehaus.plexus;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.component.repository.io.PlexusTools;

/**
 *  @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 */
public class DefaultPlexusContainerTest
    extends TestCase
{
    /** Configuration stream to use for default container test. */
    private InputStream configurationStream;

    /** Default container test classloader. */
    private ClassLoader classLoader;

    /** Basedir for default container test. */
    private String basedir;

    /** Default Container. */
    private DefaultPlexusContainer container;

    /**
     * Constructor for the PlexusTest object
     *
     * @param name
     */
    public DefaultPlexusContainerTest( String name )
    {
        super( name );
    }

    public void setUp()
        throws Exception
    {
        basedir = System.getProperty( "basedir" );

        classLoader = getClass().getClassLoader();

        configurationStream = DefaultPlexusContainerTest.class.getResourceAsStream( "DefaultPlexusContainerTest.xml" );

        // Make sure our testing necessities are alive.
        assertNotNull( configurationStream );

        assertNotNull( classLoader );

        container = new DefaultPlexusContainer();

        container.addContextValue( "basedir", basedir );

        container.addContextValue( "plexus.home", basedir + "/target/plexus-home" );

        PlexusConfiguration config = PlexusTools.buildConfiguration(
                "<User Specified Configuration Reader>",
                new InputStreamReader( configurationStream ) );

        container.setConfiguration( PlexusConfigurationMerger.merge( container.getConfiguration(), config ) );
    }

    public void tearDown()
        throws Exception
    {
        container.dispose();
        container = null;
    }

    /**
     * Test passage through standard avalon lifecycle.
     *
     * @throws Exception
     */
    public void testAvalonLifecyclePassage()
        throws Exception
    {
        // ----------------------------------------------------------------------
        //  ServiceA
        //
        //  Implements all the standard Avalon lifecycle phases.
        // ----------------------------------------------------------------------

        // Retrieve an instance of component a.

        DefaultServiceA serviceA = (DefaultServiceA) container.lookup( ServiceA.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceA );

        assertEquals( true, serviceA.enableLogging );

        assertEquals( true, serviceA.contextualize );

        assertEquals( true, serviceA.service );

        assertEquals( true, serviceA.configure );

        assertEquals( true, serviceA.initialize );

        assertEquals( true, serviceA.start );

        container.release( serviceA );

        assertEquals( true, serviceA.stop );

        assertEquals( true, serviceA.dispose );

        // make sure we get the same manager back everytime
        DefaultServiceA a0 = (DefaultServiceA) container.lookup( ServiceA.ROLE );

        DefaultServiceA a1 = (DefaultServiceA) container.lookup( ServiceA.ROLE );

        DefaultServiceA a2 = (DefaultServiceA) container.lookup( ServiceA.ROLE );

        assertTrue( a0.equals( a1 ) );

        assertTrue( a1.equals( a2 ) );

        assertTrue( a2.equals( a0 ) );

	    // make sure that the component wasn't recycled
        assertFalse( serviceA.equals( a0 ) );

        container.release( a0 );

        container.release( a1 );

        container.release( a2 );
    }

    /**
     * Test passage through arbitrary component lifecycle.
     *
     * @throws Exception
     */
    public void testArbitraryLifecylePassage()
        throws Exception
    {
        // ----------------------------------------------------------------------
        //  ServiceB
        //
        //  Implements the special Plexus contextualize and component phases with
        //  the rest being the standard Avalon ones.
        // ----------------------------------------------------------------------

        // Retrieve an manager of component b.
        DefaultServiceB serviceB1 = (DefaultServiceB) container.lookup( ServiceB.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceB1 );

        assertEquals( true, serviceB1.enableLogging );

        assertEquals( true, serviceB1.contextualize );

        assertEquals( true, serviceB1.service );

        assertEquals( true, serviceB1.configure );

        assertEquals( true, serviceB1.initialize );

        assertEquals( true, serviceB1.start );

        assertNotNull( serviceB1.getClassLoader() );

        container.release( serviceB1 );

        // Retrieve another
        DefaultServiceB serviceB2 = (DefaultServiceB) container.lookup( ServiceB.ROLE );

        assertNotNull( serviceB2 );

        container.release( serviceB2 );
    }

    /**
     * Test per-lookup instantiation strategy.
     *
     * @throws Exception
     */
    public void testPerLookupInstantiationStrategy()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Per-lookup component
        // ----------------------------------------------------------------------

        // Retrieve an manager of component e.
        DefaultServiceE serviceE1 = (DefaultServiceE) container.lookup( ServiceE.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceE1 );

        // Check the lifecycle
        assertEquals( true, serviceE1.enableLogging );

        assertEquals( true, serviceE1.contextualize );

        assertEquals( true, serviceE1.service );

        assertEquals( true, serviceE1.configure );

        assertEquals( true, serviceE1.initialize );

        assertEquals( true, serviceE1.start );

        // Retrieve another
        DefaultServiceE serviceE2 = (DefaultServiceE) container.lookup( ServiceE.ROLE );

        // Make sure the component is alive.
        assertNotNull( serviceE2 );

        // Check the lifecycle
        assertEquals( true, serviceE2.enableLogging );

        assertEquals( true, serviceE2.contextualize );

        assertEquals( true, serviceE2.service );

        assertEquals( true, serviceE2.configure );

        assertEquals( true, serviceE2.initialize );

        assertEquals( true, serviceE2.start );

        assertNotSame( serviceE1, serviceE2 );

        container.release( serviceE1 );

        container.release( serviceE2 );
    }

    public void testServiceableComponent()
        throws Exception
    {
        ServiceableComponent component = (ServiceableComponent)
            container.lookup( ServiceableComponent.ROLE );

        assertNotNull( component );

        assertTrue( component.simpleServiceLookup() );

        assertTrue( component.roleBasedServiceLookup() );
    }

    public void testConfiguration()
        throws Exception
    {
        DefaultConfigureService service = (DefaultConfigureService) 
            container.lookup( ConfigureService.ROLE );

        assertTrue( service.configure );
        assertTrue( service.blah );
        assertTrue( service.blahHasChildren );
        assertTrue( service.blehHasAttribute );
    }
}
