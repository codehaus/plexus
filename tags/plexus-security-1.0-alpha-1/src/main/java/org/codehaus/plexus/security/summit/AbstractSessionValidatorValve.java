package org.codehaus.plexus.security.summit;

import java.io.IOException;

import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.pipeline.valve.ValveInvocationException;
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
public abstract class AbstractSessionValidatorValve 
    extends AbstractValve
{
    /**
     * @see org.codehaus.plexus.summit.pipeline.valve.AbstractValve#invoke(org.codehaus.plexus.summit.rundata.RunData, org.codehaus.plexus.summit.pipeline.valve.ValveContext)
     */
    public void invoke(RunData data)
        throws IOException, ValveInvocationException
    {
        SecureRunData secData = (SecureRunData) data;

        if ( ( secData.getUser() == null ||
             !secData.getUser().isLoggedIn() ) && !isAllowedGuest() )
        {
            data.setTarget( getLoginTarget() );
        }
    }

    public String getLoginTarget()
    {
        return "Login.vm";
    }

    protected abstract boolean isAllowedGuest();
}
