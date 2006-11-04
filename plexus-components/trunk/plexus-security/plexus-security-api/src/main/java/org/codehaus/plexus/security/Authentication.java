package org.codehaus.plexus.security;

import java.io.Serializable;

/**
 * @author <a hrel="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public interface Authentication
    extends Serializable
{
    void setAuthenticated( boolean isAuthenticated );

    boolean isAuthenticated();

    User getUser();

    void setUser( User user );
}
