package org.codehaus.plexus.summit.pull;

import java.io.IOException;

import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.pipeline.valve.ValveInvocationException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * PullToolValve uses the PullService to populate the ViewContext.  After
 * populating the context it invokes the next Valve.  After the valve returns
 * the releases the tools from the context.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 7, 2003
 */
public class PullToolReleaseValve
    extends AbstractValve
{
    public void invoke( RunData data )
        throws IOException, ValveInvocationException

    {
        PullService pull = null;

        try
        {
            pull = (PullService) data.lookup( PullService.ROLE );
        }
        catch ( Exception e )
        {
            throw new ValveInvocationException( "Could not find the PullService!", e );
        }

        ViewContext viewContext = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        pull.releaseTools( viewContext );
    }
}
