/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.summit.resolver;

import org.codehaus.plexus.summit.view.DefaultView;
import org.codehaus.plexus.summit.view.View;

import javax.swing.text.*;

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
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
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

        if ( target.indexOf( "form" ) > 0 )
        {
            View layoutView = getView( "Dummy.vm", LAYOUT_TARGET_PREFIX );

            resolution.put( LAYOUT_VIEW, layoutView );
        }
        else
        {
            View layoutView = getView( target, LAYOUT_TARGET_PREFIX );

            resolution.put( LAYOUT_VIEW, layoutView );
        }

        // ---------------------------------------------------------------
        // Navigation
        // ---------------------------------------------------------------

        View navigationView = getView( target, NAVIGATION_TARGET_PREFIX );

        resolution.put( NAVIGATION_VIEW, navigationView );

        // ---------------------------------------------------------------
        // Screen
        // ---------------------------------------------------------------

        // This is where we need to look at the target and figure out what
        // kind of view we want: velocity template view or form view.

        if ( target.indexOf( "form" ) > 0 )
        {
            View screenView = new DefaultView( target.substring( target.indexOf( "." ) ) );

            resolution.put( SCREEN_VIEW, screenView );

            resolution.put( "screenType", "form" );
        }
        else
        {
            View screenView = getView( target, SCREEN_TARGET_PREFIX );

            resolution.put( SCREEN_VIEW, screenView );

            resolution.put( "screenType", "velocity" );
        }

        return resolution;
    }
}
