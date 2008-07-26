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
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * ChecksumFileTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class ChecksumFileTest extends PlexusTestCase
{
    private ChecksumFile checksum;

    protected void setUp() throws Exception
    {
        super.setUp();

        checksum = (ChecksumFile) lookup( ChecksumFile.class.getName() );
    }

    public void testChecksum() throws FileNotFoundException, DigesterException, IOException
    {
        File exampleDir = new File( getBasedir(), "src/test/examples" );

        assertTrue( checksum.isValidChecksum( new File( exampleDir, "redback-authz-open.jar.md5" ) ) );
        assertTrue( checksum.isValidChecksum( new File( exampleDir, "redback-authz-open.jar.sha1" ) ) );
    }
}
