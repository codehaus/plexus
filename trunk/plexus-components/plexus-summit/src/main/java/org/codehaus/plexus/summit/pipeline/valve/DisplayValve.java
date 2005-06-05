package org.codehaus.plexus.summit.pipeline.valve;

import java.io.IOException;

import org.codehaus.plexus.summit.display.Display;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @plexus.component
 *  role-hint="org.codehaus.plexus.summit.pipeline.valve.DisplayValve"
 *
 * @todo the encoding needs to be configurable.
 * @todo Valves need to have an initialization phase
 */
public class DisplayValve
    extends AbstractValve
{
    /**
     * @plexus.configuration
     *  default-value="new"
     */
    private String display;

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
    {
        try
        {
            Display d = (Display) data.lookup( Display.ROLE, display );

            d.render( data );
        }
        catch ( Exception e )
        {
            throw new ValveInvocationException( "Can't display!", e );
        }
    }
}
