package org.codehaus.plexus.security.summit;

import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.pipeline.valve.ValveInvocationException;
import org.codehaus.plexus.summit.rundata.RunData;

import java.io.IOException;

/**
 * <p/>
 * Validates that there is a user in the session.  If there isn't one an
 * anonymous user is retrieved from the User Authenticator.
 * </p><p>
 * This must be placed before the resolver valve.
 * </p>
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @todo How do I cancel execution of an action?
 * @since Feb 28, 2003
 */
public abstract class AbstractSessionValidatorValve
    extends AbstractValve
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private String loginTarget = "Login.vm";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
    {
        SecureRunData secData = (SecureRunData) data;

        if ( ( secData.getUser() == null || !secData.getUser().isLoggedIn() )  &&
             !isAllowedGuest() )
        {
            data.setTarget( getLoginTarget() );
        }
    }

    public String getLoginTarget()
    {
        return loginTarget;
    }

    protected abstract boolean isAllowedGuest();
}
