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

import org.codehaus.plexus.builder.runtime.GeneratorTools;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBootloaderGeneratorException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * PlatformGenerator for the windows specific shell scripts
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */
public class WindowsShellScriptPlatformGenerator
    implements ShellScriptPlatformGenerator
{
    private final static String WINDOWS_LAUNCHER_TEMPLATE = "templates/plexus-bat.vm";

    private GeneratorTools tools;

    public void generate( File binDirectory,
                          String resourceDir,
                          Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        try
        {
            tools.mergeTemplate( WINDOWS_LAUNCHER_TEMPLATE, new File( binDirectory, "plexus.bat" ), false,
                                 configurationProperties );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Failed to merge the shell script templates", e );
        }
    }
}