package org.codehaus.plexus.component;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.logging.LoggerManager;

import java.util.Map;

import junit.framework.TestCase;

public class MapOrientedComponentProcessingTest
    extends TestCase
{

    public void testShouldFindAndInitializeMapOrientedComponent()
        throws Exception
    {
        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( TestMapOrientedComponent.ROLE );
        descriptor.setImplementation( TestMapOrientedComponent.ROLE );
        descriptor.setComponentComposer( "map-oriented" );
        descriptor.setComponentConfigurator( "map-oriented" );

        ComponentRequirement requirement = new ComponentRequirement();
        requirement.setFieldName( "testRequirement" );
        requirement.setRole( LoggerManager.ROLE );

        descriptor.addRequirement( requirement );

        XmlPlexusConfiguration param = new XmlPlexusConfiguration( "testParameter", null );
        param.setValue( "testValue" );

        PlexusConfiguration configuration = new XmlPlexusConfiguration( "configuration", null );
        configuration.addChild( param );

        descriptor.setConfiguration( configuration );

        Embedder embedder = new Embedder();
        embedder.start();

        embedder.getContainer().addComponentDescriptor( descriptor );

        TestMapOrientedComponent component = (TestMapOrientedComponent) embedder.lookup( TestMapOrientedComponent.ROLE );

        Map context = component.getContext();

        assertTrue( "requirement (LogManager) missing from context.",
                    ( context.get( "testRequirement" ) instanceof LoggerManager ) );
        
        assertEquals( "parameter missing from context.", "testValue", context.get( "testParameter" ) );
    }

}
