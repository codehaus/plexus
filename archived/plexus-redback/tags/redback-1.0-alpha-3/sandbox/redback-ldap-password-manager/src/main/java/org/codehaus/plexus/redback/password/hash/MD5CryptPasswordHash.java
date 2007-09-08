package org.codehaus.plexus.redback.password.hash;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.redback.password.hash.alg.MD5Crypt;

/**
 * 
 * @author <a href="jesse@codehaus.org"> jesse
 * @version "$Id:$"
 *
 * @plexus.component role="org.codehaus.plexus.redback.password.hash.PasswordHash" role-hint="md5"
 */
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
