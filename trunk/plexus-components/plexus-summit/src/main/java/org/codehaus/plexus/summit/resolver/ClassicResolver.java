package org.codehaus.plexus.summit.resolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.summit.view.View;

/**
 * <p>This resolving strategy emulates the Turbine 2.x process of matching a target
 * up with:</p>
 *
 * <p>
 * <ul>
 *   <li> A layout module</li>
 *   <li> A layout view</li>
 *   <li> A screen module</li>
 * </ul>
 * </p>
 *
 * <p>The navigation views are stated explicitly in the layout view according to
 * the resolving process in Turbine 2.x so this resolver doesn't have to do any
 * work in this respect. </p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class ClassicResolver
    extends AbstractResolver
{


    /** * Module packages that will be searched by this resolver. */
    private List modulePackages;

    /**
     * A String representation of the module packages that have been searched.
     * Used in exception handling. the list of locations searched can be
     * displayed
     */
    private StringBuffer modulePackagesNames;

    /** Default layout module */
    public final static String DEFAULT_LAYOUT_MODULE = "Default";

    /** Key to retrieve the default layout module from the configuration */
    public final static String DEFAULT_LAYOUT_MODULE_KEY = "defaultLayoutModule";

    /** Default navigation module */
    public final static String DEFAULT_NAV_MODULE = "Default";

    /** Key to retrieve the default navigation module from the configuration */
    public final static String DEFAULT_NAV_MODULE_KEY = "defaultLayoutModule";

    /** Default screen module */
    public final static String DEFAULT_SCREEN_MODULE = "Default";

    /** Key to retrieve the default screen module from the configuration */
    public final static String DEFAULT_SCREEN_MODULE_KEY = "defaultLayoutModule";

    /** Tag for the layout module in the resolution. */
    public final static String LAYOUT_MODULE = "layoutModule";

    /** Tag for the layout view in the resolution. */
    public final static String LAYOUT_VIEW = "layoutView";

    /** Tag for the navigation module in the resolution. */
    public final static String NAVIGATION_MODULE = "navigationModule";

    /** Tag for the navigation module in the resolution. */
    public final static String NAVIGATION_VIEW = "navigationView";

    /** Tag for the screen module in the resolution. */
    public final static String SCREEN_MODULE = "screenModule";

    /** Tag for the screen view in the resolution. */
    public final static String SCREEN_VIEW = "screenView";

    /** Tag for the layouts directory. */
    private final static String LAYOUT_TARGET_PREFIX = "layouts";

    /** Tag for the navigations directory. */
    private final static String NAVIGATION_TARGET_PREFIX = "navigations";

    /** Tag for the screens directory. */
    private final static String SCREEN_TARGET_PREFIX = "screens";

    /**
     * The layout module to use when no match is found using
     * the target's name.
     */
    private String defaultLayoutModule;

    /**
     * The navigation module to use when no match is found using the
     * target's name.
     */
    private String defaultNavigationModule;

    /**
     * The screen module to use when no match is found using the
     * target's name.
     */
    private String defaultScreenModule;

    public ClassicResolver()
    {
        modulePackages = new ArrayList();

        modulePackages.add( "" );

        modulePackagesNames = new StringBuffer();
    }

    // ----------------------------------------------------------------------
    // Module stuff
    // ----------------------------------------------------------------------


    /**
     * Set the list of package in which <b>Modules</b> are searched for.
     *
     * @param modulePackages
     */
    void setModulePackages( List modulePackages )
    {
        this.modulePackages = modulePackages;
    }

    /**
     * Get the list of package in which <b>Modules</b> are searched for.
     *
     * @return String The module packages
     */
    public List getModulePackages()
    {
        return modulePackages;
    }

    /**
     * Add a module package to the search list.
     *
     * @param modulePackage module package to add to search list
     */
    void addModulePackage( String modulePackage )
    {
        modulePackages.add( modulePackage );
        modulePackagesNames.append( modulePackage ).append( "\n" );
    }

    // -------------------------------------------------------------------------
    // M O D U L E  R E L A T E D  M E T H O D S
    // -------------------------------------------------------------------------

    /**
     * Clear the set of module packages listed with this resolver.
     */
    public void clearModulePackages()
    {
        // clear() doesn't seem to be implemented in the FastArrayList so
        // we are doing the clear manually.

        int size = modulePackages.size();

        for ( int i = 0; i < size; i++ )
        {
            modulePackages.remove( 0 );
        }
    }



    /**
     */
    List getPossibleModules( String target, String targetPrefix )
        throws Exception
    {
        ArrayList modules = new ArrayList();

        List moduleSuffixes =
            ResolverUtils.getPossibleModuleSuffixes( target, getDefaultBaseName() );

        for ( Iterator i = modulePackages.iterator(); i.hasNext(); )
        {
            String modulePackage = (String) i.next();

            for ( Iterator j = moduleSuffixes.iterator(); j.hasNext(); )
            {
                String module;

                if ( targetPrefix != null && targetPrefix.length() > 0 )
                {
                    module = modulePackage + "." + targetPrefix + "." + j.next();
                }
                else
                {
                    module = modulePackage + "." + j.next();
                }

                modules.add( module );
            }
        }

        return modules;
    }



    // -------------------------------------------------------------------------
    // A C C E S S O R S
    // -------------------------------------------------------------------------

    /**
     * Set the default layout module to use when a layout module cannot be
     * resolved by the standard means.
     *
     * @param defaultLayoutModule Default layout module to use when a layout
     *      module cannot be resolved.
     */
    public void setDefaultLayoutModule( String defaultLayoutModule )
    {
        this.defaultLayoutModule = defaultLayoutModule;
    }

    /**
     * Get the default layout module.
     *
     * @return String Default layout module.
     */
    public String getDefaultLayoutModule()
    {
        return defaultLayoutModule;
    }

    /**
     * Set the default navigation module to use when a navigation module cannot
     * be resolved by the standard means.
     *
     * @param defaultNavigationModule Default navigation module to use when a
     *      navigation module cannot be resolved.
     */
    public void setDefaultNavigationModule( String defaultNavigationModule )
    {
        this.defaultNavigationModule = defaultNavigationModule;
    }

    /**
     * Get the default navigation module.
     *
     * @return String Default navigation module.
     */
    public String getDefaultNavigationModule()
    {
        return defaultNavigationModule;
    }

    /**
     * Set the default screen module to use when a screen module cannot be
     * resolved by the standard means.
     *
     * @param defaultScreenModule Default screen module to use when a screen
     *      module cannot be resolved.
     */
    public void setDefaultScreenModule( String defaultScreenModule )
    {
        this.defaultScreenModule = defaultScreenModule;
    }

    /**
     * Get the default screen module.
     *
     * @return String Default screen module.
     */
    public String getDefaultScreenModule()
    {
        return defaultScreenModule;
    }

    // -------------------------------------------------------------------------
    // M E T H O D S
    // -------------------------------------------------------------------------

    /**
     * Description of the Method
     */
    public Resolution resolve( String target )
        throws Exception
    {
        Resolution resolution = new Resolution();

        // ---------------------------------------------------------------
        // Layout
        // ---------------------------------------------------------------

        View layoutView = getView( target, LAYOUT_TARGET_PREFIX );

        resolution.put( LAYOUT_VIEW, layoutView );

        // ---------------------------------------------------------------
        // Navigation
        // ---------------------------------------------------------------

        View navigationView = getView( target, NAVIGATION_TARGET_PREFIX );

        resolution.put( NAVIGATION_VIEW, navigationView );

        // ---------------------------------------------------------------
        // Screen
        // ---------------------------------------------------------------

        View screenView = getView( target, SCREEN_TARGET_PREFIX );

        resolution.put( SCREEN_VIEW, screenView );

        return resolution;
    }
}
