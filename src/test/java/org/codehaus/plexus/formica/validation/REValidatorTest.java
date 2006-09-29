package org.codehaus.plexus.formica.validation;

/*
 * Copyright (c) 2004, Codehaus.org
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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class REValidatorTest
    extends TestCase
{
    REValidator validator;

    public REValidatorTest( final String s )
    {
        super( s );
    }

    public void setUp()
    {
        validator = new REValidator();
        validator.setPattern( "[0-9]+" );
        try
        {
            validator.initialize();
        }
        catch ( Exception e )
        {
            fail( "Validator initialization failed: " + e.getMessage() );
        }
    }

    public void test_Valid_Number_Pattern()
        throws Exception
    {


        final boolean result = validator.validate( "1234567890" );
        assertTrue( "this is a valid expression and should be true", result );
    }

    public void test_Invalid_Number_Pattern()
        throws Exception
    {
        validator.setPattern( "[0-9]+" );

        final boolean result = validator.validate( "ABCDEFG" );
        assertFalse( "this is an invalid expression and should be false", result );
    }
}
