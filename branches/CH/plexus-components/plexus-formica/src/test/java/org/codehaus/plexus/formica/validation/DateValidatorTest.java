package org.codehaus.plexus.formica.validation;

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
