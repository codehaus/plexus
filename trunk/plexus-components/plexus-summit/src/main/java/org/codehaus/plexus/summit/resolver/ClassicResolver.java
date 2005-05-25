package org.codehaus.plexus.summit.resolver;

import org.codehaus.plexus.summit.renderer.Renderer;
import org.codehaus.plexus.summit.view.View;

/**
 * <p>This resolving strategy emulates the Turbine 2.x process of matching a target
 * up with:</p>
 * <p/>
 * <p/>
 * <ul>
 * <li> A layout module</li>
 * <li> A layout view</li>
 * <li> A screen module</li>
 * </ul>
 * </p>
 * <p/>
 * <p>The navigation views are stated explicitly in the layout view according to
 * the resolving process in Turbine 2.x so this resolver doesn't have to do any
 * work in this respect. </p>
 *
 * @plexus.component
 *
 * @plexus.role org.codehaus.plexus.summit.resolver.Resolver
 *
 * @plexus.role-hint classic
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class ClassicResolver
    extends AbstractResolver
{
    /**
     * Tag for the layout view in the resolution.
     */
    public final static String LAYOUT_VIEW = "layoutView";

    /**
     * Tag for the navigation module in the resolution.
     */
    public final static String NAVIGATION_VIEW = "navigationView";

    /**
     * Tag for the screen view in the resolution.
     */
    public final static String SCREEN_VIEW = "screenView";

    /**
     * The renderer for this resolution
     */
    public final static String RENDERER = "classic.renderer";

    /**
     * Tag for the layouts directory.
     */
    private final static String LAYOUT_TARGET_PREFIX = "layouts";

    /**
     * Tag for the navigations directory.
     */
    private final static String NAVIGATION_TARGET_PREFIX = "navigations";

    /**
     * Tag for the screens directory.
     */
    private final static String SCREEN_TARGET_PREFIX = "screens";

    /**
     * @plexus.requirement
     *
     * @plexus.role-hint velocity
     */
    private Renderer renderer;

    public ClassicResolver()
    {
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

    public Renderer getRenderer( String target )
    {
        return renderer;
    }
}
