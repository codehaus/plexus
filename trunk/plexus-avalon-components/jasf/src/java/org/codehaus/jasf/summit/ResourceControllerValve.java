package org.codehaus.jasf.summit;

import java.io.IOException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.pipeline.valve.ValveContext;
import org.codehaus.plexus.summit.rundata.RunData;

import org.codehaus.jasf.ResourceController;
import org.codehaus.jasf.resources.PageResource;

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
    implements Serviceable, Configurable
{
    private String NOT_AUTHORIZED_PAGE_DEFAULT = "NotAuthroized.vm";

    private String NOT_AUTHORIZED_PAGE_KEY = "not-authorized-page";

    private String notAuthorizedPage;

    private ServiceManager manager;

    /**
     * @see org.codehaus.plexus.summit.pipeline.valve.Valve#invoke(org.codehaus.plexus.summit.rundata.RunData, org.codehaus.plexus.summit.pipeline.valve.ValveContext)
     */
    public void invoke(RunData data, ValveContext context)
        throws IOException, SummitException
    {
        SecureRunData secData = (SecureRunData) data;

        try
        {
            ServiceSelector security = 
                ( ServiceSelector ) manager.lookup( ResourceController.SELECTOR_ROLE );
            
            ResourceController controller = 
                (ResourceController) security.select( PageResource.RESOURCE_TYPE );
            
            if ( !controller.isAuthorized( 
                     secData.getUser(), 
                     new PageResource( data.getTarget() )))
            {
                data.setTarget( notAuthorizedPage );
            }
            
            manager.release( controller );
        }
        catch (ServiceException e)
        {
            throw new SummitException( 
                "Could not find the SecurityService!", e );
        }
        
        context.invokeNext( data );
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager)
        throws ServiceException
    {
        this.manager = manager;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) 
        throws ConfigurationException
    {
        notAuthorizedPage = 
            configuration.getAttribute( NOT_AUTHORIZED_PAGE_KEY,
                                        NOT_AUTHORIZED_PAGE_DEFAULT );
    }
}
