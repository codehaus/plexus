package org.codehaus.plexus.component.factory;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class DiscoveredComponentFactoryTest
    extends PlexusTestCase
{
    public void testShouldFindComponentFactoriesDefinedInBothPlexusXmlAndComponentsXml()
        throws Exception
    {
        assertNotNull( "Cannot find test component factory from plexus.xml test resource.",
                       lookup( ComponentFactory.class, "testFactory1" ) );

        assertNotNull( "Cannot find test component factory from components.xml test resource.",
                       lookup( ComponentFactory.class, "testFactory2" ) );
    }

    public void testShouldInstantiateComponentUsingFactoryDiscoveredInPlexusXml()
        throws Exception
    {
    }

    public void testShouldInstantiateComponentUsingFactoryDiscoveredInComponentsXml()
        throws Exception
    {
        lookupTestComponent( "testFactory2" );
    }

    private void lookupTestComponent( String factoryId )
        throws Exception
    {
        ComponentDescriptor<?> descriptor = new ComponentDescriptor<Object>();

        descriptor.setComponentFactory( factoryId );

        descriptor.setRole( TestRole.class.getName() );

        descriptor.setRoleHint( "hint" );

        descriptor.setImplementation( DefaultTestRole.class.getName() );

        getContainer().addComponentDescriptor( descriptor );

        // this will return a TestFactoryResultComponent due to the component factory set above 
        Object component = lookup( TestRole.class.getName(), "hint" );

        assertTrue( component instanceof TestFactoryResultComponent );

        assertEquals( factoryId, ( (TestFactoryResultComponent) component ).getFactoryId() );
    }

    public interface TestRole {
    }

    public class DefaultTestRole implements TestRole {
    }
}
