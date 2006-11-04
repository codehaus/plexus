package org.codehaus.plexus.security;

/**
 * @author <a hrel="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class DefaultAuthentication
    implements Authentication
{
    private boolean authenticated;

    private User user;

    public void setAuthenticated( boolean isAuthenticated )
    {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated()
    {
        return authenticated;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }
}
