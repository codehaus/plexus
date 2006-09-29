package org.codehaus.plexus.formica.util;

/*
 * Copyright 2004-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author Christoph Sturm
 * @version $Id$
 */
public class MungedHttpsURLTest
    extends TestCase
{
    public void testHttpsURL()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "https://host" );

        assertEquals( "https://host", mungedHttpsURL.getUrlString() );

        assertEquals( null, mungedHttpsURL.getUsername() );

        assertEquals( null, mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }

    public void testHttpsURLPath()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "https://host/path" );

        assertEquals( "https://host/path", mungedHttpsURL.getUrlString() );

        assertEquals( null, mungedHttpsURL.getUsername() );

        assertEquals( null, mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }

    public void testHttpsURLAuthentication()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "https://user:password@host" );

        assertEquals( "https://host", mungedHttpsURL.getUrlString() );

        assertEquals( "user", mungedHttpsURL.getUsername() );

        assertEquals( "password", mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }

    public void testHttpsURLAuthenticationPath()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "https://user:password@host/path" );

        assertEquals( "https://host/path", mungedHttpsURL.getUrlString() );

        assertEquals( "user", mungedHttpsURL.getUsername() );

        assertEquals( "password", mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }

    public void testHttpURL()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "http://host" );

        assertEquals( "http://host", mungedHttpsURL.getUrlString() );

        assertEquals( null, mungedHttpsURL.getUsername() );

        assertEquals( null, mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();

        mungedHttpsURL = new MungedHttpsURL( "http://host:1234" );

        assertEquals( "http://host:1234", mungedHttpsURL.getUrlString() );

        assertEquals( null, mungedHttpsURL.getUsername() );

        assertEquals( null, mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }

    public void testHttpURLPath()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "http://host/path" );

        assertEquals( "http://host/path", mungedHttpsURL.getUrlString() );

        assertEquals( null, mungedHttpsURL.getUsername() );

        assertEquals( null, mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();

        mungedHttpsURL = new MungedHttpsURL( "http://host:1234/path" );

        assertEquals( "http://host:1234/path", mungedHttpsURL.getUrlString() );

        assertEquals( null, mungedHttpsURL.getUsername() );

        assertEquals( null, mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }

    public void testHttpURLAuthentication()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "http://user:password@host" );

        assertEquals( "http://host", mungedHttpsURL.getUrlString() );

        assertEquals( "user", mungedHttpsURL.getUsername() );

        assertEquals( "password", mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();

        mungedHttpsURL = new MungedHttpsURL( "http://user:password@host:1234" );

        assertEquals( "http://host:1234", mungedHttpsURL.getUrlString() );

        assertEquals( "user", mungedHttpsURL.getUsername() );

        assertEquals( "password", mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }

    public void testHttpURLAuthenticationPath()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "http://user:password@host/path" );

        assertEquals( "http://host/path", mungedHttpsURL.getUrlString() );

        assertEquals( "user", mungedHttpsURL.getUsername() );

        assertEquals( "password", mungedHttpsURL.getPassword() );

        URL url = mungedHttpsURL.getURL();
        
        assertNotNull( url );

        mungedHttpsURL = new MungedHttpsURL( "http://user:password@host:1234/path" );

        assertEquals( "http://host:1234/path", mungedHttpsURL.getUrlString() );

        assertEquals( "user", mungedHttpsURL.getUsername() );

        assertEquals( "password", mungedHttpsURL.getPassword() );

        url = mungedHttpsURL.getURL();

        assertNotNull( url );
    }
    
    public void testIsValid()
        throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL = new MungedHttpsURL( "http://www.google.com/foo" );

        assertTrue( "URL is not valid", mungedHttpsURL.isValid() );
    }
}
