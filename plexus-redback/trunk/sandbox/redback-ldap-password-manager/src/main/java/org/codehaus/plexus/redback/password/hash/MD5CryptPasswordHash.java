package org.codehaus.plexus.redback.password.hash;

import org.codehaus.plexus.redback.password.hash.alg.MD5Crypt;

public class MD5CryptPasswordHash
    implements PasswordHash
{

    public String encodePassword( String password, Object salt )
        throws PasswordHashException
    {
        return MD5Crypt.unixMD5( password );
    }

    public boolean checkPassword( String encPassword, String input, Object salt )
        throws PasswordHashException
    {
        String encryptedPassword = encPassword;
//        if ( encryptedPassword.startsWith( "{crypt}" ) || encryptedPassword.startsWith( "{CRYPT}" ) )
//        {
//            encryptedPassword = encryptedPassword.substring( "{crypt}".length() );
//        }
//
//        int lastDollar = encryptedPassword.lastIndexOf( '$' );
//        String realSalt = encryptedPassword.substring( "$1$".length(), lastDollar );

        String check = MD5Crypt.unixMD5( input, salt.toString() );

        return check.equals( encryptedPassword );
    }

}
