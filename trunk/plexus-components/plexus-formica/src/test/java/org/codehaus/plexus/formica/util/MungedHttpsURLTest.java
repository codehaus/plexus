package org.codehaus.plexus.formica.util;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import junit.framework.TestCase;

/**
 * @author Christoph Sturm
 * @version $Id$
 */
public class MungedHttpsURLTest extends TestCase
{
    public void testHttpsURL() throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL;

        mungedHttpsURL = new MungedHttpsURL( "https://user:password@host" );

        assertEquals( "https://host", mungedHttpsURL.getUrlString() );

        assertEquals( "user", mungedHttpsURL.getUsername() );

        assertEquals( "password", mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();

        mungedHttpsURL = new MungedHttpsURL( "https://host" );

        assertEquals( "https://host", mungedHttpsURL.getUrlString() );

        assertEquals( null, mungedHttpsURL.getUsername() );

        assertEquals( null, mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }
    public void testHttpURL() throws MalformedURLException
    {
        MungedHttpsURL mungedHttpsURL;

        mungedHttpsURL = new MungedHttpsURL( "http://user:password@host" );

        assertEquals( "http://host", mungedHttpsURL.getUrlString() );

        assertEquals( "user", mungedHttpsURL.getUsername() );

        assertEquals( "password", mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();

        mungedHttpsURL = new MungedHttpsURL( "http://host" );

        assertEquals( "http://host", mungedHttpsURL.getUrlString() );

        assertEquals( null, mungedHttpsURL.getUsername() );

        assertEquals( null, mungedHttpsURL.getPassword() );

        mungedHttpsURL.getURL();
    }

}
