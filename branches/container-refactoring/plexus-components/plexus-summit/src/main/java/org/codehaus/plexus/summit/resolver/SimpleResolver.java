package org.codehaus.plexus.summit.resolver;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import org.codehaus.plexus.summit.renderer.Renderer;
import org.codehaus.plexus.summit.view.View;

/**
 * @plexus.component
 *   role-hint="simple"
 *
 * <p>A simple resolving strategy that simply attempts to resolve
 * the target view. </p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class SimpleResolver
    extends AbstractResolver
{
    public final static String SCREEN_MODULE = "screenModule";

    public final static String SCREEN_VIEW = "screenView";

    public final static String DEFAULT_SCREEN_MODULE = "Default";

    private Renderer renderer;

    public Resolution resolve( String target )
        throws Exception
    {
        Resolution resolution = new Resolution();

        View screenView = getView( target );

        resolution.put( SCREEN_VIEW, screenView );

        return resolution;
    }

    public Renderer getRenderer( String target )
    {
        return renderer;
    }
}
