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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Gradually create a digest for a stream.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public abstract class AbstractStreamingDigester
    implements StreamingDigester
{
    protected final MessageDigest md;

    private static final int BUFFER_SIZE = 32768;

    protected AbstractStreamingDigester( String algorithm )
        throws NoSuchAlgorithmException
    {
        md = MessageDigest.getInstance( algorithm );
    }

    public String getAlgorithm()
    {
        return md.getAlgorithm();
    }

    public String calc()
        throws DigesterException
    {
        return calc( this.md );
    }

    public void reset()
        throws DigesterException
    {
        md.reset();
    }

    public void update( InputStream is )
        throws DigesterException
    {
        update( is, md );
    }

    protected static String calc( MessageDigest md )
    {
        return Hex.encode( md.digest() );
    }
    
    protected static void update( InputStream is, MessageDigest digest )
        throws DigesterException
    {
        try
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int size = is.read( buffer, 0, BUFFER_SIZE );
            while ( size >= 0 )
            {
                digest.update( buffer, 0, size );
                size = is.read( buffer, 0, BUFFER_SIZE );
            }
        }
        catch ( IOException e )
        {
            throw new DigesterException( "Unable to update " + digest.getAlgorithm() + " hash: " + e.getMessage(), e );
        }
    }
}
