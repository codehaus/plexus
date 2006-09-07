package org.codehaus.plexus.security.authentication.memory;

/**
 * @author Jason van Zyl
 */
public class MemoryAuthenticationDataSource
//    implements AuthenticationDataSource
{
    private String login;

    private String password;


    public MemoryAuthenticationDataSource( String login,
                                           String password )
    {
        this.login = login;
        this.password = password;
    }


    public String getUsername()
    {
        return login;
    }

    public String getPassword()
    {
        return password;
    }
}
