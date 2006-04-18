package org.codehaus.plexus.security;

/**
 * @author <a hrel="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public interface User
{
    String getUsername();

    String getPassword();

    String getFullName();

    String getEmail();

    boolean isEnabled();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isPasswordNonExpired();

    Object getDetails();
}
