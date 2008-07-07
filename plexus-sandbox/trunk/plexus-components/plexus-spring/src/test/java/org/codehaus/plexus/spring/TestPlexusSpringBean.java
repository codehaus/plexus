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

import org.codehaus.plexus.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:olamy at codehaus.org">olamy</a>
 * @since 8 mars 2008
 * @version $Id$
 */
public class TestPlexusSpringBean
    extends PlexusInSpringTestCase
{

    private Logger log = LoggerFactory.getLogger( getClass() );
    
    public void testdefaultWineSingleton()
    {
        Wine def = (Wine) lookup( Wine.ROLE, "default" );

        assertEquals( "default", def.getName() );

        Wine spring = (Wine) getApplicationContext().getBean( PlexusToSpringUtils.buildSpringId( Wine.ROLE ) );

        assertEquals( "default", spring.getName() );

        Wine secondSpring = (Wine) getApplicationContext().getBean( PlexusToSpringUtils.buildSpringId( Wine.ROLE ) );

        assertEquals( "default", secondSpring.getName() );

        // singleton we must have the same object
        assertTrue( spring == secondSpring );
        assertEquals( spring, secondSpring );
        assertEquals( spring.hashCode(), secondSpring.hashCode() );
    }

    public void testBeanPerLookup()
        throws Exception
    {

        Wine first = (Wine) lookup( Wine.class, "saumur" );
        assertEquals( "Saumur", first.getName() );

        Wine second = (Wine) lookup( Wine.class, "saumur" );
        assertEquals( "Saumur", second.getName() );

        // per lookup first must have different object
        assertTrue( first != second );
        assertNotSame( first, second );
        assertTrue( first.hashCode() != second.hashCode() );

    }
    
    public void testPlexusRegistryMustFailed()
        throws Exception
    {
        
            Registry registry = (Registry) lookup( Registry.class, "commons-configuration-exists" );
            assertEquals( "bar", registry.getSubset( "foo" ).getString( "foo" ) );
        
    }
    
    
    
}
