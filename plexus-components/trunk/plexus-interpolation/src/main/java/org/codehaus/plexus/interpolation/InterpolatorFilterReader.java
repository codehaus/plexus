/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 */

package org.codehaus.plexus.interpolation;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A FilterReader implementation, that works with Interpolator interface instead of it's own interpolation
 * implementation. This implementation is heavily based on org.codehaus.plexus.util.InterpolationFilterReader.
 *
 * @author cstamas
 * @version $Id$
 */
public class InterpolatorFilterReader
    extends FilterReader
{

    /** Interpolator used to interpolate */
    private Interpolator interpolator;

    /** replacement text from a token */
    private String replaceData = null;

    /** Index into replacement data */
    private int replaceIndex = -1;

    /** Index into previous data */
    private int previousIndex = -1;

    /** Default begin token. */
    public static final String DEFAULT_BEGIN_TOKEN = "${";

    /** Default end token. */
    public static final String DEFAULT_END_TOKEN = "}";
    
    private String beginToken;
    
    private String orginalBeginToken;
    
    private String endToken;
    
    /** true by default to preserve backward comp */
    private boolean interpolateWithPrefixPattern = true;

    private String escapeString;
    
    private boolean useEscape = false;
    
    /**
     * this constructor use default begin token ${ and default end token } 
     * @param in reader to use
     * @param interpolator interpolator instance to use
     */
    public InterpolatorFilterReader( Reader in, Interpolator interpolator )
    {
        this( in, interpolator, DEFAULT_BEGIN_TOKEN, DEFAULT_END_TOKEN );
    }
    
    /**
     * @param in reader to use
     * @param interpolator interpolator instance to use
     * @param beginToken start token to use
     * @param endToken end token to use
     */
    public InterpolatorFilterReader( Reader in, Interpolator interpolator, String beginToken, String endToken )
    {
        super( in );

        this.interpolator = interpolator;
        
        this.beginToken = beginToken;
        
        this.endToken = endToken;
        
        this.orginalBeginToken = this.beginToken;
    }    

    /**
     * Skips characters. This method will block until some characters are available, an I/O error occurs, or the end of
     * the stream is reached.
     *
     * @param n The number of characters to skip
     * @return the number of characters actually skipped
     * @exception IllegalArgumentException If <code>n</code> is negative.
     * @exception IOException If an I/O error occurs
     */
    public long skip( long n )
        throws IOException
    {
        if ( n < 0L )
        {
            throw new IllegalArgumentException( "skip value is negative" );
        }

        for ( long i = 0; i < n; i++ )
        {
            if ( read() == -1 )
            {
                return i;
            }
        }
        return n;
    }

    /**
     * Reads characters into a portion of an array. This method will block until some input is available, an I/O error
     * occurs, or the end of the stream is reached.
     *
     * @param cbuf Destination buffer to write characters to. Must not be <code>null</code>.
     * @param off Offset at which to start storing characters.
     * @param len Maximum number of characters to read.
     * @return the number of characters read, or -1 if the end of the stream has been reached
     * @exception IOException If an I/O error occurs
     */
    public int read( char cbuf[], int off, int len )
        throws IOException
    {
        for ( int i = 0; i < len; i++ )
        {
            int ch = read();
            if ( ch == -1 )
            {
                if ( i == 0 )
                {
                    return -1;
                }
                else
                {
                    return i;
                }
            }
            cbuf[off + i] = (char) ch;
        }
        return len;
    }

    /**
     * Returns the next character in the filtered stream, replacing tokens from the original stream.
     *
     * @return the next character in the resulting stream, or -1 if the end of the resulting stream has been reached
     * @exception IOException if the underlying stream throws an IOException during reading
     */
    public int read()
        throws IOException
    {
        if ( replaceIndex != -1 && replaceIndex < replaceData.length() )
        {
            int ch = replaceData.charAt( replaceIndex++ );
            if ( replaceIndex >= replaceData.length() )
            {
                replaceIndex = -1;
            }
            return ch;
        }

        int ch = -1;
        if ( previousIndex != -1 && previousIndex < this.endToken.length() )
        {
            ch = this.endToken.charAt( previousIndex++ );
        }
        else
        {
            ch = in.read();
        }
        
        if ( ch == this.beginToken.charAt( 0 ) || ( useEscape && ch == this.orginalBeginToken.charAt( 0 ) ) )
        {
            StringBuffer key = new StringBuffer( );

            key.append( (char) ch );

            int beginTokenMatchPos = 1;

            do
            {
                if ( previousIndex != -1 && previousIndex < this.endToken.length() )
                {
                    ch = this.endToken.charAt( previousIndex++ );
                }
                else
                {
                    ch = in.read();
                }
                if ( ch != -1 )
                {
                    key.append( (char) ch );
                    if ( ( beginTokenMatchPos < this.beginToken.length() )
                        && ( ch != this.beginToken.charAt( beginTokenMatchPos++ ) )
                        && ( useEscape && this.orginalBeginToken.length() > ( beginTokenMatchPos - 1 ) && ch != this.orginalBeginToken
                            .charAt( beginTokenMatchPos - 1 ) ) )
                    {
                        ch = -1; // not really EOF but to trigger code below
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            while ( ch != this.endToken.charAt( 0 ) );

            // now test endToken
            if ( ch != -1 && this.endToken.length() > 1 )
            {
                int endTokenMatchPos = 1;

                do
                {
                    if ( previousIndex != -1 && previousIndex < this.endToken.length() )
                    {
                        ch = this.endToken.charAt( previousIndex++ );
                    }
                    else
                    {
                        ch = in.read();
                    }

                    if ( ch != -1 )
                    {
                        key.append( (char) ch );

                        if ( ch != this.endToken.charAt( endTokenMatchPos++ ) )
                        {
                            ch = -1; // not really EOF but to trigger code below
                            break;
                        }

                    }
                    else
                    {
                        break;
                    }
                }
                while ( endTokenMatchPos < this.endToken.length() );
            }

            // There is nothing left to read so we have the situation where the begin/end token
            // are in fact the same and as there is nothing left to read we have got ourselves
            // end of a token boundary so let it pass through.
            if ( ch == -1 )
            {
                replaceData = key.toString();
                replaceIndex = 1;
                return replaceData.charAt( 0 );
            }

            String value = null;
            try
            {
                boolean escapeFound = false;
                if ( useEscape )
                {
                    if (key.toString().startsWith( escapeString + orginalBeginToken ))
                    {
                        String keyStr = key.toString();
                        value = keyStr.substring( escapeString.length(), keyStr.length() );
                        escapeFound = true;
                    }
                }
                if ( !escapeFound )
                {
                    if ( interpolateWithPrefixPattern )
                    {
                        value = interpolator.interpolate( key.toString(), "" );
                    }
                    else
                    {
                        value = interpolator.interpolate( key.toString() );
                    }
                }
            }
            catch ( InterpolationException e )
            {
                IllegalArgumentException error = new IllegalArgumentException( e.getMessage() );
                error.initCause( e );

                throw error;
            }

            if ( value != null )
            {
                if ( value.length() != 0 )
                {
                    replaceData = value;
                    replaceIndex = 0;
                }
                return read();
            }
            else
            {
                previousIndex = 0;
                replaceData = key.substring( 0, key.length() - this.endToken.length() );
                replaceIndex = 0;
                return this.beginToken.charAt( 0 );
            }
        }

        return ch;
    }

    public boolean isInterpolateWithPrefixPattern()
    {
        return interpolateWithPrefixPattern;
    }

    public void setInterpolateWithPrefixPattern( boolean interpolateWithPrefixPattern )
    {
        this.interpolateWithPrefixPattern = interpolateWithPrefixPattern;
    }
    public String getEscapeString()
    {
        return escapeString;
    }

    public void setEscapeString( String escapeString )
    {
        // TODO NPE if escapeString is null ?
        if ( escapeString != null && escapeString.length() >= 1 )
        {
            this.escapeString = escapeString;
            this.orginalBeginToken = beginToken;
            this.beginToken = escapeString + beginToken;
            this.useEscape = escapeString != null && escapeString.length() >= 1;
        }
    }
}
