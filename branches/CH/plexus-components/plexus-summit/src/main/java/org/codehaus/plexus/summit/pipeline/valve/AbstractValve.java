package org.codehaus.plexus.summit.pipeline.valve;

import java.io.IOException;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.PlexusContainer;

/**
 * Valve that can be used as the basis of Valve implementations.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractValve
    extends AbstractLogEnabled
    implements Valve
{
    private PlexusContainer container;

    public void initialize()
        throws Exception
    {
    }

    public abstract void invoke( RunData data )
        throws IOException, SummitException;

    public PlexusContainer getServiceManager()
    {
        return container;
    }

    public void setServiceManager( PlexusContainer container )
    {
        this.container = container;
    }


}


