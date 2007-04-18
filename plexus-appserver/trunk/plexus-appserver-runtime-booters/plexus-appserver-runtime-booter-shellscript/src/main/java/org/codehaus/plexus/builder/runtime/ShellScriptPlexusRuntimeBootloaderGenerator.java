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

import org.codehaus.plexus.builder.runtime.platform.ShellScriptPlatformGenerator;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.Iterator;

/**
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class ShellScriptPlexusRuntimeBootloaderGenerator
    implements PlexusRuntimeBootloaderGenerator
{
    private Map platformGenerators;

    private GeneratorTools tools;

    public void generate( File outputDirectory, Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        try
        {
            File binDirectory = new File( outputDirectory, "bin" );
            tools.mkdirs( binDirectory );

            // ----------------------------------------------------------------------------
            // Look up the appropriate generators
            // ----------------------------------------------------------------------------

            // for now just run them all, this needs to be configurable
            // TODO make the list we load configurable
            Iterator platforms = platformGenerators.keySet().iterator();
            while ( platforms.hasNext() )
            {
                ShellScriptPlatformGenerator platform =
                    (ShellScriptPlatformGenerator) platformGenerators.get(platforms.next() );

                // TODO figure if we can fix / remove this second param
                platform.generate( binDirectory, "", configurationProperties );
            }
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Unable to create shellscript booters", e);
        }
    }

    public Map getPlatformGenerators()
    {
        return platformGenerators;
    }
}
