package org.codehaus.bacon.session;

import java.security.SecureRandom;

public final class SessionKeyFactory
{
    public static final int DEFAULT_SESSION_KEY_SIZE = 16;

    private static final String VALID_SESSION_ID_CHARS = "0123456789ABCDEF";

    private static SecureRandom secureRandom = new SecureRandom();

    private SessionKeyFactory()
    {
    }

    public static SessionKey newSessionKey()
    {
        return newSessionKey( DEFAULT_SESSION_KEY_SIZE );
    }

    public static SessionKey newSessionKey( int keySize )
    {
        StringBuffer buffer = new StringBuffer();

        for ( int i = 0; i < keySize; i++ )
        {
            int nextIdx = secureRandom.nextInt() % VALID_SESSION_ID_CHARS.length();

            buffer.append( VALID_SESSION_ID_CHARS.charAt( nextIdx ) );
        }

        return new SessionKey( buffer.toString() );
    }

}
