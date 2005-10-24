package org.codehaus.plexus.formica.validation;

import org.codehaus.plexus.PlexusTestCase;

import java.net.URL;
import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class UrlSourceValidatorTest
    extends PlexusTestCase
{
    public void testValidatorWithValidUrl()
        throws Exception
    {
        Validator v = (Validator) lookup( Validator.ROLE, "url-source" );

        assertNotNull( v );

        String u = new File( getBasedir(), "pom.xml" ).toURL().toExternalForm();

        assertTrue( v.validate( u ) );
    }

    public void testValidatorWithNonExistentFileSourceUrl()
        throws Exception
    {
        Validator v = (Validator) lookup( Validator.ROLE, "url-source" );

        assertNotNull( v );

        String u = new File( getBasedir(), "nopom.xml" ).toURL().toExternalForm();

        assertFalse( v.validate( u ) );
    }

    public void testValidatorWithNonExistentHttpSourceUrl()
        throws Exception
    {
        Validator v = (Validator) lookup( Validator.ROLE, "url-source" );

        assertNotNull( v );

        String u = new URL( "http://ThisWontResolve.plexus.codehaus.org/bar" ).toExternalForm();

        assertFalse( v.validate( u ) );
    }
}
