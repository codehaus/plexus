/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.plexus.spring;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 28 mars 2008
 * @version $Id$
 */
public class PlexusServletContextPropertyPlaceholderConfigurer
    extends ServletContextPropertyPlaceholderConfigurer
{

    private Logger log = LoggerFactory.getLogger( getClass() );

    private static final String PLEXUS_HOME = "plexus.home";

    private ServletContext servletContext;

    private void setPlexusHome( ServletContext context, Properties contextProperties )
    {
        if ( contextProperties.contains( PLEXUS_HOME ) )
        {
            // already configured
            return;
        }
        String realPath = context.getRealPath( "/WEB-INF" );

        if ( realPath != null )
        {
            File f = new File( realPath );

            contextProperties.setProperty( PLEXUS_HOME, f.getAbsolutePath() );
        }
        else
        {
            log.info( "Not setting 'plexus.home' as plexus is running inside webapp with no 'real path'." );
        }
    }

    public void setServletContext( ServletContext servletContext )
    {
        this.servletContext = servletContext;
        super.setServletContext( servletContext );
    }

    protected Properties mergeProperties()
        throws IOException
    {
        Properties mergedProperties = super.mergeProperties();
        setPlexusHome( this.servletContext, mergedProperties );
        return mergedProperties;
    }

}
