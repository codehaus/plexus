package org.codehaus.plexus.summit.display;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.renderer.Renderer;
import org.codehaus.plexus.summit.resolver.ClassicResolver;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.View;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * <p>
 * A <code>ClassicDisplay</code> takes a <code>Resolution</code> computed
 * by the <code>ClassicResolver</code> and displays the resolution according
 * to the Turbine 2.x model where we have something like the following:
 * </p>
 *
 * <pre>
 *
 * +------------------------------------------------+
 * | DefaultLayout.vm                               |
 * | +--------------------------------------------+ |
 * | | TopNavigation.vm                           | |
 * | +--------------------------------------------+ |
 * |                                                |
 * | +--------------------------------------------+ |
 * | |                                            | |
 * | |                                            | |
 * | | $screenViewContent                         | |
 * | |                                            | |
 * | |                                            | |
 * | +--------------------------------------------+ |
 * |                                                |
 * | +--------------------------------------------+ |
 * | | TopNavigation.vm                           | |
 * | +--------------------------------------------+ |
 * +------------------------------------------------+
 *
 * </pre>
 *
 * <p>
 * This example uses Velocity templates as an example but this
 * <code>Display</code> strategy could just as easily be applied
 * to a set of JSPs.
 * </p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class ClassicBufferedDisplay
    extends AbstractDisplay
{
    public void render( RunData data )
        throws Exception
    {
        Renderer renderer = (Renderer) lookup( Renderer.ROLE );

        ViewContext viewContext = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        viewContext.put( "data", data );

        // ---------------------------------------------------------------
        // Screen
        // ---------------------------------------------------------------

        View screenView = data.getResolution().getView( ClassicResolver.SCREEN_VIEW );

        String screenViewContent = renderer.render( data, screenView.getName() );

        viewContext.put( "screenViewContent", screenViewContent );

        // ---------------------------------------------------------------
        // Layout
        // ---------------------------------------------------------------

        View layoutView = data.getResolution().getView( ClassicResolver.LAYOUT_VIEW );

        renderer.render( data, layoutView.getName(), getWriter( data ) );
    }
}
