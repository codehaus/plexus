package org.codehaus.plexus.security;

import java.io.Serializable;

/**
 * @author <a hrel="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: Authentication.java 2990 2006-01-09 16:33:52Z evenisse $
 */
public interface Authentication
    extends Serializable
{

    public void setAuthenticated( boolean isAuthenticated );

    public boolean isAuthenticated();

    public User getUser();

    public void setUser( User user );
}
