package org.codehaus.plexus.formica.validation;

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
