package org.codehaus.jasf.summit;

import java.io.IOException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.pipeline.valve.ValveContext;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * <p>
 * Validates that there is a user in the session.  If there isn't one an
 * anonymous user is retrieved from the User Authenticator.
 * </p><p>
 * This must be placed before the resolver valve.
 * </p>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 28, 2003
 * 
 * @todo How do I cancel execution of an action?
 */
public class SessionValidatorValve 
    extends AbstractValve
    implements Configurable
{
    private String LOGIN_TARGET_DFEAULT = "Login.vm";

    private String LOGIN_TARGET_KEY = "login-target";

    private String loginTarget;
    
    /**
     * @see org.codehaus.plexus.summit.pipeline.valve.AbstractValve#invoke(org.codehaus.plexus.summit.rundata.RunData, org.codehaus.plexus.summit.pipeline.valve.ValveContext)
     */
    public void invoke(RunData data, ValveContext context)
        throws IOException, SummitException
    {
        SecureRunData secData = (SecureRunData) data;
        
        getLogger().debug( "Validating session." );
        
        if ( secData.getUser() == null ||
             !secData.getUser().isLoggedIn() )
        {
            data.setTarget( loginTarget );
            
            getLogger().debug( "Invalid session." );
            //data.setAction(null);
        }
        
        context.invokeNext( data );
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {
        loginTarget = configuration.getAttribute( LOGIN_TARGET_KEY, 
                                                  LOGIN_TARGET_DFEAULT );
    }
}
