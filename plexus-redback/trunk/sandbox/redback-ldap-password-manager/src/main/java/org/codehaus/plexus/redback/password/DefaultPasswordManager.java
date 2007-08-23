package org.codehaus.plexus.redback.password;

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

import org.codehaus.plexus.collections.ActiveMap;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.redback.password.hash.PasswordHash;
import org.codehaus.plexus.redback.password.hash.PasswordHashException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author <a href="jesse@codehaus.org"> jesse
 * @version "$Id:$"
 *
 * @plexus.component role="org.codehaus.plexus.redback.password.PasswordManager" role-hint="default"
 */
public class DefaultPasswordManager
    implements PasswordManager, LogEnabled
{

    private static final Pattern ENCODING_SPEC_PATTERN = Pattern.compile( "\\{([a-zA-Z0-9]+)\\}(.+)" );

    /**
     * @plexus.configuration default-value="md5"
     */
    private String defaultHash;

    /**
     * @plexus.requirement role="org.codehaus.plexus.redback.password.hash.PasswordHash"
     */
    private ActiveMap passwordHashes;

    private Logger log;

    public boolean checkPassword( String rawPasswordInfo, String input )
        throws UnsupportedAlgorithmException, PasswordManagerException
    {
        String hashType = defaultHash;
        String encodedPassword = rawPasswordInfo;

        Matcher matcher = ENCODING_SPEC_PATTERN.matcher( rawPasswordInfo );

        if( matcher.matches() )
        {
            hashType = matcher.group( 1 );
            encodedPassword = matcher.group( 2 );
        }

        PasswordHash hash;
        try
        {
            hash = (PasswordHash) passwordHashes.checkedGet( hashType.toLowerCase() );
        }
        catch ( ComponentLookupException e )
        {
            throw new UnsupportedAlgorithmException( hashType + " has no associated PasswordHash component in the system.", e );
        }

        log.debug( "Verifying password with encoding: " + hashType + " (instance: " + hash + ")." );

        try
        {
            return hash.checkPassword( encodedPassword, input, null );
        }
        catch ( PasswordHashException e )
        {
            throw new PasswordManagerException( "Failed to check password using: " + hashType + " (instance: " + hash + ")", e );
        }
    }

    public String encodePassword( String hashType, String unencodedPassword, Object salt )
        throws UnsupportedAlgorithmException, PasswordManagerException
    {
        PasswordHash hash;
        try
        {
            hash = (PasswordHash) passwordHashes.checkedGet( hashType.toLowerCase() );
        }
        catch ( ComponentLookupException e )
        {
            throw new UnsupportedAlgorithmException( hashType + " has no associated PasswordHash component in the system.", e );
        }

        log.debug( "Encoding password using hash: " + hashType + " (instance: " + hash + ")." );

        try
        {
            return hash.encodePassword( unencodedPassword, salt );
        }
        catch ( PasswordHashException e )
        {
            throw new PasswordManagerException( "Failed to check password using: " + hashType + " (instance: " + hash + ")", e );
        }
    }

    public void enableLogging( Logger log )
    {
        this.log = log;
    }

    public String encodePasswordUsingDefaultHash( String unencodedPassword, Object salt )
        throws UnsupportedAlgorithmException, PasswordManagerException
    {
        return encodePassword( defaultHash, unencodedPassword, salt );
    }

}
