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
public class DateValidatorTest
    extends TestCase
{
    DateValidator validator;

    public DateValidatorTest( final String s )
    {
        super( s );
    }

    public void setUp()
    {
        validator = new DateValidator();
        validator.setPattern( "MM/dd/yyyy" );
    }

    public void test_Valid_MM_dd_yyyy_Date()
        throws Exception
    {


        final boolean result = validator.validate( "05/11/1972" );
        assertTrue( "this is a valid MM/dd/yyyy date", result );
    }

    public void test_Invalid_MM_dd_yyyy_Date()
        throws Exception
    {
        final boolean result = validator.validate( "05/11/72" );
        assertFalse( "this is an invalid MM/dd/yyyy date", result );
    }
}
