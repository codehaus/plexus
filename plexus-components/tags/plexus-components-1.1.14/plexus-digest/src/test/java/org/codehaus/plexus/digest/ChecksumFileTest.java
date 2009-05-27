package org.codehaus.plexus.digest;

/*
 * Copyright 2001-2007 The Codehaus.
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

/**
 * ChecksumFileTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class ChecksumFileTest extends PlexusTestCase
{
    private ChecksumFile checksum;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        checksum = (ChecksumFile) lookup( ChecksumFile.class.getName() );
    }

    public void testIsValidChecksum()
        throws Exception
    {
        File exampleDir = new File( getBasedir(), "src/test/examples" );

        assertTrue( checksum.isValidChecksum( new File( exampleDir, "redback-authz-open.jar.md5" ) ) );
        assertTrue( checksum.isValidChecksum( new File( exampleDir, "redback-authz-open.jar.sha1" ) ) );

        assertTrue( checksum.isValidChecksum( new File( exampleDir, "plain.jar.md5" ) ) );
        assertTrue( checksum.isValidChecksum( new File( exampleDir, "plain.jar.sha1" ) ) );

        assertTrue( checksum.isValidChecksum( new File( exampleDir, "single-space.jar.md5" ) ) );
        assertTrue( checksum.isValidChecksum( new File( exampleDir, "single-space.jar.sha1" ) ) );

        assertTrue( checksum.isValidChecksum( new File( exampleDir, "space-asterisk.jar.md5" ) ) );
        assertTrue( checksum.isValidChecksum( new File( exampleDir, "space-asterisk.jar.sha1" ) ) );

        assertTrue( checksum.isValidChecksum( new File( exampleDir, "openssl.jar.md5" ) ) );
        assertTrue( checksum.isValidChecksum( new File( exampleDir, "openssl.jar.sha1" ) ) );
    }

    public void testCreateChecksum()
        throws Exception
    {
        File dataFile = File.createTempFile( "plexus-digest-test", null );
        dataFile.deleteOnExit();

        File md5File = checksum.createChecksum( dataFile, new Md5Digester() );
        md5File.deleteOnExit();
        assertNotNull( md5File );
        assertTrue( md5File.isFile() );
        assertTrue( checksum.isValidChecksum( md5File ) );

        File sha1File = checksum.createChecksum( dataFile, new Sha1Digester() );
        sha1File.deleteOnExit();
        assertNotNull( sha1File );
        assertTrue( sha1File.isFile() );
        assertTrue( checksum.isValidChecksum( sha1File ) );
    }

}
