package org.codehaus.plexus.summit.pull.tools;

import org.codehaus.plexus.summit.pull.RequestTool;
import org.codehaus.plexus.summit.renderer.Renderer;
import org.codehaus.plexus.summit.resolver.ClassicResolver;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.View;

/**
 * This class is a hack that allows you to render templates via the old Turbine
 * 3 style.  It resolves the template, then looks up the renderer to render the
 * template it.
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2003
 */
public class RendererResolver
    extends ClassicResolver
    implements RequestTool
{
    private RunData data;
    
    public void setRunData(RunData data)
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
        Renderer renderer = ( Renderer ) lookup( Renderer.ROLE );

        View view = getView( target, basedir );

        return renderer.render( data, view.getName() );
    }
}
