package org.codehaus.plexus.registry;

/*
 * Copyright 2007, Brett Porter
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

import java.util.NoSuchElementException;

/**
 * Test the commons configuration registry.
 */
public class CommonsConfigurationRegistryTest
    extends PlexusTestCase
{
    private Registry registry;

    private static final int INT_TEST_VALUE = 8080;

    public void testDefaultConfiguration()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "default" );

        assertEquals( "Check system property override", System.getProperty( "user.dir" ),
                      registry.getString( "user.dir" ) );
        assertEquals( "Check system property", System.getProperty( "user.home" ), registry.getString( "user.home" ) );
        assertNull( "Check other properties are not loaded", registry.getString( "test.value" ) );
    }

    public void testBuilderConfiguration()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "builder" );

        assertEquals( "Check system property override", "new user dir", registry.getString( "user.dir" ) );
        assertEquals( "Check system property default", System.getProperty( "user.home" ),
                      registry.getString( "user.home" ) );
        assertEquals( "Check other properties are loaded", "foo", registry.getString( "test.value" ) );
        assertEquals( "Check other properties are loaded", 1, registry.getInt( "test.number" ) );
        assertTrue( "Check other properties are loaded", registry.getBoolean( "test.boolean" ) );
    }

    public void testDump()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "default" );

        String dump = registry.dump();
        assertTrue( dump.startsWith( "Configuration Dump.\n\"" ) );
    }

    public void testDefaults()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "builder" );

        assertNull( "Check getString returns null", registry.getString( "foo" ) );
        assertEquals( "Check getString returns default", "bar", registry.getString( "foo", "bar" ) );

        try
        {
            registry.getInt( "foo" );
            fail();
        }
        catch ( NoSuchElementException e )
        {
            // success
        }

        assertEquals( "Check getInt returns default", INT_TEST_VALUE, registry.getInt( "foo", INT_TEST_VALUE ) );

        try
        {
            registry.getBoolean( "foo" );
            fail();
        }
        catch ( NoSuchElementException e )
        {
            // success
        }
    }

    public void testInterpolation()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "builder" );

        assertEquals( "Check system property interpolation", System.getProperty( "user.home" ) + "/.m2/repository",
                      registry.getString( "repository" ) );

        assertEquals( "Check configuration value interpolation", "foo/bar",
                      registry.getString( "test.interpolation" ) );
    }

    public void testAddConfigurationXmlFile()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "default" );

        registry.addConfigurationFromFile( getTestFile( "src/test/resources/org/codehaus/plexus/registry/test.xml" ) );

        assertEquals( "Check system property default", System.getProperty( "user.dir" ),
                      registry.getString( "user.dir" ) );
        assertEquals( "Check other properties are loaded", "foo", registry.getString( "test.value" ) );
    }

    public void testAddConfigurationPropertiesFile()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "default" );

        registry.addConfigurationFromFile(
            getTestFile( "src/test/resources/org/codehaus/plexus/registry/test.properties" ) );

        assertEquals( "Check system property default", System.getProperty( "user.dir" ),
                      registry.getString( "user.dir" ) );
        assertEquals( "Check other properties are loaded", "baz", registry.getString( "foo.bar" ) );
        assertNull( "Check other properties are not loaded", registry.getString( "test.value" ) );
    }

    public void testAddConfigurationXmlResource()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "default" );

        registry.addConfigurationFromResource( "org/codehaus/plexus/registry/test.xml" );

        assertEquals( "Check system property default", System.getProperty( "user.dir" ),
                      registry.getString( "user.dir" ) );
        assertEquals( "Check other properties are loaded", "foo", registry.getString( "test.value" ) );
    }

    public void testAddConfigurationPropertiesResource()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "default" );

        registry.addConfigurationFromResource( "org/codehaus/plexus/registry/test.properties" );

        assertEquals( "Check system property default", System.getProperty( "user.dir" ),
                      registry.getString( "user.dir" ) );
        assertEquals( "Check other properties are loaded", "baz", registry.getString( "foo.bar" ) );
        assertNull( "Check other properties are not loaded", registry.getString( "test.value" ) );
    }

    public void testAddConfigurationUnrecognisedType()
        throws Exception
    {
        registry = (Registry) lookup( Registry.class.getName(), "default" );

        try
        {
            registry.addConfigurationFromResource( "org/codehaus/plexus/registry/test.foo" );
            fail();
        }
        catch ( RegistryException e )
        {
            // success
        }

        try
        {
            registry.addConfigurationFromFile(
                getTestFile( "src/test/resources/org/codehaus/plexus/registry/test.foo" ) );
            fail();
        }
        catch ( RegistryException e )
        {
            // success
        }
    }
}
