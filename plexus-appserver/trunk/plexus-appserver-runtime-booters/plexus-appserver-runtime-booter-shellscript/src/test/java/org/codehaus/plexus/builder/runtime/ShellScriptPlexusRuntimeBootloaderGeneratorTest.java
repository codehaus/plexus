package org.codehaus.plexus.builder.runtime;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.util.Properties;

/**
 * Simple test for shell scripts, making sure they are found and
 * output the expected scripts.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */
public class ShellScriptPlexusRuntimeBootloaderGeneratorTest
    extends PlexusTestCase
{
    private File outDir;

    public void setUp()
        throws Exception
    {
        super.setUp();

        outDir = getTestFile( "target/tmp" );
        outDir.mkdirs();
    }

    public void testLoadComponents()
        throws Exception
    {
        PlexusRuntimeBootloaderGenerator shellGenerator = getShellGenerator();

        assertTrue( shellGenerator.getPlatformGenerators().size() > 0 );
    }

    public void testMakesPlexusScripts()
        throws Exception
    {
        PlexusRuntimeBootloaderGenerator shellGenerator = getShellGenerator();

        shellGenerator.generate( outDir, new Properties() );

        assertTrue( ( new File( outDir, "plexus.sh" ) ).exists() );
        assertTrue( ( new File( outDir, "plexus.bat" ) ).exists() );
    }

    private PlexusRuntimeBootloaderGenerator getShellGenerator()
        throws Exception
    {
        return (PlexusRuntimeBootloaderGenerator)
            lookup( "org.codehaus.plexus.builder.runtime.PlexusRuntimeBootloaderGenerator", "shellscript" );
    }
}
