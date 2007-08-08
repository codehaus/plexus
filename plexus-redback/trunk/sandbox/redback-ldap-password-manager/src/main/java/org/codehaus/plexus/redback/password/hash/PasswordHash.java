package org.codehaus.plexus.redback.password.hash;

public interface PasswordHash
{

    public String encodePassword( String password, Object salt )
        throws PasswordHashException;

    public boolean checkPassword( String encPassword, String input, Object salt )
        throws PasswordHashException;

}
