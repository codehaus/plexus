package org.codehaus.plexus.summit.resolver;

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.codehaus.plexus.summit.AbstractTest;

/**
 * <p/>
 * Test suite for the {@link org.codehaus.plexus.summit.resolver.ClassicResolver}
 * class.
 * </p>
 * <p/>
 * <p/>
 * The ClassicResolver class is a strategy that emulates the Turbine 2.x
 * process of resolving views and modules.
 * </p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class ClassicResolverTest
    extends AbstractTest
{
    /**
     * Classic resolver.
     */
    private ClassicResolver classicResolver;

    /**
     * Our test target.
     */
    private String target;

    /**
     * Return the Test
     */
    public static Test suite()
    {
        return new TestSuite( ClassicResolverTest.class );
    }

    /**
     * Setup the test.
     */
    public void setUp() throws Exception
    {
        super.setUp();
        
        // This is NOT the right way to use a Resolver.  You are
        // supposed to only use the ROLE's interface; however, for
        // testing purposes, we cast to ClassicResolver knowing 100%
        // that we are getting a ClassicResolver because this unit
        // test has its own dedicated plexus conf.
        classicResolver = (ClassicResolver) lookup( Resolver.ROLE, "classic" );

        target = "/science/biology/Mitosis.vm";
    }

    /**
     * Tear down the test.
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        // Nothing to do here yet.
    }

    // -------------------------------------------------------------------------
    // A C C E S S O R S
    // -------------------------------------------------------------------------   

    public void testGetPossibleViews()
        throws Exception
    {
        // Set up the classic resolver
        message( "Testing getPossibleViews(target, targetPrefix)" );

        List possibleViews = classicResolver.getPossibleViews( target, "layouts" );

        for ( Iterator i = possibleViews.iterator(); i.hasNext(); )
        {
            System.out.println( i.next() );
        }

        assertEquals( "layouts/science/biology/Mitosis.vm", possibleViews.get( 0 ) );
        assertEquals( "layouts/science/biology/Index.vm", possibleViews.get( 1 ) );
        assertEquals( "layouts/science/Index.vm", possibleViews.get( 2 ) );
        assertEquals( "layouts/Index.vm", possibleViews.get( 3 ) );

    }

    public void testConfiguredClassicResolver()
        throws Exception
    {
        Resolver resolver = (Resolver) lookup( Resolver.ROLE, "classic" );

        assertEquals( "Error.vm", resolver.getErrorView() );
        assertEquals( "Index.vm", resolver.getDefaultView() );
    }
}
