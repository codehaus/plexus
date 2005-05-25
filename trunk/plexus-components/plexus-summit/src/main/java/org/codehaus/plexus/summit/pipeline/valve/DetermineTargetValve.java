package org.codehaus.plexus.summit.pipeline.valve;

import java.io.IOException;

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.resolver.Resolver;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @plexus.component
 *
 * @plexus.role org.codehaus.plexus.summit.pipeline.valve.Valve
 *
 * @plexus.role-hint org.codehaus.plexus.summit.pipeline.valve.DetermineTargetValve
 */
public class DetermineTargetValve
    extends AbstractValve
{
    /**
     * @plexus.requirement
     *
     * @plexus.role-hint new
     */
    private Resolver resolver;

    public void invoke( RunData data )
        throws IOException, SummitException
    {
        if ( !data.hasTarget() )
        {
            String target = data.getParameters().getString( "target" );

            if ( target == null )
            {
                target = data.getParameters().getString( "view" );
            }

            if ( target != null )
            {
                data.setTarget( target );
            }
            else
            {
                target = resolver.getInitialView();

                System.out.println( "setting target = " + target );

                data.setTarget( target );
            }

            System.out.println( "target = " + target );
        }
    }
}
