package org.codehaus.plexus.formica.validation;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class EmailValidatorTest
    extends TestCase
{
    Validator validator;

    public EmailValidatorTest( final String s )
    {
        super( s );
    }

    public void setUp()
    {
        validator = new EmailValidator();
    }

    public void testValidEmailAddress()
        throws Exception
    {
        final boolean result = validator.validate( "jason@maven.org" );
        assertTrue( "this is a valid email address", result );
    }

    public void testInvalidEmailAddress()
        throws Exception
    {
        final boolean result = validator.validate( "jason&maven.org" );
        assertFalse( "this is a valid email address", result );
    }
}
