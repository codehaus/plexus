package org.codehaus.plexus.builder.runtime.platform;

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBootloaderGeneratorException;

import java.io.File;
import java.util.Properties;

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

/**
 * Generator to create the Windows specific portions of the JSW booter.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */
public class WindowsJswPlatformGenerator
    extends AbstractJswPlatformGenerator
{
    public static final String WINDOWS = "windows-x86-32";

    public static final String WINDOWS_SOURCE = JSW + "/wrapper-windows-x86-32-" + JSW_VERSION;
// We are not ready for the current JSW released scripts
    public static final String WINDOWS_SCRIPTS = JSW + "/";

    public void generate( File binDirectory,
                          String resourceDir,
                          Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        // TODO: make it configurable - we don't always want a subdir
        File windowsBinDir = new File( binDirectory, WINDOWS );
        tools.mkdirs( windowsBinDir );

        tools.copyResource( WINDOWS + "/wrapper.exe", WINDOWS_SOURCE + "/bin/wrapper.exe", true, binDirectory );
        tools.copyResource( WINDOWS + "/wrapper.dll", WINDOWS_SOURCE + "/lib/wrapper.dll", false, binDirectory );
//        tools.copyResource( WINDOWS + "/run.bat", WINDOWS_SOURCE + "/src/bin/App.bat.in", false, binDirectory );
//        tools.copyResource( WINDOWS + "/InstallService.bat", WINDOWS_SOURCE + "/src/bin/InstallApp-NT.bat.in", false, binDirectory );
//        tools.copyResource( WINDOWS + "/UninstallService.bat", WINDOWS_SOURCE + "/src/bin/UninstallApp-NT.bat.in", false, binDirectory );
        tools.copyResource( WINDOWS + "/run.bat", WINDOWS_SCRIPTS + "run.bat", false, binDirectory );
        tools.copyResource( WINDOWS + "/InstallService.bat", WINDOWS_SCRIPTS + "InstallService.bat", false, binDirectory );
        tools.copyResource( WINDOWS + "/UninstallService.bat", WINDOWS_SCRIPTS + "UninstallService.bat", false, binDirectory );

        Properties win32Props = new Properties();
        win32Props.setProperty( "library.path", "../../bin/" + WINDOWS );
        win32Props.setProperty( "extra.path", ";" );
        copyWrapperConf( windowsBinDir, configurationProperties, win32Props );
    }
}
