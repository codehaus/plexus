package org.codehaus.plexus.summit.pipeline;

import java.io.IOException;

import org.codehaus.plexus.summit.SummitComponent;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * <p>A <code>Pipeline</code> is the entity which controls the flow
 * of the request/response lifecyle in Summit.</p>
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Pipeline
    extends SummitComponent
{
    public final static String ROLE = Pipeline.class.getName();

    public static final String SELECTOR_ROLE = Pipeline.class.getName() + "Selector";

    /**
     * <p>Cause the specified request and response to be processed by
     * the sequence of Valves associated with this pipeline, until one
     * of these Valves decides to end the processing.</p>
     *
     * <p>The implementation must ensure that multiple simultaneous
     * requests (on different threads) can be processed through the
     * same Pipeline without interfering with each other's control
     * flow.</p>
     *
     * @param data The run-time information, including the servlet
     * request and response we are processing.
     *
     * @exception IOException an input/output error occurred.
     */
    void invoke( RunData data )
        throws SummitException, IOException;
}
