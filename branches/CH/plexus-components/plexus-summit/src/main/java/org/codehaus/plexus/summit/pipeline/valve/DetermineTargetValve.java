package org.codehaus.plexus.summit.pipeline.valve;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

import java.io.IOException;

public class DetermineTargetValve
    extends AbstractValve
{
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
                target = getResolver( data ).getDefaultView();

                data.setTarget( target );
            }
        }
    }

    private Resolver getResolver( RunData data )
        throws SummitException
    {
        try
        {
            return (Resolver) data.lookup( Resolver.ROLE, "new" );
        }
        catch ( Exception e )
        {
            throw new SummitException( "Could not get a Resolver", e );
        }
    }
}
