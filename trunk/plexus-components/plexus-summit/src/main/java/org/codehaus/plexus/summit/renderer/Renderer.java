package org.codehaus.plexus.summit.renderer;

import java.io.Writer;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * <p>A <code>Renderer</code> is a
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Renderer
{
    String ROLE = Renderer.class.getName();

    String render( RunData data, String view )
        throws SummitException, Exception;

    void render( RunData data, String view, Writer writer )
        throws SummitException, Exception;

    boolean viewExists( String view );
}
