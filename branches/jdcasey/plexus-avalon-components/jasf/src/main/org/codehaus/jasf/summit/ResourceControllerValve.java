package org.codehaus.jasf.summit;

import java.io.IOException;

import org.apache.avalon.framework.service.ServiceSelector;
import org.codehaus.jasf.ResourceController;
import org.codehaus.jasf.resources.PageResource;
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
public class ResourceControllerValve
    extends AbstractValve
{

    private String notAuthorizedPage = "NotAuthroized.vm";

    /**
     * @see org.codehaus.plexus.summit.pipeline.valve.Valve#invoke(org.codehaus.plexus.summit.rundata.RunData, org.codehaus.plexus.summit.pipeline.valve.ValveContext)
     */
    public void invoke(RunData data)
        throws IOException, SummitException
    {
        SecureRunData secData = (SecureRunData) data;

        try
        {
            ServiceSelector security = 
                ( ServiceSelector ) data.getServiceManager().lookup( ResourceController.SELECTOR_ROLE );
            
            ResourceController controller = 
                (ResourceController) security.select( PageResource.RESOURCE_TYPE );
            
            if ( !controller.isAuthorized( 
                     secData.getUser(), 
                     new PageResource( data.getTarget() )))
            {
                data.setTarget( notAuthorizedPage );
            }
            
            data.getServiceManager().release( controller );
        }
        catch (Exception e)
        {
            throw new SummitException( 
                "Could not find the SecurityService!", e );
        }
    }
}
