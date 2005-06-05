package org.codehaus.plexus.summit.pipeline.valve;

import java.io.IOException;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.resolver.Resolution;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @plexus.component
 *  role-hint="org.codehaus.plexus.summit.pipeline.valve.ResolverValve"
 */
public class ResolverValve
    extends AbstractValve
{
    /**
     * @plexus.configuration
     *  default-value="new"
     */
    private String resolver;

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
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
            throw new ValveInvocationException( "Couldn't resolve target.", e );
        }
    }
}
