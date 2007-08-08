package org.codehaus.plexus.redback.password;

public interface PasswordManager
{

    boolean checkPassword( String rawPasswordInfo, String input )
        throws UnsupportedAlgorithmException, PasswordManagerException;

    String encodePassword( String hashType, String unencodedPassword, Object salt )
        throws UnsupportedAlgorithmException, PasswordManagerException;

    String encodePasswordUsingDefaultHash( String unencodedPassword, Object salt )
        throws UnsupportedAlgorithmException, PasswordManagerException;

}
