package org.codehaus.plexus.summit.pull.tools;

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
