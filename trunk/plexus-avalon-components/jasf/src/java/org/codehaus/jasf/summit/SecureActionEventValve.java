package org.codehaus.jasf.summit;

import java.io.IOException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.pipeline.valve.ValveContext;
import org.codehaus.plexus.summit.rundata.RunData;

import org.codehaus.jasf.exception.UnauthorizedException;
import org.codehaus.jasf.summit.activity.SecureActionEvent;

/**
 * Executes an action after checking to see that the user has the neccessary
 * credentials using the ResourceController for the ClassResource.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 1, 2003
 */
public class SecureActionEventValve
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
        try
        {
            SecureActionEvent actionEvent = 
                ( SecureActionEvent ) manager.lookup( SecureActionEvent.ROLE );
            
            try
            {
                actionEvent.perform( data );
            }
            catch ( UnauthorizedException e )
            {
                data.setTarget( notAuthorizedPage );
            }
            catch ( Exception e )
            {
                throw new SummitException(
                    "Could not execute action!", e );
            }
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
