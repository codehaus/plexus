package org.codehaus.plexus.security.summit;

import java.io.IOException;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.security.ResourceController;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Checks to see if the user is authorized to access the specified target.
 * If not, it redirects the user to a page telling them they aren't
 * authorized.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 28, 2003
 */
public class PageControllerValve
    extends AbstractValve
{
    private String notAuthorizedPage = "NotAuthroized.vm";
    private ResourceController controller;
    
    /**
     * @see org.codehaus.plexus.summit.pipeline.valve.Valve#invoke(org.codehaus.plexus.summit.rundata.RunData, org.codehaus.plexus.summit.pipeline.valve.ValveContext)
     */
    public void invoke(RunData data)
        throws IOException, SummitException
    {
        SecureRunData secData = (SecureRunData) data;

        if ( !controller.isAuthorized(secData.getUser(), 
                                      data.getTarget()) )
        {
            data.setTarget(notAuthorizedPage);
        }
    }
}
