package org.codehaus.plexus.security.summit;

import java.io.IOException;
import java.util.Map;

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.action.ActionNotFoundException;
import org.codehaus.plexus.action.web.ActionValve;
import org.codehaus.plexus.security.ResourceController;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Mar 1, 2003
 */
public class SecureActionValve
    extends ActionValve
{
    private String notAuthorizedPage = "NotAuthroized.vm";
    private ResourceController controller;
    private ActionManager actionManager;
    
    public void invoke( RunData data )
        throws IOException, SummitException
    {
        SecureRunData sdata = (SecureRunData) data;
        String actionId = data.getParameters().getString( "action", "" ).trim();
    
        if ( !actionId.equals("") )
        {
            Action action = null;
    
            try
            {
                action = actionManager.lookup( actionId );
            }
            catch ( ActionNotFoundException e )
            {
                getLogger().error( "Cannot find action with the id of " + actionId, e );
                return;
            }
    
            if (controller.isAuthorized(sdata.getUser(), actionId))
            {
                try
                {
                    Map m = createContext(data);
        
                    m.put( "data", data );
        
                    action.execute( m );
                }
                catch ( Exception e )
                {
                    getLogger().error( e.getMessage(), e );
                }
            }
            else
            {
                data.setTarget(notAuthorizedPage);
            }
            
        }
    }
}
