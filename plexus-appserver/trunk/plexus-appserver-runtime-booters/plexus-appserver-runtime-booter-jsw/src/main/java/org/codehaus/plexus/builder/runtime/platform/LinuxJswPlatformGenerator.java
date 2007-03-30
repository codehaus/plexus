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

import java.io.File;
import java.util.Properties;

/**
 * Generator to create the Linux specific portions of the JSW booter.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */
public class LinuxJswPlatformGenerator
    extends AbstractJswPlatformGenerator
{
    public static final String LINUX = "linux";

    public static final String LINUX_SOURCE = JSW + "/wrapper-linux-x86-32-" + JSW_VERSION;

    public void generate( File binDirectory,
                          String resourceDir,
                          Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        // TODO: make it configurable - we don't always want a subdir
        File linuxBinDir = new File( binDirectory, LINUX );
        tools.mkdirs( linuxBinDir );

        File runSh = new File( linuxBinDir, "run.sh" );
        tools.filterCopy( tools.getResourceAsStream( JSW + "/run.sh" ), runSh, configurationProperties );
        tools.executable( runSh );

        tools.copyResource( LINUX + "/wrapper", LINUX_SOURCE + "/bin/wrapper", true, binDirectory  );
        tools.copyResource( LINUX + "/libwrapper.so", LINUX_SOURCE + "/lib/libwrapper.so", false, binDirectory );

        Properties linuxProps = new Properties();
        linuxProps.setProperty( "library.path", "../../bin/" + LINUX );
        linuxProps.setProperty( "extra.path", "" );
        copyWrapperConf( linuxBinDir, configurationProperties, linuxProps );
    }
}
