package org.codehaus.plexus.summit.pipeline.valve;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.resolver.Resolution;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

import java.io.IOException;

public class ResolverValve
    extends AbstractValve
{
    public void invoke( RunData data )
        throws IOException, SummitException
    {
        String target = data.getTarget();

        try
        {
            Resolver resolver = (Resolver) data.lookup( Resolver.ROLE, "new" );

            Resolution resolution = resolver.resolve( target );

            data.setResolution( resolution );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}
