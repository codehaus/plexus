package org.codehaus.plexus.security.configuration;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.io.File;
import java.net.URL;

/**
 * ConfigurationFactory
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.configuration.UserConfiguration"
 */
public class UserConfiguration
    extends CompositeConfiguration
    implements Initializable
{
    public static final String ROLE = UserConfiguration.class.getName();

    /**
     * @plexus.configuration default-value="${user.home}/.m2"
     */
    private String basePath;

    /**
     * @plexus.configuration default-value="security.xml"
     */
    private String configFilename;

    private static final String DEFAULT_CONFIG_RESOURCE = "/org/codehaus/plexus/security/config-defaults.properties";

    public void initialize()
        throws InitializationException
    {
        URL defaultConfigURL = this.getClass().getResource( DEFAULT_CONFIG_RESOURCE );
        if ( defaultConfigURL == null )
        {
            throw new InitializationException( "Unable to find resource [" + DEFAULT_CONFIG_RESOURCE + "]" );
        }

        try
        {
            PropertiesConfiguration defaultConfig = new PropertiesConfiguration( defaultConfigURL );

            PropertiesConfiguration userConfig = new PropertiesConfiguration();
            userConfig.setBasePath( basePath );

            File configFile = new File( new File( basePath ), configFilename );
            if ( configFile.exists() )
            {
                userConfig.load( configFilename );
            }

            // Check the user properties file first.
            super.addConfiguration( userConfig );
            // Then check the default config file.
            super.addConfiguration( defaultConfig );
        }
        catch ( ConfigurationException e )
        {
            throw new InitializationException( "Unable to load configuration [" + configFilename + "] : "
                + e.getMessage(), e );
        }
    }
}
