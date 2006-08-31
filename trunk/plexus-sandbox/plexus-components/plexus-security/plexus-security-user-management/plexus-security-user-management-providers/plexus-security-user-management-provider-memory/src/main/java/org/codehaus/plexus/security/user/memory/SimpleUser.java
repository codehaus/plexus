package org.codehaus.plexus.security.user.memory;

import org.codehaus.plexus.security.user.User;

/**
 * @author Jason van Zyl
 */
public class SimpleUser
    implements User
{
    private String firstName;

    private String lastName;

    private String email;


    public SimpleUser( String firstName,
                       String lastName,
                       String email )
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getEmail()
    {
        return email;
    }
}
