/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codehaus.plexus.spring;

import junit.framework.TestCase;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 18 mars 2008
 * @version $Id$
 */
public class ConfigurableTreePlexusBeanTest
    extends TestCase
{
    private Logger log = LoggerFactory.getLogger( getClass() );
    
    public void testlookupConfigurablePlexusBean()
        throws Exception
    {
        try
        {
            String[] confLocations = new String[] {
                "classpath*:META-INF/plexus/components.xml",
                "testTreeConfigurablePlexusBean.xml" };
            ConfigurableApplicationContext applicationContext = new PlexusClassPathXmlApplicationContext( confLocations );
            ConfigurablePlexusBean plexusBean = (ConfigurablePlexusBean) applicationContext
                .getBean( "configurablePlexusBean#configurable" );
            assertNotNull( plexusBean.getTaskEntryEvaluators() );
            assertEquals( 2, plexusBean.getTaskEntryEvaluators().size() );
            assertEquals( "first", plexusBean.getTaskEntryEvaluators().get( 0 ) );
            assertEquals( "second", plexusBean.getTaskEntryEvaluators().get( 1 ) );
            
            assertNotNull( plexusBean.getContainer() );
            
            ArtifactHandler artifactHandler = (ArtifactHandler) applicationContext.getBean( "artifactHandler#ejb" );
            
            assertNotNull( plexusBean.getExecutableResolver() );
            
            assertNotNull( plexusBean.getMavenSettingsBuilder() );
            
            assertNotNull( plexusBean.getServiceLocator() );
            
            assertEquals( "foo", plexusBean.getToMailbox());
            
            assertEquals( "bar", plexusBean.getFromMailbox() );
        }
        catch ( Throwable e )
        {
            log.error( e.getMessage(), e );
            throw new Exception( e.getMessage(), e );
        }
    }
}
