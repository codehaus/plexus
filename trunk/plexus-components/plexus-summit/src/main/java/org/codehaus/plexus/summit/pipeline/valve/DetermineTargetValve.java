package org.codehaus.plexus.summit.pipeline.valve;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

import java.io.IOException;

public class DetermineTargetValve
    extends AbstractValve
{
    private Resolver resolver;
    
    public void invoke( RunData data )
        throws IOException, SummitException
    {
        if ( !data.hasTarget() )
        {
            String target = data.getParameters().getString( "target" );

            if ( target != null )
            {
                data.setTarget( target );
            }
            else
            {
                target = resolver.getDefaultView();

                data.setTarget( target );
            }
        }
    }
}
