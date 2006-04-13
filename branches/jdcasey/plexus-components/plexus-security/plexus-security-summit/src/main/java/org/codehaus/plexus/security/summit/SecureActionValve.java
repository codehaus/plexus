package org.codehaus.plexus.security.summit;

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.action.ActionNotFoundException;
import org.codehaus.plexus.security.ResourceController;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.pipeline.valve.ValveInvocationException;
import org.codehaus.plexus.summit.rundata.RunData;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Mar 1, 2003
 */
public class SecureActionValve
    extends AbstractValve
{
    private String notAuthorizedPage = "NotAuthroized.vm";

    private ResourceController controller;

    private ActionManager actionManager;

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
    {
        SecureRunData sdata = (SecureRunData) data;
        String actionId = data.getParameters().getString( "action", "" ).trim();

        if ( !actionId.equals( "" ) )
        {
            Action action;

            try
            {
                action = actionManager.lookup( actionId );
            }
            catch ( ActionNotFoundException e )
            {
                getLogger().error( "Cannot find action with the id of " + actionId, e );
                return;
            }

            if ( controller.isAuthorized( sdata.getUser(), actionId ) )
            {
                try
                {
                    Map m = createContext( data );

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
                data.setTarget( notAuthorizedPage );
            }

        }
    }

    protected Map createContext( RunData data )
    {
        // The parameter map in the request consists of an array of values for
        // the given key so this is why this is being done.
        Map m = new HashMap();

        for ( Enumeration e = data.getRequest().getParameterNames(); e.hasMoreElements(); )
        {
            String key = (String) e.nextElement();

            m.put( key, data.getRequest().getParameter( key ) );
        }
        return m;
    }
}
