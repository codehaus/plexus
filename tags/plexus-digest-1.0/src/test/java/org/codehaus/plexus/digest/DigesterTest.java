package org.codehaus.plexus.digest;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Test the digester.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class DigesterTest
    extends PlexusTestCase
{
    private static final String MD5 = "adbc688ce77fa2aece4bb72cad9f98ba";

    private static final String SHA1 = "2a7b459938e12a2dc35d1bf6cff35e9c2b592fa9";

    private static final String WRONG_SHA1 = "4d8703779816556cdb8be7f6bb5c954f4b5730e2";

    private Digester sha1Digest;

    private Digester md5Digest;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        sha1Digest = (Digester) lookup( Digester.ROLE, "sha1" );
        md5Digest = (Digester) lookup( Digester.ROLE, "md5" );
    }

    public void testAlgorithm()
    {
        assertEquals( "SHA-1", sha1Digest.getAlgorithm() );
        assertEquals( "MD5", md5Digest.getAlgorithm() );
    }

    public void testBareDigestFormat()
        throws DigesterException, IOException
    {
        File file = new File( getClass().getResource( "/test-file.txt" ).getPath() );

        try
        {
            md5Digest.verify( file, MD5 );
        }
        catch ( DigesterException e )
        {
            fail( "Bare format MD5 must not throw exception" );
        }

        try
        {
            sha1Digest.verify( file, SHA1 );
        }
        catch ( DigesterException e )
        {
            fail( "Bare format SHA1 must not throw exception" );
        }

        try
        {
            sha1Digest.verify( file, WRONG_SHA1 );
            fail( "wrong checksum must throw an exception" );
        }
        catch ( DigesterException e )
        {
            //expected
        }
    }

    public void testOpensslDigestFormat()
        throws IOException, DigesterException
    {
        File file = new File( getClass().getResource( "/test-file.txt" ).getPath() );

        try
        {
            md5Digest.verify( file, "MD5(test-file.txt)= " + MD5 );
        }
        catch ( DigesterException e )
        {
            fail( "OpenSSL MD5 format must not cause exception" );
        }

        try
        {
            md5Digest.verify( file, "MD5 (test-file.txt) = " + MD5 );
        }
        catch ( DigesterException e )
        {
            fail( "FreeBSD MD5 format must not cause exception" );
        }

        try
        {
            sha1Digest.verify( file, "SHA1(test-file.txt)= " + SHA1 );
        }
        catch ( DigesterException e )
        {
            fail( "OpenSSL SHA1 format must not cause exception" );
        }

        try
        {
            sha1Digest.verify( file, "SHA1 (test-file.txt) = " + SHA1 );
        }
        catch ( DigesterException e )
        {
            fail( "FreeBSD SHA1 format must not cause exception" );
        }

        try
        {
            sha1Digest.verify( file, "SHA1 (FOO) = " + SHA1 );
            fail( "Wrong filename should cause an exception" );
        }
        catch ( DigesterException e )
        {
            //expected
        }

        try
        {
            sha1Digest.verify( file, "SHA1 (test-file.txt) = " + WRONG_SHA1 );
            fail( "Wrong sha1 should cause an exception" );
        }
        catch ( DigesterException e )
        {
            //expected
        }
    }

    public void testGnuDigestFormat()
        throws NoSuchAlgorithmException, IOException, DigesterException
    {
        File file = new File( getClass().getResource( "/test-file.txt" ).getPath() );

        try
        {
            md5Digest.verify( file, MD5 + " *test-file.txt" );
        }
        catch ( DigesterException e )
        {
            fail( "GNU format MD5 must not cause exception" );
        }

        try
        {
            md5Digest.verify( file, MD5 + " test-file.txt" );
        }
        catch ( DigesterException e )
        {
            fail( "GNU text format MD5 must not cause exception" );
        }

        try
        {
            sha1Digest.verify( file, SHA1 + " *test-file.txt" );
        }
        catch ( DigesterException e )
        {
            fail( "GNU format SHA1 must not cause exception" );
        }

        try
        {
            sha1Digest.verify( file, SHA1 + " test-file.txt" );
        }
        catch ( DigesterException e )
        {
            fail( "GNU text format SHA1 must not cause exception" );
        }

        try
        {
            sha1Digest.verify( file, SHA1 + " FOO" );
            fail( "Wrong filename cause an exception" );
        }
        catch ( DigesterException e )
        {
            //expected
        }

        try
        {
            sha1Digest.verify( file, WRONG_SHA1 + " test-file.txt" );
            fail( "Wrong SHA1 cause an exception" );
        }
        catch ( DigesterException e )
        {
            //expected
        }
    }

    public void testUntrimmedContent()
        throws NoSuchAlgorithmException, IOException
    {
        File file = new File( getClass().getResource( "/test-file.txt" ).getPath() );
        try
        {
            sha1Digest.verify( file, SHA1 + " *test-file.txt \n" );
        }
        catch ( DigesterException e )
        {
            fail( "GNU untrimmed SHA1 must not cause exception" );
        }
    }
}
