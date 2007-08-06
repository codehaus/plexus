package org.codehaus.plexus.builder.runtime;

/*
 * Licensed to The Codehaus ( www.codehaus.org ) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Codehaus licenses this file
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
import org.codehaus.plexus.util.PropertyUtils;

import java.io.File;
import java.util.Properties;

/**
 * Simple test for the jsw wrappers, making sure they are found.
 * See the individual Jsw PlatformGenerator tests for more testing.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */
public class JswPlexusRuntimeBootloaderGeneratorTest
    extends PlexusTestCase
{
    private File outDir;

    public void setUp()
        throws Exception
    {
        super.setUp();

        outDir = getTestFile( "target/runtime" );
        outDir.mkdirs();
    }

    public void testLoadComponents()
        throws Exception
    {
        PlexusRuntimeBootloaderGenerator jswGenerator = getJswGenerator();

        assertTrue( jswGenerator.getPlatformGenerators().size() > 0 );
    }

    public void testMakesPlexusScripts()
        throws Exception
    {
        PlexusRuntimeBootloaderGenerator jswGenerator = getJswGenerator();

        File configurationPropertiesFile = getTestFile( "src/test/resources/configuration.properties" );

        Properties configurationProperties = PropertyUtils.loadProperties( configurationPropertiesFile );

        jswGenerator.generate( outDir, configurationProperties );
    }

    private PlexusRuntimeBootloaderGenerator getJswGenerator()
        throws Exception
    {
        return (PlexusRuntimeBootloaderGenerator)
            lookup( "org.codehaus.plexus.builder.runtime.PlexusRuntimeBootloaderGenerator", "jsw" );
    }
}
