package org.codehaus.plexus.security.user.memory;

import org.codehaus.plexus.security.user.User;

/**
 * @author Jason van Zyl
 */
public class SimpleUser
    implements User
{
    private String username;

    private String password;

    private String email;

    public SimpleUser( String username,
                       String password,
                       String email )
    {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getEmail()
    {
        return email;
    }

    public Object getPrincipal()
    {
        return username;
    }
}
