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
public class CreditCardValidatorTest
    extends TestCase
{
    Validator validator;

    public CreditCardValidatorTest( final String s )
    {
        super( s );
    }

    public void setUp()
    {
        validator = new CreditCardValidator();
    }

    public void testMasterCard()
        throws Exception
    {
        final boolean result = validator.validate( "5500000000000004" );
        assertTrue( "this is a mastercard credit card", result );
    }

    public void testVisaCard()
        throws Exception
    {
        final boolean result = validator.validate( "4111111111111111" );
        assertTrue( "this is a visa credit card", result );
    }

    public void testAmexCard()
        throws Exception
    {
        final boolean result = validator.validate( "340000000000009" );
        assertTrue( "this is an amex credit card", result );
    }

    public void testDiscoverCard()
        throws Exception
    {
        final boolean result = validator.validate( "6011000000000004" );
        assertTrue( "this is a diners club credit card", result );
    }

    public void testBogusVisaCard()
        throws Exception
    {
        final boolean result = validator.validate( "55000000000000003" );
        assertFalse( "this is a bogus visa credit card", result );
    }

    // Unsupported credit card types
    // CarteBlance   30000000000004 (14)
    // DinersClub    30000000000004 (14)
    // EnRoute      201400000000009 (15)
    // JCB         3088000000000008 (16)
}
