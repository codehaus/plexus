package org.codehaus.plexus.summit.pipeline.valve;

import java.io.IOException;

import org.codehaus.plexus.summit.display.Display;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @todo the encoding needs to be configurable.
 * @todo Valves need to have an initialization phase
 */
public class DisplayValve
    extends AbstractValve
{
    private String display;
    
    public void invoke( RunData data )
        throws IOException, SummitException
    {
        try
        {
            Display display = (Display) data.lookup( Display.ROLE, this.display );

            display.render( data );
        }
        catch ( Exception e )
        {
            throw new SummitException( "Can't display!", e );
        }
    }
}
