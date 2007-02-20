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

import org.codehaus.plexus.registry.test.AbstractRegistryTest;

import java.io.InputStream;

/**
 * @author <a href="mailto:Olivier.LAMY@accor.com">olamy</a>
 * @version $Id$
 * @since 8 feb. 07
 */
public class NamingRegistryTest
    extends AbstractRegistryTest
{

    // need a load-on-start for org.codehaus.plexus.naming.Naming
    protected InputStream getConfiguration()
        throws Exception
    {
        return getResourceAsStream( "/plexus.xml" );
    }

    protected InputStream getConfiguration( String arg0 )
        throws Exception
    {
        return this.getConfiguration();
    }

    protected String getCustomConfigurationName()
    {
        return getBasedir() + "/src/test/resources/plexus.xml";
    }

    protected String getConfigurationName( String arg0 )
        throws Exception
    {
        return this.getCustomConfigurationName();
    }

    public String getRoleHint()
    {
        return "naming";
    }

    public String getEmptyRoleHint()
    {
        return "empty-naming";
    }

}
