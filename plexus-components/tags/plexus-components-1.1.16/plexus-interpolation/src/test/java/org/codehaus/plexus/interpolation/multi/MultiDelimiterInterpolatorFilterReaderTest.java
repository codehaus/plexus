package org.codehaus.plexus.interpolation.multi;

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

import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * InterpolatorFilterReaderTest, heavily based on InterpolationFilterReaderTest. Heh, even the test strings remained the
 * same!
 * 
 * @author cstamas
 * 
 */
public class MultiDelimiterInterpolatorFilterReaderTest
    extends TestCase
{
    /*
     * Added and commented by jdcasey@03-Feb-2005 because it is a bug in the InterpolationFilterReader.
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

        assertEquals( "jason is an asshole", interpolate( foo, m, "@{", "}" ) );
    }

    public void testInterpolationWithInterpolatedValueAtEndWithCustomTokenAndCustomString()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "@name@ is an @noun@";

        assertEquals( "jason is an asshole", interpolate( foo, m, "@", "@" ) );
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

    public void testEscapeOnlyAtStart()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "\\@name@ is an @noun@";

        String result = interpolate( foo, m, "@", "@" );
        assertEquals( "@name@ is an asshole", result );
    }

    public void testEscapeOnlyAtStartDefaultToken()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "noun", "asshole" );

        String foo = "\\${name} is an ${noun}";

        String result = interpolate( foo, m, "${", "}" );
        assertEquals( "${name} is an asshole", result );
    }

    public void testShouldDetectRecursiveExpressionPassingThroughTwoPrefixes()
        throws Exception
    {
        List prefixes = new ArrayList();

        prefixes.add( "prefix1" );
        prefixes.add( "prefix2" );

        RecursionInterceptor ri = new PrefixAwareRecursionInterceptor( prefixes, false );

        Map context = new HashMap();
        context.put( "name", "${prefix2.name}" );

        String input = "${prefix1.name}";

        StringSearchInterpolator interpolator = new StringSearchInterpolator();

        interpolator.addValueSource( new MapBasedValueSource( context ) );

        MultiDelimiterInterpolatorFilterReader r = new MultiDelimiterInterpolatorFilterReader( new StringReader( input ),
                                                                                               interpolator, ri );
        r.setInterpolateWithPrefixPattern( false );
        r.setEscapeString( "\\" );
        StringBuffer buf = new StringBuffer();
        int read = -1;
        char[] cbuf = new char[1024];
        while ( ( read = r.read( cbuf ) ) > -1 )
        {
            buf.append( cbuf, 0, read );
        }

        assertEquals( input, buf.toString() );
    }

    public void testShouldDetectRecursiveExpressionWithPrefixAndWithout()
        throws Exception
    {
        List prefixes = new ArrayList();

        prefixes.add( "prefix1" );

        RecursionInterceptor ri = new PrefixAwareRecursionInterceptor( prefixes, false );

        Map context = new HashMap();
        context.put( "name", "${prefix1.name}" );

        String input = "${name}";

        StringSearchInterpolator interpolator = new StringSearchInterpolator();

        interpolator.addValueSource( new MapBasedValueSource( context ) );

        MultiDelimiterInterpolatorFilterReader r = new MultiDelimiterInterpolatorFilterReader( new StringReader( input ),
                                                                                               interpolator, ri );
        r.setInterpolateWithPrefixPattern( false );
        r.setEscapeString( "\\" );
        StringBuffer buf = new StringBuffer();
        int read = -1;
        char[] cbuf = new char[1024];
        while ( ( read = r.read( cbuf ) ) > -1 )
        {
            buf.append( cbuf, 0, read );
        }

        assertEquals( "${prefix1.name}", buf.toString() );
    }

    public void testInterpolationWithMultipleTokenTypes()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "otherName", "@name@" );

        String foo = "${otherName}";

        assertEquals( "jason", interpolateMulti( foo, m, new String[] { "${*}", "@*@" } ) );
    }

    public void testInterpolationWithMultipleTokenTypes_ReversedOrdering()
        throws Exception
    {
        Map m = new HashMap();
        m.put( "name", "jason" );
        m.put( "otherName", "${name}" );

        String foo = "@otherName@";

        assertEquals( "jason", interpolateMulti( foo, m, new String[] { "${*}", "@*@" } ) );
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

        MultiDelimiterInterpolatorFilterReader r = new MultiDelimiterInterpolatorFilterReader( new StringReader( input ), interpolator );
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

    private String interpolate( String input, Map context, String beginToken, String endToken )
        throws Exception
    {
        StringSearchInterpolator interpolator = new StringSearchInterpolator( beginToken, endToken );

        interpolator.addValueSource( new MapBasedValueSource( context ) );

        MultiDelimiterInterpolatorFilterReader r = new MultiDelimiterInterpolatorFilterReader( new StringReader( input ), interpolator );
        r.addDelimiterSpec( beginToken + "*" + endToken );

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

    private String interpolateMulti( String input, Map context, String[] specs )
        throws Exception
    {
        MultiDelimiterStringSearchInterpolator interp = new MultiDelimiterStringSearchInterpolator();
        interp.addValueSource( new MapBasedValueSource( context ) );

        MultiDelimiterInterpolatorFilterReader r = new MultiDelimiterInterpolatorFilterReader( new StringReader( input ), interp );

        for ( int i = 0; i < specs.length; i++ )
        {
            interp.addDelimiterSpec( specs[i] );
            r.addDelimiterSpec( specs[i] );
        }

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
