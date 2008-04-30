package org.codehaus.plexus.spring;

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

import java.util.Properties;

import junit.framework.TestCase;

import org.springframework.context.ConfigurableApplicationContext;

public abstract class AbstractPlexusApplicationContextTest
    extends TestCase
{
    /**
     * {@inheritDoc}
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        System.setProperty( "plexus-spring.debug", "true" );
    }

    protected abstract ConfigurableApplicationContext createApplicationContest( String[] strings );

    public void testInjectSpringBeansInPlexusComponent()
        throws Exception
    {

        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] { "testInjectSpringBeansInPlexusComponent.xml",
                "testInjectSpringBeansInPlexusComponent-context.xml" } );
        PlexusBean plexusBean = (PlexusBean) applicationContext.getBean( "plexusBean" );
        assertEquals( "field injection failed", "expected SpringBean", plexusBean.describe() );
        applicationContext.close();
    }

    public void testPlexusLifecycleSupport()
        throws Exception
    {
        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] {
                "testPlexusLifecycleSupport.xml" } );
        PlexusBean plexusBean = (PlexusBean) applicationContext.getBean( "plexusBean" );
        assertEquals( PlexusBean.INITIALIZED, plexusBean.getState() );
        assertNotNull( plexusBean.getContext() );
        assertNotNull( plexusBean.getLogger() );
        applicationContext.close();
        assertEquals( PlexusBean.DISPOSED, plexusBean.getState() );

    }

    public void testInjectMapForRole()
        throws Exception
    {
        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] {
                "testInjectMapForRole.xml",
                "testInjectMapForRole-context.xml" } );
        ComplexPlexusBean plexusBean = (ComplexPlexusBean) applicationContext.getBean( "complexPlexusBean" );
        assertTrue( plexusBean.getBeans().containsKey( "spring" ) );
        assertTrue( plexusBean.getBeans().containsKey( "plexus" ) );
        assertEquals( "2 components for role org.codehaus.plexus.spring.PlexusBean", plexusBean.toString() );
    }

    public void testInjectListForRole()
        throws Exception
    {
        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] {
                "testInjectListForRole.xml",
                "testInjectListForRole-context.xml" } );
        ComplexPlexusBean plexusBean = (ComplexPlexusBean) applicationContext.getBean( "complexPlexusBean" );
        assertEquals( 2, plexusBean.getBeansList().size() );
    }

    public void testInjectPlexusConfiguration()
        throws Exception
    {
        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] {
                "testInjectPlexusConfiguration.xml" } );
        ConfigPlexusBean plexusBean = (ConfigPlexusBean) applicationContext.getBean( "plexusBean" );
        assertEquals( "expected", plexusBean.getConfig().getChild( "xml" ).getAttribute( "test" ) );
    }

    public void testInjectPlexusProperties()
        throws Exception
    {
        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] {
                "testInjectPlexusProperties.xml" } );
        PropertiesPlexusBean plexusBean = (PropertiesPlexusBean) applicationContext.getBean( "plexusBean" );
        assertEquals( "expected", plexusBean.getProperties().getProperty( "test" ) );
    }

    public void testInjectSpringProperties()
        throws Exception
    {
        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] { "testInjectSpringProperties.xml" } );
        PropertiesPlexusBean plexusBean = (PropertiesPlexusBean) applicationContext.getBean( "plexusBean" );
        assertEquals( "expected", plexusBean.getProperties().getProperty( "test" ) );
    }

    public void testSpringNamespaces()
        throws Exception
    {
        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] { "testSpringNamespaces.xml" } );

        SpringBean bean = (SpringBean) applicationContext.getBean( "myBean" );
        
        assertEquals( "myValue", bean.getMyProperty() );

        Properties myProperties = (Properties) applicationContext.getBean( "myProperties" );
        assertNotNull( myProperties );
    }

    public void testInjectPlexusCollection()
        throws Exception
    {
        ConfigurableApplicationContext applicationContext =
            createApplicationContest( new String[] { "testInjectPlexusCollection.xml" } );
        PlexusBean plexusBean = (PlexusBean) applicationContext.getBean( "plexusBean" );
        assertEquals( "expected", plexusBean.getMessage() );
        assertEquals( 3, plexusBean.getStringList().size() );
        assertEquals( 2, plexusBean.getStringSet().size() );
    }

}
