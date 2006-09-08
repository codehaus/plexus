package org.codehaus.plexus.security.authentication;

/**
 * @author Jason van Zyl
 *
 * todo which this back to an interface and use the mojo style expression evaluation to populate the particular required fields
 */
public class AuthenticationDataSource
{
    public String ROLE = AuthenticationDataSource.class.getName();

    private String username;

    private String password;


    public AuthenticationDataSource( String login, String password )
    {
        this.username = login;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }
}
