package org.codehaus.plexus.configuration;

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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 * @version $Id$
 */
public final class DefaultPlexusConfigurationTest
    extends TestCase
{
    private DefaultPlexusConfiguration configuration;

    public void setUp()
    {
        configuration = new DefaultPlexusConfiguration( "a" );
    }

    public void testWithHelper()
        throws Exception
    {
        PlexusConfiguration c = ConfigurationTestHelper.getTestConfiguration();

        ConfigurationTestHelper.testConfiguration( c );
    }

    public void testGetValue()
        throws Exception
    {
        String orgValue = "Original String";
        configuration.setValue( orgValue );
        assertEquals( orgValue, configuration.getValue() );
    }

    public void testGetAttribute()
        throws Exception
    {
        String key = "key";
        String value = "original value";
        String defaultStr = "default";
        configuration.setAttribute( key, value );
        assertEquals( value, configuration.getAttribute( key, defaultStr ) );
        assertEquals( defaultStr, configuration.getAttribute( "newKey", defaultStr ) );
    }

    public void testGetChild()
        throws Exception
    {
        DefaultPlexusConfiguration child = (DefaultPlexusConfiguration) configuration.getChild( "child" );

        assertNotNull( child );

        child.setValue( "child value" );

        assertEquals( 1, configuration.getChildCount() );

        child = (DefaultPlexusConfiguration) configuration.getChild( "child" );

        assertNotNull( child );

        assertEquals( "child value", child.getValue() );

        assertEquals( 1, configuration.getChildCount() );
    }

    public void testToString()
        throws Exception
    {
        // TODO: this currently works since getTestConfiguration() invokes PlexusTools.buildConfiguration()
        // and it returns XmlPlexusConfiguration actually.
        PlexusConfiguration c = ConfigurationTestHelper.getTestConfiguration();

        assertEquals( "<string string=\"string\">string</string>\n", c.getChild( "string" ).toString() );

        // TODO: uncomment once maven can test the latest plexus-utils
        assertEquals( "<singleton attribute=\"attribute\"/>\n", c.getChild( "singleton" ).toString() );
    }

    public void testProgrammaticConfigurationCreation()
        throws Exception
    {
        String viewRoot = "/path/to/viewRoot";

        PlexusConfiguration c = new DefaultPlexusConfiguration( "configuration" ).addChild( "viewRoot", viewRoot );

        assertEquals( viewRoot, c.getChild( "viewRoot" ).getValue() );
    }
}
