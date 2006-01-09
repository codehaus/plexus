package org.codehaus.plexus.rememberme;

import org.codehaus.plexus.security.Authentication;
import org.codehaus.plexus.security.DefaultAuthentication;
import org.codehaus.plexus.security.User;

/**
 * @author <a hrel="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class RememberMeAuthentication
    implements Authentication
{
    private Authentication authentication;

    public RememberMeAuthentication( User user, boolean authenticated )
    {
        if ( authentication == null )
        {
            authentication = new DefaultAuthentication();
        }

        authentication.setUser( user );

        authentication.setAuthenticated( authenticated );
    }

    public void setAuthenticated( boolean authenticated )
    {
        authentication.setAuthenticated( authenticated );
    }

    public boolean isAuthenticated()
    {
        return authentication.isAuthenticated();
    }

    public User getUser()
    {
        return authentication.getUser();
    }

    public void setUser( User user )
    {
        authentication.setUser( user );
    }
}
