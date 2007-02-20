package org.codehaus.plexus.registry.test;

/*
 * Copyright 2007 The Codehaus Foundation.
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
import org.codehaus.plexus.registry.Registry;

import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:Olivier.LAMY@accor.com">olamy</a>
 * @version $Id$
 * @since 8 feb. 07
 */
public abstract class AbstractRegistryTest
    extends PlexusTestCase
{

    public abstract String getRoleHint();

    public Registry getRegistry()
        throws Exception
    {
        return (Registry) lookup( Registry.ROLE, getRoleHint() );
    }

    public void testInt()
        throws Exception
    {
        Registry registry = getRegistry();
        assertEquals( "not 2 ", 2, registry.getInt( "two" ) );
    }

    public void testIntUnknown()
        throws Exception
    {
        Registry registry = getRegistry();
        try
        {
            registry.getInt( "unknown" );
            assertTrue( "no NoSuchElementException", false );
        }
        catch ( NoSuchElementException e )
        {
            // cool it works
        }
    }

    public void testString()
        throws Exception
    {
        Registry registry = getRegistry();
        assertEquals( "not foo ", "foo", registry.getString( "string" ) );
    }

    public void testStringUnknown()
        throws Exception
    {
        Registry registry = getRegistry();
        String value = registry.getString( "unknown" );
        assertNull( "unknow not null", value );

    }

    public void testBoolean()
        throws Exception
    {
        Registry registry = getRegistry();
        assertEquals( "not true ", true, registry.getBoolean( "boolean" ) );
    }

    public void testBooleanUnknown()
        throws Exception
    {
        Registry registry = getRegistry();
        try
        {
            registry.getBoolean( "unknown" );
            assertTrue( "no NoSuchElementException", false );
        }
        catch ( NoSuchElementException e )
        {
            // cool it works
        }
    }

    public void testIsNotEmpty()
        throws Exception
    {
        assertFalse( getRegistry().isEmpty() );
    }

    public void testGetSubRegistry()
        throws Exception
    {
        assertNotNull( getRegistry().getSubset( "subOne" ) );
    }

    public void testgetSubsetValues()
        throws Exception
    {
        Registry sub = getRegistry().getSubset( "subOne" );
        assertNotNull( sub );
        assertEquals( "entryOne", sub.getString( "firstEntry" ) );
        assertEquals( "entryTwo", sub.getString( "secondEntry" ) );
    }

    public void testgetSubsetEmpty()
        throws Exception
    {
        assertNotNull( getRegistry().getSubset( "none" ) );
        assertTrue( getRegistry().getSubset( "none" ).isEmpty() );

    }
}
