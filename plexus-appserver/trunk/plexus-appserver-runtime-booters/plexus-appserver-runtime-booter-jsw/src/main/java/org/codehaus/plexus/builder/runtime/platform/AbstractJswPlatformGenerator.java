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
import org.codehaus.plexus.builder.runtime.JswPlexusRuntimeBootloaderGenerator;

import java.util.Properties;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;

/**
 * Abstract generator to aid with specific platforms of the JSW booter.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */

public abstract class AbstractJswPlatformGenerator
    implements JswPlatformGenerator
{
    protected static String JSW = "jsw";

    protected static String JSW_VERSION = JswPlexusRuntimeBootloaderGenerator.JSW_VERSION;

    /**
     * @plexus.requirement
     */
    protected GeneratorTools tools;

    protected void copyWrapperConf( File destDir, Properties configurationProperties, Properties additionalProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        Properties props = new Properties();

        if ( configurationProperties != null )
        {
            for ( Iterator i = configurationProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                props.setProperty( key, configurationProperties.getProperty( key ) );
            }
        }

        if ( additionalProperties != null )
        {
            for ( Iterator i = additionalProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                props.setProperty( key, additionalProperties.getProperty( key ) );
            }
        }

        try
        {
            tools.filterCopy( tools.getResourceAsStream( JSW + "/wrapper-common-" + JSW_VERSION + "/src/conf/wrapper.conf.in" ),
                              new File( destDir, "wrapper.conf" ),
                              props );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error whilst copying resource", e);
        }
    }
}
