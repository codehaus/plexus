package org.codehaus.plexus.digest;

/*
 * Copyright 2001-2006 The Codehaus.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse files from checksum file formats.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class DigestUtils
{
    private DigestUtils()
    {
        // don't create this class
    }
    
    /**
     * Take a raw checksum string and verify that it matches the expectedFilename and digester, then
     * return the trimmed checksum string.
     * 
     * @param rawChecksum the raw checksum string that may include the filename.
     * @param digester the expected digester for this checksum string.
     * @return the trimmed checksum string (no filename portion)
     * @throws DigesterException if there was a problem verifying the checksum string.
     */
    public static String cleanChecksum( String rawChecksum, Digester digester, String expectedFilename ) throws DigesterException
    {
        return cleanChecksum( rawChecksum, digester.getAlgorithm(), expectedFilename );
    }

    public static String cleanChecksum( String checksum, String algorithm, String path )
        throws DigesterException
    {
        String trimmedChecksum = checksum.replace( '\n', ' ' ).trim();

        // Free-BSD / openssl
        String regex = algorithm.replaceAll( "-", "" ) + "\\s*\\((.*?)\\)\\s*=\\s*([a-fA-F0-9]+)";
        Matcher m = Pattern.compile( regex ).matcher( trimmedChecksum );
        if ( m.matches() )
        {
            String filename = m.group( 1 );
            if ( !isValidChecksumPattern( filename, path ) )
            {
                throw new DigesterException( "Supplied checksum does not match checksum pattern" );
            }
            trimmedChecksum = m.group( 2 );
        }
        else
        {
            // GNU tools
            m = Pattern.compile( "([a-fA-F0-9]+)\\s+\\*?(.+)" ).matcher( trimmedChecksum );
            if ( m.matches() )
            {
                String filename = m.group( 2 );
                if ( !isValidChecksumPattern( filename, path ) )
                {
                    throw new DigesterException( "Supplied checksum does not match checksum pattern" );
                }
                trimmedChecksum = m.group( 1 );
            }
        }
        return trimmedChecksum;
    }

    private static boolean isValidChecksumPattern( String filename, String path )
    {
        return filename.endsWith( path ) || ( "-".equals( filename ) );
    }
}
