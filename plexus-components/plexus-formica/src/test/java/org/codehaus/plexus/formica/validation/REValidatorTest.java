package org.codehaus.plexus.formica.validation;

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
