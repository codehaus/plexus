package org.codehaus.plexus.util;

/*
 * Copyright 2007 The Codehaus Foundation.
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
import java.io.StringReader;

/**
 * Wraps a String as an InputStream. Note that data will be lost for
 * characters not in ISO Latin 1, as a simple char->byte mapping is assumed.
 *
 * @author <a href="mailto:umagesh@codehaus.org">Magesh Umasankar</a>
 */
public class StringInputStream
    extends InputStream
{
    /** Source string, stored as a StringReader */
    private StringReader in;

    /**
     * Composes a stream from a String
     *
     * @param source The string to read from. Must not be <code>null</code>.
     */
    public StringInputStream( String source )
    {
        in = new StringReader( source );
    }

    /**
     * Reads from the Stringreader, returning the same value. Note that
     * data will be lost for characters not in ISO Latin 1. Clients
     * assuming a return value in the range -1 to 255 may even fail on
     * such input.
     *
     * @return the value of the next character in the StringReader
     *
     * @exception IOException if the original StringReader fails to be read
     */
    public int read() throws IOException
    {
        return in.read();
    }

    /**
     * Closes the Stringreader.
     *
     * @exception IOException if the original StringReader fails to be closed
     */
    public void close() throws IOException
    {
        in.close();
    }

    /**
     * Marks the read limit of the StringReader.
     *
     * @param limit the maximum limit of bytes that can be read before the
     *              mark position becomes invalid
     */
    public synchronized void mark( final int limit )
    {
        try
        {
            in.mark( limit );
        }
        catch ( IOException ioe )
        {
            throw new RuntimeException( ioe.getMessage() );
        }
    }

    /**
     * Resets the StringReader.
     *
     * @exception IOException if the StringReader fails to be reset
     */
    public synchronized void reset() throws IOException
    {
        in.reset();
    }

    /**
     * @see InputStream#markSupported
     */
    public boolean markSupported()
    {
        return in.markSupported();
    }
}

