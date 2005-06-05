package org.codehaus.plexus.summit.pipeline.valve;

import java.io.IOException;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

public interface Valve
{
    static final String ROLE = Valve.class.getName();

    void invoke( RunData data )
        throws IOException, ValveInvocationException;
}
