package org.codehaus.plexus.summit.renderer;

import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * <p>Base class from which all <code>Renderer</code>s are
 * derived.</p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractRenderer
    extends AbstractSummitComponent
    implements Renderer
{
    public String render( RunData data, String view )
        throws SummitException, Exception
    {
        StringWriter writer = new StringWriter();

        render( data, view, writer );

        return writer.toString();
    }

    public abstract void render( RunData data, String view, Writer writer )
        throws SummitException, Exception;

    public abstract boolean viewExists( String view );
}
