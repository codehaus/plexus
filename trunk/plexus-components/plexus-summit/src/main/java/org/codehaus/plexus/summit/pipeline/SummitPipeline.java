package org.codehaus.plexus.summit.pipeline;

import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.pipeline.valve.Valve;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;

/**
 * Flexible implementation of a {@link org.codehaus.plexus.summit.pipeline.Pipeline}.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 */
public class SummitPipeline
    extends AbstractSummitComponent
    implements Pipeline, Initializable
{
    protected String name;

    protected List valves;

    public String getName()
    {
        return name;
    }

    public void invoke( RunData data )
        throws SummitException, IOException
    {
        for ( Iterator iterator = valves.iterator(); iterator.hasNext(); )
        {
            Valve phase = (Valve) iterator.next();

            try
            {
                phase.invoke( data );
            }
            catch ( Exception e )
            {
                throw new SummitException( "Cannot invoke valve:",  e );
            }
        }
    }

    public void initialize()
        throws Exception
    {
        for ( Iterator iterator = valves.iterator(); iterator.hasNext(); )
        {
            Valve phase = (Valve) iterator.next();

            phase.enableLogging( getLogger() );

            phase.setServiceManager( getContainer() );
        }
    }
}
