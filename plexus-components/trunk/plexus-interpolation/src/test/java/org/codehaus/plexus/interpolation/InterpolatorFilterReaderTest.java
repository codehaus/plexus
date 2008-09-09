package org.codehaus.plexus.interpolation;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * InterpolatorFilterReaderTest, heavily based on InterpolationFilterReaderTest. Heh,
 * even the test strings remained the same!
 *
 * @author cstamas
 *
 */
public class InterpolatorFilterReaderTest
    extends TestCase
{
    /*
     * Added and commented by jdcasey@03-Feb-2005 because it is a bug in the
     * InterpolationFilterReader.
     * kenneyw@15-04-2005 fixed the bug.
     */
    public void testShouldNotInterpolateExpressionAtEndOfDataWithInvalidEndToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "TestValue" );

        String testStr = "This is a ${test";

        assertEquals( "This is a ${test", interpolate( testStr, m ) );
    }

    /*
     * kenneyw@14-04-2005 Added test to check above fix.
     */
    public void testShouldNotInterpolateExpressionWithMissingEndToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "TestValue" );

        String testStr = "This is a ${test, really";

        assertEquals( "This is a ${test, really", interpolate( testStr, m ) );
    }

    public void testShouldNotInterpolateWithMalformedStartToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "testValue" );

        String foo = "This is a $!test} again";

        assertEquals( "This is a $!test} again", interpolate( foo, m ) );
    }

    public void testShouldNotInterpolateWithMalformedEndToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "test", "testValue" );

        String foo = "This is a ${test!} again";

        assertEquals( "This is a ${test!} again", interpolate( foo, m ) );
    }

    public void testDefaultInterpolationWithNonInterpolatedValueAtEnd()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "${name} is an ${noun}. ${not.interpolated}";

        assertEquals( "jason is an asshole. ${not.interpolated}", interpolate( foo, m ) );
    }

    public void testDefaultInterpolationWithInterpolatedValueAtEnd()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "${name} is an ${noun}";

        assertEquals( "jason is an asshole", interpolate( foo, m ) );
    }

    public void testInterpolationWithInterpolatedValueAtEndWithCustomToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "@{name} is an @{noun}";

        assertEquals( "jason is an asshole", interpolate( foo, m, "@{", "}", "@{", "}" ) );
    }

    public void testInterpolationWithInterpolatedValueAtEndWithCustomTokenAndCustomString()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "@name@ is an @noun@";

        assertEquals( "jason is an asshole", interpolate( foo, m, "@", "@", "@", "@" ) );
    }

    public void testEscape()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "${name} is an \\${noun}";

        assertEquals( "jason is an ${noun}", interpolate( foo, m, "\\" ) );
    }

    public void testEscapeAtStart()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "\\${name} is an \\${noun}";

        assertEquals( "${name} is an ${noun}", interpolate( foo, m, "\\" ) );
    }    
    
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String interpolate( String input, Map context )
        throws Exception
    {
        return interpolate( input, context, null );
    }

    private String interpolate( String input, Map context, String escapeStr )
        throws Exception
    {
        Interpolator interpolator = new StringSearchInterpolator();

        interpolator.addValueSource( new MapBasedValueSource( context ) );

        InterpolatorFilterReader r = new InterpolatorFilterReader( new StringReader( input ), interpolator );
        r.setInterpolateWithPrefixPattern( false );
        if ( escapeStr != null )
        {
            r.setEscapeString( escapeStr );
        }
        StringBuffer buf = new StringBuffer();
        int read = -1;
        char[] cbuf = new char[1024];
        while ( ( read = r.read( cbuf ) ) > -1 )
        {
            buf.append( cbuf, 0, read );
        }

        return buf.toString();
    }

    private String interpolate( String input, Map context, String beginToken, String endToken, String startRegExp,
                                String endRegExp )
        throws Exception
    {
        StringSearchInterpolator interpolator = new StringSearchInterpolator( startRegExp, endRegExp );

        interpolator.addValueSource( new MapBasedValueSource( context ) );

        InterpolatorFilterReader r = new InterpolatorFilterReader( new StringReader( input ), interpolator, beginToken,
                                                                   endToken );
        r.setInterpolateWithPrefixPattern( false );
        r.setEscapeString( "\\" );
        StringBuffer buf = new StringBuffer();
        int read = -1;
        char[] cbuf = new char[1024];
        while ( ( read = r.read( cbuf ) ) > -1 )
        {
            buf.append( cbuf, 0, read );
        }

        return buf.toString();
    }    
    
}
