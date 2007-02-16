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

        assertTrue( v.validate( "http://www.apache.org" ) );

        assertTrue( v.validate( "http://www.apache.org/" ) );

        assertTrue( v.validate( "http://www.apache.org/;jsessionid=12345" ) );

        assertTrue( v.validate( "http://www.apache.org/my_servlet?param=value" ) );

        assertTrue( v.validate( "http://www.apache.org/pom.xml" ) );

        assertTrue( v.validate( "http://www.blah.museum/pom.xml" ) );

        assertTrue( v.validate( "http://www.blah.my-internal-local-toplevel-domain/pom.xml" ) );

        assertTrue( v.validate( "http://www.0123456789.fr/pom.xml" ) );

        assertTrue( v.validate( "http://127.0.0.1/pom.xml" ) );

        assertTrue( v.validate( "http://username:password@www.apache.org/pom.xml" ) );

        assertTrue( v.validate( "https://www.apache.org/pom.xml" ) );

        assertTrue( v.validate( "https://username:password@www.apache.org/pom.xml" ) );
        
        assertTrue( v.validate( "https://svn.example.com:8443/trunk/extranet/pom.xml" ) );

        assertTrue( v.validate( "http://localhost/pom.xml" ) );

        assertTrue( v.validate( "file://myserver/pom.xml" ) );

        assertTrue( v.validate( "file:///pom.xml" ) );

        assertTrue( v.validate( "file:///C:/Program Files/pom.xml" ) );

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
