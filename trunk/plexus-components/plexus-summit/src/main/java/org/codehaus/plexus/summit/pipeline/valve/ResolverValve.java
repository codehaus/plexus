package org.codehaus.plexus.summit.pipeline.valve;

import java.io.IOException;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.resolver.Resolution;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

public class ResolverValve
    extends AbstractValve
{
    private String resolver;

    public void invoke( RunData data )
        throws IOException, SummitException
    {
        String target = data.getTarget();

        try
        {
            Resolver res = (Resolver) data.lookup( Resolver.ROLE, resolver );

            Resolution resolution = res.resolve( target );

            data.setResolution( resolution );
        }
        catch ( Exception e )
        {
            if ( e instanceof SummitException )
            {
                throw (SummitException) e;
            }

            throw new SummitException( "Couldn't resolve target.", e );
        }
    }
}
