package org.codehaus.plexus.summit.resolver;

import java.util.ArrayList;
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

    public void testDefaultBaseName()
    {
        // defaultBaseName
        message( "Testing {get|set}DefaultBaseName()" );
        classicResolver.setDefaultBaseName( "Default" );
        assertEquals( "Default", classicResolver.getDefaultBaseName() );
    }

    public void testDefaultLayoutModule()
    {
        // defaultLayoutModule
        message( "Testing {get|set}DefaultLayoutModule()" );
        classicResolver.setDefaultLayoutModule( "org.codehaus.plexus.summit.modules.layouts.DefaultLayoutModule" );
        assertEquals( "org.codehaus.plexus.summit.modules.layouts.DefaultLayoutModule", classicResolver.getDefaultLayoutModule() );
    }

    public void testDefaultScreenModule()
    {
        // defaultScreenModule
        message( "Testing {get|set}DefaultScreenModule()" );
        classicResolver.setDefaultScreenModule( "org.codehaus.plexus.summit.modules.screens.DefaultScreenModule" );
        assertEquals( "org.codehaus.plexus.summit.modules.screens.DefaultScreenModule", classicResolver.getDefaultScreenModule() );
    }

    public void testModulePackages()
    {
        // modulePackages
        message( "Testing {get|set}ModulePackages()" );
        List modulePackages = new ArrayList();
        modulePackages.add( "org.codehaus.plexus.summit.modules" );
        modulePackages.add( "org.zenplex.tambora.modules" );
        classicResolver.setModulePackages( modulePackages );
        assertEquals( 2, classicResolver.getModulePackages().size() );
        assertEquals( "org.codehaus.plexus.summit.modules", classicResolver.getModulePackages().get( 0 ) );
        assertEquals( "org.zenplex.tambora.modules", classicResolver.getModulePackages().get( 1 ) );
    }

    public void testClearModulePackages()
    {
        // reset the module packages list to nothing
        message( "Testing clearModulePackages()" );
        classicResolver.clearModulePackages();
        assertEquals( 0, classicResolver.getModulePackages().size() );
    }

    public void testAddModulePackages()
    {
        // add module packages using the add method
        message( "Testing addModulePackage()" );

        classicResolver.addModulePackage( "org.codehaus.plexus.summit.modules" );

        classicResolver.addModulePackage( "org.zenplex.tambora.modules" );

        // The size is 3 here because if no packages are specified in
        // the plexus config for this component, the "" package is
        // added by default.

        assertEquals( 3, classicResolver.getModulePackages().size() );

        assertEquals( "", classicResolver.getModulePackages().get( 0 ) );

        assertEquals( "org.codehaus.plexus.summit.modules", classicResolver.getModulePackages().get( 1 ) );

        assertEquals( "org.zenplex.tambora.modules", classicResolver.getModulePackages().get( 2 ) );
    }

    public void testGetPossibleViews()
        throws Exception
    {
        // Set up the classic resolver
        message( "Testing getPossibleViews(target, targetPrefix)" );
        classicResolver.clearModulePackages();
        classicResolver.setDefaultBaseName( "Default" );
        classicResolver.setDefaultViewExtension( "vm" );

        List possibleViews = classicResolver.getPossibleViews( target, "layouts" );

        for ( Iterator i = possibleViews.iterator(); i.hasNext(); )
        {
            System.out.println( i.next() );
        }

        assertEquals( "layouts/science/biology/Mitosis.vm", possibleViews.get( 0 ) );
        assertEquals( "layouts/science/biology/Default.vm", possibleViews.get( 1 ) );
        assertEquals( "layouts/science/Default.vm", possibleViews.get( 2 ) );
        assertEquals( "layouts/Default.vm", possibleViews.get( 3 ) );

    }

    public void testGetView()
        throws Exception
    {
        // Set up the classic resolver
        classicResolver.clearModulePackages();
        classicResolver.setDefaultBaseName( "Default" );
        classicResolver.setDefaultViewExtension( "vm" );
        
        //View view = classicResolver.getView(target);
        //assertNotNull( view );
    }

    public void testConfiguredClassicResolver()
        throws Exception
    {
        Resolver resolver = (Resolver) lookup( Resolver.ROLE, "classic" );

        assertEquals( "Error.vm", resolver.getErrorView() );
        assertEquals( "Default", resolver.getDefaultBaseName() );
        assertEquals( "vm", resolver.getDefaultViewExtension() );
        assertEquals( "Index.vm", resolver.getDefaultView() );
    }
}
