package org.codehaus.plexus.summit.resolver;

import org.codehaus.plexus.summit.view.View;

/**
 * <p>A simple resolving strategy that simply attempts to resolve
 * the target view. </p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class SimpleResolver
    extends AbstractResolver
{
    public final static String SCREEN_MODULE = "screenModule";

    public final static String SCREEN_VIEW = "screenView";

    public final static String DEFAULT_SCREEN_MODULE = "Default";

    public Resolution resolve( String target )
        throws Exception
    {
        Resolution resolution = new Resolution();

        View screenView = getView( target );

        resolution.put( SCREEN_VIEW, screenView );

       return resolution;
    }
}
