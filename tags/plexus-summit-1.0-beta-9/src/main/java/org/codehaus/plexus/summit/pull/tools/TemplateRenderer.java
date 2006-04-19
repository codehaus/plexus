package org.codehaus.plexus.summit.pull.tools;

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

import org.codehaus.plexus.summit.pull.RequestTool;
import org.codehaus.plexus.summit.renderer.Renderer;
import org.codehaus.plexus.summit.resolver.AbstractResolver;
import org.codehaus.plexus.summit.resolver.Resolution;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.View;

/**
 * This class is a hack that allows you to render templates via the old Turbine
 * 3 style.  It resolves the template, then looks up the renderer to render the
 * template it.
 *
 * @plexus.component
 *  role="org.codehaus.plexus.summit.pull.tools.TemplateRenderer"
 *  instantiation-strategy="per-lookup"
 *
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2003
 */
public class TemplateRenderer
    extends AbstractResolver
    implements RequestTool
{
    private RunData data;

    /**
     * @plexus.requirement
     *  role-hint="velocity"
     */
    private Renderer renderer;

    public void setRunData( RunData data )
    {
        this.data = data;
    }

    public void refresh()
    {
        this.data = null;
    }

    public String render( String basedir, String target )
        throws Exception
    {
        View view = getView( target, basedir );

        return getRenderer( target ).render( data, view.getName() );
    }

    protected Renderer getRenderer( String target )
        throws Exception
    {
        return renderer;
    }

    public Resolution resolve( String view )
        throws Exception
    {
        return null;
    }
}
