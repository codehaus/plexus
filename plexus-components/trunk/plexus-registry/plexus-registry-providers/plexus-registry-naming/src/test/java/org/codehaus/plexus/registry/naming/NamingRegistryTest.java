package org.codehaus.plexus.registry.naming;

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

import java.util.Properties;

import org.codehaus.plexus.registry.test.AbstractRegistryTest;

/**
 * @author <a href="mailto:Olivier.LAMY@accor.com">olamy</a>
 * @version $Id$
 * @since 8 feb. 07
 */
public class NamingRegistryTest
    extends AbstractRegistryTest
{

    // need a load-on-start for org.codehaus.plexus.naming.Naming

    protected String getCustomConfigurationName()
    {
        return "plexus.xml";
    }

    /** 
     * @see org.codehaus.plexus.registry.test.AbstractRegistryTest#getRoleHint()
     */
    public String getRoleHint()
    {
        return "naming";
    }

    /**
     * @return
     */
    public String getEmptyRoleHint()
    {
        return "empty-naming";
    }

    public void testNaminggetKeys()
        throws Exception
    {
        assertFalse( this.getRegistry().getKeys().isEmpty() );
        assertEquals( 4, this.getRegistry().getKeys().size() );
    }

    public void testgetProperties()
        throws Exception
    {
        Properties properties = this.getRegistry().getProperties( "subOne" );
        assertEquals( 2, properties.size() );
    }

    public void testDump()
        throws Exception
    {
        String dump = this.getRegistry().dump();
        assertNotNull( dump );
        System.out.println( "dump " + dump );
    }
}
