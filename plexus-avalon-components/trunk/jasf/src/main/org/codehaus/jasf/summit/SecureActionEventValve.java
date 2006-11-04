package org.codehaus.jasf.summit;

import java.io.IOException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.jasf.exception.UnauthorizedException;
import org.codehaus.jasf.summit.activity.SecureActionEvent;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Executes an action after checking to see that the user has the neccessary
 * credentials using the ResourceController for the ClassResource.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 1, 2003
 */
public class SecureActionEventValve
    extends AbstractValve
{
    private String notAuthorizedPage = "NotAuthroized.vm";

    private ServiceManager manager;

    /**
     * @see org.codehaus.plexus.summit.pipeline.valve.Valve#invoke(org.codehaus.plexus.summit.rundata.RunData, org.codehaus.plexus.summit.pipeline.valve.ValveContext)
     */
    public void invoke(RunData data)
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
    }
}
