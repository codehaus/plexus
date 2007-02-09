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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.StringUtils;

/**
 * UserConfigurationTest
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class UserConfigurationTest
    extends PlexusTestCase
{
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        System.setProperty( "plexus.home", (String) getContainer().getContext().get( "plexus.home" ) );
    }

    private void assertEmpty( String str )
    {
        if ( StringUtils.isNotEmpty( str ) )
        {
            fail( "Expected String to be empty." );
        }
    }

    public void testLoad()
        throws Exception
    {
        UserConfiguration config = (UserConfiguration) lookup( UserConfiguration.ROLE );
        assertNotNull( config );
        // check that the configuration loaded correctly - if this fails, maybe you aren't in the right basedir
        assertNotNull( config.getString( "test.value" ) );
    }

    public void testGetString()
        throws Exception
    {
        UserConfiguration config = (UserConfiguration) lookup( UserConfiguration.ROLE );
        // Test default configuration entry
        assertEquals( "25", config.getString( "email.smtp.port" ) );
        // Test overlaid configuration entry
        assertEquals( "127.0.2.2", config.getString( "email.smtp.host" ) );
        // Test default value
        assertEquals( "127.0.0.1", config.getString( "email.smtp.host.bad", "127.0.0.1" ) );
        // Test expressions
        assertEquals( "jdbc:derby:" + System.getProperty( "plexus.home" ) + "/database;create=true",
                      config.getString( "jdbc.url" ) );
        assertEquals( "foo/bar", config.getString( "test.expression" ) );

        assertEmpty( config.getString( "email.smtp.foo.foo" ) );
    }

    public void testGetBoolean()
        throws Exception
    {
        UserConfiguration config = (UserConfiguration) lookup( UserConfiguration.ROLE );
        assertFalse( config.getBoolean( "email.smtp.ssl.enabled" ) );
        assertFalse( config.getBoolean( "email.smtp.tls.enabled" ) );
    }

    public void testGetInt()
        throws Exception
    {
        UserConfiguration config = (UserConfiguration) lookup( UserConfiguration.ROLE );
        assertEquals( 25, config.getInt( "email.smtp.port" ) );
        assertEquals( 8080, config.getInt( "email.smtp.port.bad", 8080 ) );
    }
}
