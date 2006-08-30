package org.codehaus.plexus.security.authentication.memory;

import org.codehaus.plexus.security.authentication.AuthenticationDataSource;

/**
 * @author Jason van Zyl
 */
public class MemoryAuthenticationDataSource
    implements AuthenticationDataSource
{
    private String login;

    private String password;


    public MemoryAuthenticationDataSource( String login,
                                           String password )
    {
        this.login = login;
        this.password = password;
    }


    public String getLogin()
    {
        return login;
    }

    public String getPassword()
    {
        return password;
    }
}
