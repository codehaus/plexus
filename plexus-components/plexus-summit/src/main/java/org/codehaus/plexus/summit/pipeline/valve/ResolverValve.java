package org.codehaus.plexus.summit.pipeline.valve;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.resolver.Resolution;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

import java.io.IOException;

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
            e.printStackTrace();
        }
    }
}
