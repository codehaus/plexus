package org.codehaus.plexus.summit.resolver;

import org.codehaus.plexus.summit.renderer.Renderer;
import org.codehaus.plexus.summit.view.DefaultView;
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
 *  role-hint="new"
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class NewResolver
    extends AbstractResolver
{
    public final static String LAYOUT_VIEW = "layoutView";

    public final static String NAVIGATION_VIEW = "navigationView";

    public final static String SCREEN_VIEW = "screenView";

    private final static String LAYOUT_TARGET_PREFIX = "layouts";

    private final static String NAVIGATION_TARGET_PREFIX = "navigations";

    private final static String SCREEN_TARGET_PREFIX = "screens";

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

        resolution.put( "screenType", "velocity" );

        return resolution;
    }

    protected Renderer getRenderer( String target )
        throws Exception
    {
        return (Renderer) lookup( Renderer.ROLE, "velocity" );
    }
}
