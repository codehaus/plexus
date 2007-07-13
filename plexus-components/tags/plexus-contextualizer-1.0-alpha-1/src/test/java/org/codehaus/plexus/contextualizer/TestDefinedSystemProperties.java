package org.codehaus.plexus.contextualizer;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.context.ContextException;

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
/**
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 22 feb. 07
 * @version $Id$
 */
public class TestDefinedSystemProperties
    extends PlexusTestCase
{

    protected String getConfigurationName( String arg0 )
        throws Exception
    {
        return super.getCustomConfigurationName();
    }

    protected String getCustomConfigurationName()
    {
        return "definedsysprops.xml";
    }

    public void testUserHome()
        throws Exception
    {
        String userHome = (String) getContainer().getContext().get( "user.home" );
        assertNotNull( userHome );
        assertEquals( System.getProperty( "user.home" ), userHome );
    }

    public void testNonSettedSysProps()
        throws Exception
    {
        try
        {
            String userDir = (String) getContainer().getContext().get( "user.dir" );
            assertTrue( "not in ContextException", false );
        }
        catch ( ContextException e )
        {
            // cool it works
        }
    }

    public void testProvidedContextValue()
        throws Exception
    {
        String value = (String) getContainer().getContext().get( "keyone" );
        // keyone -> valueone
        assertEquals( "valueone", value );
    }

}
