package org.codehaus.plexus.builder.runtime.platform;

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

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBootloaderGeneratorException;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Generator to create the MacOSX specific portions of the JSW booter.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */
public class MacOSXJswPlatformGenerator
    extends AbstractJswPlatformGenerator
{
    public static final String MACOSX = "macosx-universal-32";

    public static final String MACOSX_SOURCE = JSW + "/wrapper-macosx-universal-32-" + JSW_VERSION;

    public void generate( File binDirectory,
                          String resourceDir,
                          Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        try
        {
            // TODO: make it configurable - we don't always want a subdir
            File osxBinDir = new File( binDirectory, MACOSX );
            tools.mkdirs( osxBinDir );

            File runSh = new File( osxBinDir, "run.sh" );
            tools.filterCopy( tools.getResourceAsStream( JSW + "/wrapper-common-" + JSW_VERSION + "/src/bin/sh.script.in" ), runSh, configurationProperties );
            tools.executable( runSh );

            tools.copyResource( MACOSX + "/wrapper", MACOSX_SOURCE + "/bin/wrapper", true, binDirectory  );
            tools.copyResource( MACOSX + "/libwrapper.jnilib", MACOSX_SOURCE + "/lib/libwrapper.jnilib", false, binDirectory );

            Properties osxProps = new Properties();
            osxProps.setProperty( "library.path", "../../bin/" + MACOSX );
            osxProps.setProperty( "extra.path", "" );
            copyWrapperConf( osxBinDir, configurationProperties, osxProps );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error whilst generating macosx script", e);
        }
        catch ( CommandLineException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error whilst making macosx script executable", e);
        }
    }
}
