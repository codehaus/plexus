package org.codehaus.plexus.summit.pipeline.valve;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.PlexusContainer;

import java.io.IOException;

public interface Valve
{
    static final String ROLE = Valve.class.getName();

    void invoke( RunData data )
        throws IOException, SummitException;

    void enableLogging( Logger logger );

    PlexusContainer getServiceManager();

    void setServiceManager( PlexusContainer container );

    void initialize()
        throws Exception;
}
