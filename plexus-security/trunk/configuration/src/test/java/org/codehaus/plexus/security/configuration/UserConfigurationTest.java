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

/**
 * UserConfigurationTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class UserConfigurationTest
    extends PlexusTestCase
{
    public void testLoad()
        throws Exception
    {
        UserConfiguration config = (UserConfiguration) lookup( UserConfiguration.ROLE );
        assertNotNull( config );
    }

    public void testGetString()
        throws Exception
    {
        UserConfiguration config = (UserConfiguration) lookup( UserConfiguration.ROLE );
        assertEquals( "localhost", config.getString( "email.smtp.host" ) );
        assertEquals( "127.0.0.1", config.getString( "email.smtp.host.bad", "127.0.0.1" ) );
    }

    public void testGetBoolean()
        throws Exception
    {
        UserConfiguration config = (UserConfiguration) lookup( UserConfiguration.ROLE );
        assertFalse( config.getBoolean( "email.smtp.ssl.enabled" ) );
        assertTrue( config.getBoolean( "email.smtp.tls.enabled", true ) );
    }

    public void testGetInt()
        throws Exception
    {
        UserConfiguration config = (UserConfiguration) lookup( UserConfiguration.ROLE );
        assertEquals( 25, config.getInt( "email.smtp.port" ) );
        assertEquals( 8080, config.getInt( "email.smtp.port.bad", 8080 ) );
    }
}
