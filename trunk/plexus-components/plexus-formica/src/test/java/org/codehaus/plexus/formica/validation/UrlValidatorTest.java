package org.codehaus.plexus.formica.validation;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class UrlValidatorTest
    extends PlexusTestCase
{
    public void testValidatorWithValidUrl()
        throws Exception
    {
        Validator v = (Validator) lookup( Validator.ROLE, "url" );

        assertNotNull( v );

        assertTrue( v.validate( "http://www.apache.org/pom.xml" ) );

        assertTrue( v.validate( "http://localhost/pom.xml" ) );

        assertFalse( v.validate( "anything://www.apache.org/pom.xml" ) );
    }

    public void testValidatorWithInvalidUrl()
        throws Exception
    {
        Validator v = (Validator) lookup( Validator.ROLE, "url" );

        assertNotNull( v );

        assertFalse( v.validate( "a;ldaldskjaslkdjf" ) );
    }
}
